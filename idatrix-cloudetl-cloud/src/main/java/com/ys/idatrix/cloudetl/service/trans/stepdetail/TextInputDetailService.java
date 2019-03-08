/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.service.trans.stepdetail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.compress.CompressionInputStream;
import org.pentaho.di.core.compress.CompressionProvider;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.TextFileInputFieldInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.steps.file.BaseFileField;
import org.pentaho.di.trans.steps.fileinput.text.EncodingType;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputUtils;
import org.pentaho.di.www.CarteSingleton;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.dto.step.parts.AdditionalOutputFieldsDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputContentDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputErrorHandlingDto;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPTextFileInput;
import com.ys.idatrix.cloudetl.ext.CloudSession;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import net.sf.json.JSONObject;

/**
 * TextInput related Detail Service
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Service
public class TextInputDetailService implements StepDetailService {

	@Override
	public String getStepDetailType() {
		return "TextFileInput";
	}

	/**
	 * flag : getFields
	 * 
	 * @throws Exception
	 */
	@Override
	public List<Object> dealStepDetailByflag(String flag, Map<String, Object> param) throws Exception {
		if (StringUtils.isEmpty(flag)) {
			return null;
		}

		switch (flag) {
		case "getFields":
			return getTextFields(param);
		default:
			return null;

		}

	}

	/**
	 * @param inputFiles
	 *            content
	 * @return Text Fields list
	 * @throws Exception
	 */
	private List<Object> getTextFields(Map<String, Object> params) throws Exception {

		checkDetailParam(params, "fileName", "content");

		SPTextFileInput sptfiMeta = new SPTextFileInput();
		sptfiMeta.setInputFiles(Lists.newArrayList());
		sptfiMeta.setContent((TextFileInputContentDto) JSONObject.toBean(JSONObject.fromObject(params.get("content")),
				TextFileInputContentDto.class));
		sptfiMeta.setFields(Lists.newArrayList());
		sptfiMeta.setFilters(Lists.newArrayList());
		sptfiMeta.setAdditionalOutputFields(new AdditionalOutputFieldsDto());
		sptfiMeta.setErrorHandling(new TextFileInputErrorHandlingDto());
		
		String owner =  params.get("owner") == null || Utils.isEmpty( (String)params.get("owner"))  ? CloudSession.getResourceUser() : (String)params.get("owner");
		String fileName = params.get("fileName").toString();
		String filePath = FilePathUtil.getRealFileName(owner,fileName);

		if ("CSV".equalsIgnoreCase(sptfiMeta.getContent().getFileType())) {
			return getCSV(sptfiMeta,filePath);
		} else {
			return getFixed(sptfiMeta,filePath);
		}

	}

	// Get the data layout
	private List<Object> getCSV(SPTextFileInput sptfiMeta,String filePath) throws Exception {

		List<Object> result = null;

		TextFileInputMeta tfimeta = new TextFileInputMeta();
		sptfiMeta.decodeParameterObject(tfimeta, sptfiMeta, null, null);
		TextFileInputContentDto content = sptfiMeta.getContent();
		// CSV without separator defined
		if ("CSV".equalsIgnoreCase(content.getFileType())
				&& (content.getSeparator() == null || content.getSeparator().isEmpty())) {
			return null;
		}

		InputStream fileInputStream;
		CompressionInputStream inputStream = null;
		StringBuilder lineStringBuilder = new StringBuilder(256);
		int fileFormatType = tfimeta.getFileFormatTypeNr();

		String delimiter = content.getSeparator();
		String enclosure = content.getEnclosure();
		String escapeCharacter = content.getEscapeCharacter();

		if (filePath != null) {
			try {

				fileInputStream = KettleVFS.getInputStream(filePath);

				CompressionProvider provider = CompressionProviderFactory.getInstance()
						.createCompressionProviderInstance(content.getFileCompression());
				inputStream = provider.createInputStream(fileInputStream);

				InputStreamReader reader;
				if (content.getEncoding() != null && content.getEncoding().length() > 0) {
					reader = new InputStreamReader(inputStream, content.getEncoding());
				} else {
					reader = new InputStreamReader(inputStream);
				}
				EncodingType encodingType = EncodingType.guessEncodingType(reader.getEncoding());

				// Scan the header-line, determine fields...
				String line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
						fileFormatType, lineStringBuilder);
				if (line != null) {
					// Estimate the number of input fields...
					// Chop up the line using the delimiter
					String[] fields = TextFileInputUtils.guessStringsFromLine(null,CarteSingleton.getInstance().getLog(), line,
							tfimeta, delimiter, enclosure, escapeCharacter);
					result = Lists.newArrayList();
					for (int i = 0; i < fields.length; i++) {
						String field = fields[i];
						if (field == null || field.length() == 0 || !content.getHeader()) {
							field = "Field" + (i + 1);
						} else {
							// Trim the field
							field = Const.trim(field);
							// Replace all spaces & - with underscore _
							field = Const.replace(field, " ", "_");
							field = Const.replace(field, "-", "_");
						}

						result.add(new String[] { field, "String" });
					}
					return result;

				}
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e) {
					// Ignore errors
				}
			}
		}

		return null;

	}

	/**
	 * @param fileNameList
	 * @param content
	 * @throws Exception
	 */
	private List<Object> getFixed(SPTextFileInput sptfiMeta,String filePath) throws Exception {
		List<Object> result = null;

		TextFileInputMeta tfimeta = new TextFileInputMeta();
		sptfiMeta.decodeParameterObject(tfimeta, sptfiMeta, null, null);

		List<String> rows = getFirst(filePath,50, false, sptfiMeta, tfimeta);
		Vector<TextFileInputFieldInterface> fields = getFields(tfimeta, rows);

		result = Lists.newArrayList();
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("rows", rows);
		map.put("fields", fields);
		result.add(map);

		return result;

	}

	private List<String> getFirst(String filePath,int nrlines, boolean skipHeaders, SPTextFileInput sptfiMeta,
			TextFileInputMeta tfimeta) throws KettleException, Exception {

		InputStream fi;
		CompressionInputStream f = null;
		StringBuilder lineStringBuilder = new StringBuilder(256);
		int fileFormatType = tfimeta.getFileFormatTypeNr();

		List<String> retval = new ArrayList<>();

		if (filePath != null) {
			try {
				fi = KettleVFS.getInputStream(filePath);

				CompressionProvider provider = CompressionProviderFactory.getInstance()
						.createCompressionProviderInstance(sptfiMeta.getContent().getFileCompression());
				f = provider.createInputStream(fi);

				InputStreamReader reader;
				if (sptfiMeta.getContent().getEncoding() != null && sptfiMeta.getContent().getEncoding().length() > 0) {
					reader = new InputStreamReader(f, sptfiMeta.getContent().getEncoding());
				} else {
					reader = new InputStreamReader(f);
				}
				EncodingType encodingType = EncodingType.guessEncodingType(reader.getEncoding());

				int linenr = 0;
				int maxnr = nrlines
						+ (sptfiMeta.getContent().getHeader() ? sptfiMeta.getContent().getNrHeaderLines() : 0);

				if (skipHeaders) {
					// Skip the header lines first if more then one, it helps us
					// position
					if (sptfiMeta.getContent().getLayoutPaged() && sptfiMeta.getContent().getNrLinesDocHeader() > 0) {
						int skipped = 0;
						String line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
								fileFormatType, lineStringBuilder);
						while (line != null && skipped < sptfiMeta.getContent().getNrLinesDocHeader() - 1) {
							skipped++;
							line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
									fileFormatType, lineStringBuilder);
						}
					}

					// Skip the header lines first if more then one, it helps us
					// position
					if (sptfiMeta.getContent().getHeader() && sptfiMeta.getContent().getNrHeaderLines() > 0) {
						int skipped = 0;
						String line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
								fileFormatType, lineStringBuilder);
						while (line != null && skipped < sptfiMeta.getContent().getNrHeaderLines() - 1) {
							skipped++;
							line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
									fileFormatType, lineStringBuilder);
						}
					}
				}

				String line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType,
						fileFormatType, lineStringBuilder);
				while (line != null && (linenr < maxnr || nrlines == 0)) {
					retval.add(line);
					linenr++;
					line = TextFileInputUtils.getLine(CarteSingleton.getInstance().getLog(), reader, encodingType, fileFormatType,
							lineStringBuilder);
				}
			}  finally {
				try {
					if (f != null) {
						f.close();
					}
				} catch (Exception e) {
					// Ignore errors
				}
			}
		}

		return retval;
	}

	private Vector<TextFileInputFieldInterface> getFields(TextFileInputMeta info, List<String> rows) {
		Vector<TextFileInputFieldInterface> fields = new Vector<>();

		int maxsize = 0;
		for (String row : rows) {
			int len = row.length();
			if (len > maxsize) {
				maxsize = len;
			}
		}

		int prevEnd = 0;
		int dummynr = 1;

		for (int i = 0; i < info.inputFields.length; i++) {
			 BaseFileField f = info.inputFields[i];

			// See if positions are skipped, if this is the case, add dummy
			// fields...
			if (f.getPosition() != prevEnd) { // gap

				BaseFileField field = new BaseFileField("Dummy" + dummynr, prevEnd,
						f.getPosition() - prevEnd);
				field.setIgnored(true); // don't include in result by default.
				fields.add(field);
				dummynr++;
			}

			BaseFileField field = new BaseFileField(f.getName(), f.getPosition(), f.getLength());
			field.setType(f.getType());
			field.setIgnored(false);
			field.setFormat(f.getFormat());
			field.setPrecision(f.getPrecision());
			field.setTrimType(f.getTrimType());
			field.setDecimalSymbol(f.getDecimalSymbol());
			field.setGroupSymbol(f.getGroupSymbol());
			field.setCurrencySymbol(f.getCurrencySymbol());
			field.setRepeated(f.isRepeated());
			field.setNullString(f.getNullString());

			fields.add(field);

			prevEnd = field.getPosition() + field.getLength();
		}

		if (info.inputFields.length == 0) {
			BaseFileField field = new BaseFileField("Field1", 0, maxsize);
			fields.add(field);
		} else {
			// Take the last field and see if it reached until the maximum...
			BaseFileField f = info.inputFields[info.inputFields.length - 1];

			int pos = f.getPosition();
			int len = f.getLength();
			if (pos + len < maxsize) {
				// If not, add an extra trailing field!
				BaseFileField field = new BaseFileField("Dummy" + dummynr, pos + len, maxsize - pos - len);
				field.setIgnored(true); // don't include in result by default.
				fields.add(field);
			}
		}

		Collections.sort(fields);

		return fields;
	}

}
