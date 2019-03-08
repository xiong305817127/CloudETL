package com.ys.idatrix.cloudetl.readcontent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.apache.poi.hslf.model.Comment;
import org.apache.poi.hslf.model.HeadersFooters;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTable;
import org.apache.poi.hslf.usermodel.HSLFTableCell;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xslf.usermodel.DrawingParagraph;
import org.apache.poi.xslf.usermodel.DrawingTextBody;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFCommentAuthors;
import org.apache.poi.xslf.usermodel.XSLFComments;
import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFSlideLayout;
import org.apache.poi.xslf.usermodel.XSLFSlideMaster;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTComment;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentAuthor;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KCellType;
import org.pentaho.di.core.spreadsheet.KSheet;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.pentaho.di.trans.steps.excelinput.WorkbookFactory;

import com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta.TYPE_CODE;

public class ReadContentInput extends BaseStep implements StepInterface {

	private ReadContentInputMeta meta;
	private ReadContentInputData data;

	public ReadContentInput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (ReadContentInputMeta) smi;
		data = (ReadContentInputData) sdi;

		if (super.init(smi, sdi)) {

			data.files = meta.getFileList(this);
			if (data.files.nrOfFiles() == 0 && data.files.nrOfMissingFiles() > 0 && !meta.isAcceptingFilenames()) {
				logError("未找到输入文件.");
				return false;
			}
			data.isNextFile = true;
			data.isFileRow = meta.isPageRow();

			return true;
		}
		return false;

	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (ReadContentInputMeta) smi;
		data = (ReadContentInputData) sdi;

		if (first) {
			first = false;

			data.outputRowMeta = new RowMeta(); // start from scratch!
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);

			if (meta.isAcceptingFilenames()) {
				// Read the files from the specified input stream...
				data.files.getFiles().clear();

				int idx = -1;
				RowSet rowSet = findInputRowSet(meta.getAcceptingStepName());
				Object[] fileRow = getRowFrom(rowSet);
				while (fileRow != null) {
					if (idx < 0) {
						idx = rowSet.getRowMeta().indexOfValue(meta.getAcceptingField());
						if (idx < 0) {
							logError(
									"从步骤[" + meta.getAcceptingStepName() + "]中未找到域[" + meta.getAcceptingField() + "].");
							setErrors(1);
							stopAll();
							return false;
						}
					}
					String fileValue = rowSet.getRowMeta().getString(fileRow, idx);
					try {
						data.files.addFile(KettleVFS.getFileObject(fileValue, getTransMeta()));
					} catch (KettleFileException e) {
						throw new KettleException("文件对象[" + fileValue + "]创建失败.", e);
					}
					// Grab another row
					fileRow = getRowFrom(rowSet);
				}
			}

			handleMissingFiles();
		}

		// See if we're not done processing...
		// We are done processing if the filenr >= number of files.
		if (data.filenr >= data.files.nrOfFiles()) {
			if (log.isDetailed()) {
				logDetailed("已完成最后一个文件, 文件数:" + data.filenr);
			}

			setOutputDone(); // signal end to receiver(s)
			return false; // end of data or error.
		}

		try {

			Object[] r = getRowFromFile();
			if (r != null) {
				incrementLinesInput();

				// Send out the good news: we found a row of data!
				putRow(data.outputRowMeta, r);

				return true;
			} else {
				// 数据行内容为空,不处理
				return true;
			}
		} catch (Exception e) {
			logError("处理文件行失败:文件:" + data.filename + ",异常:" + e.toString(), e);

			if (meta.isIgnoreError()) {
				logBasic("忽略处理异常,继续执行...");
				
				data.isNextFile = true ;
				// Reset the start-row:
				data.rownr = 0;
				// advance to the next file!
				data.filenr++;
				
				return true;
			} else {

				setErrors(1);
				stopAll();
				return false;
			}
		}

	}

	private Object[] getRowFromFile() throws Exception {

		Object[] retval = new Object[data.outputRowMeta.size()];

		if (data.isNextFile) {
			// Open a new openFile..
			data.file = data.files.getFile(data.filenr);
			data.filename = KettleVFS.getFilename(data.file);

			if (meta.isAddResultFile()) {
				ResultFile resultFile = new ResultFile(ResultFile.FILE_TYPE_GENERAL, data.file,
						getTransMeta().getName(), toString());
				resultFile.setComment("cong readContent 步骤读取内容.");
				addResultFile(resultFile);
			}

			if(Utils.isEmpty( meta.getType() )) {
				if( data.filename.endsWith("doc") || data.filename.endsWith("DOC") || data.filename.endsWith("docx") || data.filename.endsWith("DOCX") ) {
					data.type = TYPE_CODE.word ;
				}else if( data.filename.endsWith("pdf") || data.filename.endsWith("PDF") ) {
					data.type = TYPE_CODE.pdf ;
				}else if( data.filename.endsWith("xls") || data.filename.endsWith("XLS") || data.filename.endsWith("xlsx") || data.filename.endsWith("XLSX") ) {
					data.type = TYPE_CODE.excel ;
				}else if( data.filename.endsWith("ppt") || data.filename.endsWith("PPT") || data.filename.endsWith("pptx") || data.filename.endsWith("PPTX") ) {
					data.type = TYPE_CODE.ppt ;
				}else {
					data.type = TYPE_CODE.text ;
				}
			}else {
				data.type = TYPE_CODE.valueOf(meta.getType());
			}
			
			if (log.isDetailed()) {
				logDetailed("开始读取文件 ,序号:" + data.filenr + " 文件名: " + data.filename);
			}

			switch (data.type) {
			case word:
				if (data.isFileRow) {
					if (data.filename.endsWith("doc") || data.filename.endsWith("DOC")) {
						try {
							data.wordDoc = new HWPFDocument(KettleVFS.getInputStream(data.file));
							data.sectionNumber = data.wordDoc.getRange().numSections();
						}catch( OfficeXmlFileException e ) {
							//尝试使用 docx方式
							OPCPackage opcPackage = OPCPackage.open(KettleVFS.getInputStream(data.file));
							data.wordExtractor = new XWPFWordExtractor(opcPackage);
							data.wordDocx = (XWPFDocument) ((XWPFWordExtractor) data.wordExtractor).getDocument();// new
																													// XWPFDocument(opcPackage);
							data.sectionNumber = data.wordDocx.getBodyElements().size();
						}
					} else if (data.filename.endsWith("docx") || data.filename.endsWith("DOCX")) {
						OPCPackage opcPackage = OPCPackage.open(KettleVFS.getInputStream(data.file));
						data.wordExtractor = new XWPFWordExtractor(opcPackage);
						data.wordDocx = (XWPFDocument) ((XWPFWordExtractor) data.wordExtractor).getDocument();// new
																												// XWPFDocument(opcPackage);
						data.sectionNumber = data.wordDocx.getBodyElements().size();
					} else {
						logError("word 文件类型不支持,只支持[.doc,.docx].");
						throw new KettleException("word 文件类型不支持,只支持[.doc,.docx].");
					}

				} else {
					if (data.filename.endsWith("doc") || data.filename.endsWith("DOC")) {
						try {
							data.wordExtractor = new WordExtractor(KettleVFS.getInputStream(data.file));
						}catch( OfficeXmlFileException e ) {
							//尝试使用 docx方式
							OPCPackage opcPackage = OPCPackage.open(KettleVFS.getInputStream(data.file));
							data.wordExtractor = new XWPFWordExtractor(opcPackage);
						}
					} else if (data.filename.endsWith("docx") || data.filename.endsWith("DOCX")) {
						OPCPackage opcPackage = OPCPackage.open(KettleVFS.getInputStream(data.file));
						data.wordExtractor = new XWPFWordExtractor(opcPackage);
					} else {
						logError("word 文件类型不支持,只支持[.doc,.docx].");
						throw new KettleException("word 文件类型不支持,只支持[.doc,.docx].");
					}
				}
				break;
			case pdf:
				data.pdfDocument = PDDocument.load(KettleVFS.getInputStream(data.file));
				data.pdfts = new PDFTextStripper();
				data.pageNumber = data.pdfDocument.getNumberOfPages();
				break;
			case text:
				if (data.isFileRow) {
					data.textReader = new LineNumberReader(
							new InputStreamReader(KettleVFS.getInputStream(data.file), meta.getEncoding()));
				}
				break;
			case excel:

				SpreadSheetType spread = SpreadSheetType.POI;
				if (data.filename.endsWith("xlsx") || data.filename.endsWith("XLSX")) {
					double lengthM = data.file.getContent().getSize() / 1000000;
					if (lengthM > 3) {
						// 大于4兆
						spread = SpreadSheetType.SAX_POI;
					}
				}
				
				try {
					data.workbook = WorkbookFactory.getWorkbook(spread, KettleVFS.getInputStream(data.file), meta.getEncoding());
				}catch( KettleException e ) {
					if( spread == SpreadSheetType.POI ) {
						data.workbook = WorkbookFactory.getWorkbook( SpreadSheetType.SAX_POI, KettleVFS.getInputStream(data.file), meta.getEncoding());
					}else if( spread == SpreadSheetType.SAX_POI ){
						data.workbook = WorkbookFactory.getWorkbook( SpreadSheetType.POI, KettleVFS.getInputStream(data.file), meta.getEncoding());
					}
				}
				
				// See if we have sheet names to retrieve, otherwise we'll have to get all
				data.sheetNames = data.workbook.getSheetNames();
				// Start at the first sheet again...
				data.sheetnr = 0;
				break;
			case ppt:
				if (data.isFileRow) {
					if (data.filename.endsWith("ppt") || data.filename.endsWith("PPT")) {
						try {
							data.slide = new HSLFSlideShow(KettleVFS.getInputStream(data.file));
						}catch( OfficeXmlFileException e ) {
							data.slide = new XMLSlideShow(KettleVFS.getInputStream(data.file));
						}
					} else if (data.filename.endsWith("pptx") || data.filename.endsWith("PPTX")) {
						data.slide = new XMLSlideShow(KettleVFS.getInputStream(data.file));
					} else {
						logError("word 文件类型不支持,只支持[.ppt,.pptx].");
						throw new KettleException("word 文件类型不支持,只支持[.ppt,.pptx].");
					}
					data.slidenr = data.slide.getSlides().size();
				} else {
					if (data.filename.endsWith("ppt") || data.filename.endsWith("PPT")) {
						try {
							data.pptExtractor = new PowerPointExtractor(KettleVFS.getInputStream(data.file));
						}catch( OfficeXmlFileException e ) {
							data.pptExtractor = new XSLFPowerPointExtractor( new XMLSlideShow(KettleVFS.getInputStream(data.file)));
						}
					} else if (data.filename.endsWith("pptx") || data.filename.endsWith("PPTX")) {
						data.pptExtractor = new XSLFPowerPointExtractor( new XMLSlideShow(KettleVFS.getInputStream(data.file)));
					} else {
						logError("word 文件类型不支持,只支持[.ppt,.pptx].");
						throw new KettleException("word 文件类型不支持,只支持[.ppt,.pptx].");
					}
				}
				break;
			}
			data.isNextFile = false;
		}

		if (meta.isIncludeFileName() && !Utils.isEmpty(meta.getFileNameFieldName())) {
			if(meta.isIncludeOnlyFileName()) {
				retval[1] = data.file.getName().getBaseName();
			}else {
				retval[1] = data.filename;
			}
			
		}

		String content = null;

		if (data.isFileRow) {
			switch (data.type) {
			case word:
				content = getRowFromWord();
				break;
			case pdf:
				content = getRowFromPdf();
				break;
			case text:
				content = getRowFromText();
				break;
			case excel:
				content = getRowFromExcel();
				break;
			case ppt:
				content = getRowFromPpt();
				break;
			}
		} else {
			switch (data.type) {
			case word:
				content = getContentFromWord();
				break;
			case pdf:
				content = getContentFromPdf();
				break;
			case text:
				content = getContentFromText();
				break;
			case excel:
				content = getContentFromExcel();
				break;
			case ppt:
				content = getContentFromPpt();
				break;
			}
		}

		if (!Utils.isEmpty(content)) {
			if(meta.isOnlyVisible()) {
				StringBuffer contentBuffer = new StringBuffer();
				int sz = content.length();
				for (int i = 0; i < sz; i++) {
					char c = content.charAt(i);
					if (isPrintableChinese(c)) {
						contentBuffer.append(c);
					}
				}
				retval[0] = contentBuffer.toString();
			}else {
				retval[0] = content;
			}
		} else {
			retval = null;
		}

		// Perhaps it was the last sheet?
		if (data.isNextFile) {
			// Reset the start-row:
			data.rownr = 0;
			// advance to the next file!
			data.filenr++;
		}

		return retval;

	}

	public String getContentFromWord() throws Exception {
		if (log.isDebug()) {
			logDetailed("开始读取word 数据:文件序号:" + data.filenr);
		}
		// 开始提取页数
		String content = data.wordExtractor.getText();

		if (log.isRowLevel()) {
			logRowlevel("获取到word数据: " + content);
		}

		data.wordExtractor.close();
		data.wordExtractor = null;

		data.isNextFile = true;
		data.rownr = 0;

		return content != null ? content.trim() : content;
	}

	public String getRowFromWord() throws Exception {

		if (log.isDebug()) {
			logDetailed("开始读取word 小节数据:文件序号:" + data.filenr);
		}
		String content = null;
		// 开始提取页数
		if (data.wordDoc != null) {
			Section section = data.wordDoc.getRange().getSection(data.rownr);
			content = section.text();
		} else if (data.wordDocx != null) {
			StringBuffer text = new StringBuffer();
			XWPFWordExtractor extractor = (XWPFWordExtractor) data.wordExtractor;
			IBodyElement ele = data.wordDocx.getBodyElements().get(data.rownr);
			extractor.appendBodyElementText(text, ele);
			content = text.toString();
		}

		int lineNr = ++data.rownr;

		if (log.isRowLevel()) {
			logRowlevel("获取到小节数据: 小节号:" + lineNr + " 内容:" + content);
		}

		if (data.rownr >= data.sectionNumber) {

			if (data.wordDoc != null) {
				data.wordDoc.close();
				data.wordDoc = null;
			}
			if (data.wordDocx != null) {
				data.wordDocx.close();
				data.wordDocx = null;
			}
			data.sectionNumber = 0;

			data.isNextFile = true;
			data.rownr = 0;
		}

		return content != null ? content.trim() : content;

	}

	public String getContentFromPdf() throws Exception {

		if (log.isDebug()) {
			logDetailed("开始读取PDF 数据:文件序号:" + data.filenr);
		}
		// 开始提取页数
		data.pdfts.setStartPage(1);
		data.pdfts.setEndPage(data.pageNumber);
		String content = data.pdfts.getText(data.pdfDocument);

		if (log.isRowLevel()) {
			logRowlevel("获取到PDF数据: " + content);
		}

		data.pdfDocument.close();
		data.pdfDocument = null;
		data.pdfts = null;
		data.pageNumber = 0;

		data.isNextFile = true;
		data.rownr = 0;

		return content != null ? content.trim() : content;
	}

	public String getRowFromPdf() throws Exception {
		// What sheet were we handling?
		if (log.isDebug()) {
			logDetailed("开始读取 PDF 页数据:文件序号:" + data.filenr);
		}
		// 开始提取页数
		data.pdfts.setStartPage(data.rownr + 1);
		data.pdfts.setEndPage(data.rownr + 1);

		String content = data.pdfts.getText(data.pdfDocument);
		int lineNr = ++data.rownr;

		if (log.isRowLevel()) {
			logRowlevel("获取到页数据: 页号:" + lineNr + " 内容:" + content);
		}

		if (data.rownr >= data.pageNumber) {

			data.pdfDocument.close();
			data.pdfDocument = null;
			data.pdfts = null;
			data.pageNumber = 0;

			data.isNextFile = true;
			data.rownr = 0;
		}

		return content != null ? content.trim() : content;
	}

	public String getContentFromText() throws Exception {

		if (log.isDebug()) {
			logDetailed("开始读取TEXT 数据:文件序号:" + data.filenr);
		}

		int length = ((Long) data.file.getContent().getSize()).intValue();
		byte[] filecontent = new byte[length];
		InputStream in = data.file.getContent().getInputStream();
		in.read(filecontent);
		in.close();
		String content = new String(filecontent, meta.getEncoding());

		if (log.isRowLevel()) {
			logRowlevel("获取到PDF数据: " + content);
		}

		if (data.textReader != null) {
			data.textReader.close();
			data.textReader = null;
		}

		data.isNextFile = true;
		data.rownr = 0;

		return content != null ? content.trim() : content;
	}

	public String getRowFromText() throws Exception {
		// What sheet were we handling?
		if (log.isDebug()) {
			logDetailed("开始读取 TEXT 行数据:文件序号:" + data.filenr);
		}

		String content = data.textReader.readLine();
		int lineNr = ++data.rownr;

		if (log.isRowLevel()) {
			logRowlevel("获取到行数据: 行号:" + lineNr + " 内容:" + content);
		}
		if (content == null) {

			data.textReader.close();
			data.textReader = null;

			data.isNextFile = true;
			data.rownr = 0;
		} else {
			content = content.trim();
		}

		return content;

	}

	public String getContentFromExcel() throws Exception {

		if (log.isDebug()) {
			logDetailed("开始读取Excel 数据:文件序号:" + data.filenr);
		}

		StringBuilder sb = new StringBuilder();
		int curSheet = data.sheetnr;
		while (curSheet == data.sheetnr && data.workbook != null) {
			String rowContent = getRowFromExcel();
			if (!Utils.isEmpty(rowContent)) {
				sb.append(rowContent);
				if (!rowContent.endsWith(Const.CR)) {
					sb.append(Const.CR);
				}
			}
		}

		if (log.isRowLevel()) {
			logRowlevel("获取到 Excel数据:" + sb.toString());
		}

		return sb.toString().trim();
	}

	public String getRowFromExcel() throws Exception {

		// What sheet were we handling?
		if (log.isDebug()) {
			logDetailed("开始读取 Excel 行数据:文件序号:" + data.filenr + ",sheet序号:" + data.sheetnr);
		}

		StringBuilder sb = new StringBuilder();
		boolean nextsheet = false;

		String sheetName = data.sheetNames[data.sheetnr];
		KSheet sheet = data.workbook.getSheet(sheetName);
		if (sheet != null) {
			// at what row do we continue reading?
			if (data.rownr < 0) {
				data.rownr = 0;
			}
			// Build a new row and fill in the data from the sheet...
			try {
				KCell[] cells = sheet.getRow(data.rownr);
				// Already increase cursor 1 row
				int lineNr = ++data.rownr;
				// Excel starts counting at 0
				if (log.isRowLevel()) {
					logRowlevel("读取Excel 行内容,行号:" + lineNr + " 文件序号.sheet序号:" + data.filenr + "." + data.sheetnr);
				}

				// Set values in the row...
				for (int i = 0; i < cells.length; i++) {
					KCell cell = cells[i];
					if (cell == null) {
						sb.append("	");
						continue;
					}

					KCellType cellType = cell.getType();
					if (KCellType.BOOLEAN == cellType || KCellType.BOOLEAN_FORMULA == cellType) {
						sb.append(cell.getValue()).append("	");
					} else {
						if (KCellType.DATE.equals(cellType) || KCellType.DATE_FORMULA.equals(cellType)) {
							Date date = (Date) cell.getValue();
							long time = date.getTime();
							int offset = TimeZone.getDefault().getOffset(time);
							sb.append(new ValueMetaDate().getString(new Date(time - offset))).append("	");
						} else {
							if (KCellType.LABEL == cellType || KCellType.STRING_FORMULA == cellType) {
								// String string = (String) cell.getValue();
								sb.append(cell.getValue()).append("	");
							} else {
								if (KCellType.NUMBER == cellType || KCellType.NUMBER_FORMULA == cellType) {
									sb.append(cell.getValue()).append("	");
								} else if (KCellType.EMPTY == cellType) {
									sb.append("	");
									continue;
								} else {
									if (log.isDetailed()) {
										KCellType ct = cell.getType();
										logDetailed("不知道的类型:" + ((ct != null) ? ct.toString() : "null"),
												cell.getContents());
									}
									String c = cell.getContents();
									if (!Utils.isEmpty(c)) {
										sb.append(c).append("	");
									}
								}
							}
						}
					}

				}
				if (log.isRowLevel()) {
					logRowlevel("获取到行数据: 行号:" + lineNr + " 内容:" + sb.toString());
				}

				if (data.rownr > sheet.getRows()) {
					nextsheet = true;
				}

			} catch (ArrayIndexOutOfBoundsException e) {
				if (log.isRowLevel()) {
					logRowlevel("异常:数组超界,进入下一个sheet.");
				}
				// We tried to read below the last line in the sheet.
				// Go to the next sheet...
				nextsheet = true;
			}
		} else {
			nextsheet = true;
		}

		if (nextsheet) {
			// Go to the next sheet
			data.sheetnr++;

			// Reset the start-row:
			data.rownr = -1;

			// Perhaps it was the last sheet?
			if (data.sheetnr >= data.sheetNames.length) {

				data.sheetnr = 0;
				// Close the openFile!
				data.workbook.close();
				data.workbook = null; // marker to open again.
				data.isNextFile = true;
			}
		}

		return sb.toString().trim();
	}

	private String getContentFromPpt() throws Exception {

		if (log.isDebug()) {
			logDetailed("开始读取PPT 数据:文件序号:" + data.filenr);
		}

		String content = data.pptExtractor.getText();
		if (log.isRowLevel()) {
			logRowlevel("获取到PPT数据: " + content);
		}

		data.pptExtractor.close();
		data.pptExtractor = null;
		if (data.slide != null) {
			data.slide.close();
			data.slide = null;
		}
		data.slidenr = 0;

		data.isNextFile = true;
		data.rownr = 0;

		return content != null ? content.trim() : content;
	}

	private String getRowFromPpt() throws Exception {

		// What sheet were we handling?
		if (log.isDebug()) {
			logDetailed("开始读取ppt 页数据:文件序号:" + data.filenr);
		}
		StringBuilder ret = new StringBuilder();
		// 开始提取页数
		if (data.slide instanceof HSLFSlideShow) {
			// ppt
			HSLFSlideShow slideShow = (HSLFSlideShow) data.slide;
			HSLFSlide slide = slideShow.getSlides().get(data.rownr);

			HeadersFooters hf = slide.getHeadersFooters();
			String headerText = null;
			String footerText = null;
			if (hf != null) {
				if (hf.isHeaderVisible()) {
					headerText = Const.NVL(hf.getHeaderText(), "");
				}
				if (hf.isFooterVisible()) {
					footerText = Const.NVL(hf.getFooterText(), "");
				}
			}
			// Slide header, if set
			if (!Utils.isEmpty(headerText)) {
				ret.append(headerText).append(Const.CR);
			}

			// Slide text
			if (slide.getTextParagraphs() != null) {
				for (List<HSLFTextParagraph> lp : slide.getTextParagraphs()) {
					ret.append(HSLFTextParagraph.getText(lp));
					if (ret.length() > 0 && ret.charAt(ret.length() - 1) != '\n') {
						ret.append('\n');
					}
				}
			}

			// Table text
			if (slide.getShapes() != null) {
				for (HSLFShape shape : slide.getShapes()) {
					if (shape instanceof HSLFTable) {
						HSLFTable table = (HSLFTable) shape;
						final int nrows = table.getNumberOfRows();
						final int ncols = table.getNumberOfColumns();
						for (int row = 0; row < nrows; row++) {
							for (int col = 0; col < ncols; col++) {
								HSLFTableCell cell = table.getCell(row, col);
								// defensive null checks; don't know if they're necessary
								if (cell != null) {
									String txt = cell.getText();
									txt = (txt == null) ? "" : txt;
									ret.append(txt);
									if (col < ncols - 1) {
										ret.append('\t');
									}
								}
							}
							ret.append('\n');
						}
					}
				}
			}

			// Slide footer, if set
			if (!Utils.isEmpty(footerText)) {
				ret.append(footerText).append(Const.CR);
			}

			// Comments, if requested and present
			if (slide.getComments() != null) {
				for (Comment comment : slide.getComments()) {
					ret.append(comment.getAuthor() + " - " + comment.getText() + "\n");
				}
			}

		} else if (data.slide instanceof XMLSlideShow) {
			// pptx
			XMLSlideShow slideShow = (XMLSlideShow) data.slide;
			XSLFSlide slide = slideShow.getSlides().get(data.rownr);

			XSLFCommentAuthors commentAuthors = slideShow.getCommentAuthors();
			XSLFNotes notes = slide.getNotes();
			XSLFComments comments = slide.getComments();
			XSLFSlideLayout layout = slide.getSlideLayout();
			XSLFSlideMaster master = layout.getSlideMaster();

			// TODO Do the slide's name
			// (Stored in docProps/app.xml)

			// Do the slide's text if requested
			if (slide.getCommonSlideData() != null) {
				for (DrawingTextBody textBody : slide.getCommonSlideData().getDrawingText()) {
					for (DrawingParagraph p : textBody.getParagraphs()) {
						ret.append(p.getText());
						ret.append("\n");
					}
				}
			}

			// If requested, get text from the master and it's layout
			if (layout != null && layout.getCommonSlideData() != null) {
				for (DrawingTextBody textBody : layout.getCommonSlideData().getDrawingText()) {
					for (DrawingParagraph p : textBody.getParagraphs()) {
						ret.append(p.getText());
						ret.append("\n");
					}
				}
			}
			if (master != null && master.getCommonSlideData() != null) {
				for (DrawingTextBody textBody : master.getCommonSlideData().getDrawingText()) {
					for (DrawingParagraph p : textBody.getParagraphs()) {
						ret.append(p.getText());
						ret.append("\n");
					}
				}
			}

			// If the slide has comments, do those too
			if (comments != null && comments.getCTCommentsList() != null) {
				for (CTComment comment : comments.getCTCommentsList().getCmArray()) {
					// Do the author if we can
					if (commentAuthors != null) {
						CTCommentAuthor author = commentAuthors.getAuthorById(comment.getAuthorId());
						if (author != null) {
							ret.append(author.getName() + ": ");
						}
					}

					// Then the comment text, with a new line afterwards
					ret.append(comment.getText());
					ret.append("\n");
				}
			}

			// Do the notes if requested
			if (notes != null && notes.getCommonSlideData() != null) {
				for (DrawingTextBody textBody : notes.getCommonSlideData().getDrawingText()) {
					for (DrawingParagraph p : textBody.getParagraphs()) {
						ret.append(p.getText());
						ret.append("\n");
					}
				}
			}

		}
		int lineNr = ++data.rownr;

		if (log.isRowLevel()) {
			logRowlevel("获取到页数据: 页号:" + lineNr + " 内容:" + ret.toString());
		}

		if (data.rownr >= data.slidenr) {

			if (data.pptExtractor != null) {
				data.pptExtractor.close();
				data.pptExtractor = null;
			}
			if (data.slide != null) {
				data.slide.close();
				data.slide = null;
			}

			data.slidenr = 0;

			data.isNextFile = true;
			data.rownr = 0;
		}

		return ret.toString().trim();
	}

	private void handleMissingFiles() throws KettleException {
		List<FileObject> nonExistantFiles = data.files.getNonExistantFiles();

		if (nonExistantFiles.size() != 0) {
			String message = FileInputList.getRequiredFilesDescription(nonExistantFiles);
			if (log.isBasic()) {
				logBasic("必须要的文件不存在:" + message);
			}
			// if ( meta.isErrorIgnored() ) {
			// for ( FileObject fileObject : nonExistantFiles ) {
			// data.errorHandler.handleNonExistantFile( fileObject );
			// }
			// } else {
			throw new KettleException("必须要的文件不存在:" + message);
			// }
		}

		List<FileObject> nonAccessibleFiles = data.files.getNonAccessibleFiles();
		if (nonAccessibleFiles.size() != 0) {
			String message = FileInputList.getRequiredFilesDescription(nonAccessibleFiles);
			if (log.isBasic()) {
				logBasic("文件没有使用权限:" + message);
			}

			// if ( meta.isErrorIgnored() ) {
			// for ( FileObject fileObject : nonAccessibleFiles ) {
			// data.errorHandler.handleNonAccessibleFile( fileObject );
			// }
			// } else {
			throw new KettleException("文件没有使用权限:" + message);
			// }

		}
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (ReadContentInputMeta) smi;
		data = (ReadContentInputData) sdi;

		try {
			if (data.files != null && data.files.getFiles().size() > 0) {
				for (FileObject file : data.files.getFiles()) {
					file.close();
				}
				data.files.getFiles().clear();
				data.files = null;
			}
			if (data.file != null) {
				data.file.close();
				data.file = null;
			}

			if (data.wordDoc != null) {
				data.wordDoc.close();
				data.wordDoc = null;
			}
			if (data.wordDocx != null) {
				data.wordDocx.close();
				data.wordDocx = null;
			}
			if (data.wordExtractor != null) {
				data.wordExtractor.close();
				data.wordExtractor = null;
			}
			if (data.pdfDocument != null) {
				data.pdfDocument.close();
				data.pdfDocument = null;
				data.pdfts = null;
			}
			if (data.textReader != null) {
				data.textReader.close();
				data.textReader = null;
			}
			if (data.workbook != null) {
				data.workbook.close();
				data.workbook = null;
			}

			if (data.pptExtractor != null) {
				data.pptExtractor.close();
				data.pptExtractor = null;
			}
			if (data.slide != null) {
				data.slide.close();
				data.slide = null;
			}
		} catch (Exception e) {
			// Ignore close errors
		}

		super.dispose(smi, sdi);
	}
	
	public boolean isPrintableChinese(char c) {  
		if(CharUtils.isAsciiPrintable(c) ) {
			return true ;
		}
		
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
            return true;  
        }  
        return false;  
    }
	
}
