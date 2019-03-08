/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.record;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ys.idatrix.cloudetl.ext.CloudApp;
import com.ys.idatrix.cloudetl.ext.utils.UnixPathUtil;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;
import com.ys.idatrix.cloudetl.logger.CloudLogType;

/**
 * AnalyzerHistory <br/>
 * @author JW
 * @since 2018年1月15日
 * 
 */
public class AnalyzerHistory {

	private static final String XML_TAG_ROOT = "records";
	private static final String XML_TAG_ELEM = "record";

	private static final int MAX_RECORD_NUMBER = 100;

	private String filepath;
	private CloudLogType filetype;

	private String filename;

	// History records, only load once-time
	List<AnalyzerRecorder> historyRecords;

	private AnalyzerHistory(String path, String name, CloudLogType type) throws Exception {
		this.filepath = UnixPathUtil.unixPath(CloudApp.getInstance().getRepositoryRootFolder() + path + type.getType() + CloudLogConst.SEPARATOR);
		this.filename = this.filepath + name + type.getExtension();
		this.filetype = type;

		try {
			loadAnalyzerHistory();
		} catch (KettleXMLException e) {
			historyRecords = new ArrayList<>();
		}
	}

	public static synchronized AnalyzerHistory initAnalyzerHistory(String path, String name, CloudLogType type) throws Exception {
		AnalyzerHistory history = new AnalyzerHistory(path, name, type);
		return history;
	}

	public String getXML() throws Exception {
		StringBuilder retval = new StringBuilder();

		retval.append(XMLHandler.openTag(XML_TAG_ROOT)).append(Const.CR);

		for (AnalyzerRecorder record : historyRecords) {
			retval.append("\t").append(XMLHandler.openTag(XML_TAG_ELEM)).append(Const.CR);
			retval.append("\t\t").append(XMLHandler.addTagValue("triggerId", record.getTriggerId()));
			retval.append("\t\t").append(XMLHandler.addTagValue("name", record.getMetaName()));
			retval.append("\t\t").append(XMLHandler.addTagValue("user", record.getUser()));

			if(record.getReporter() != null ) {
				retval.append("\t\t").append(XMLHandler.openTag("reporter")).append(Const.CR);
				retval.append("\t\t\t").append(XMLHandler.addTagValue("numberOfDataNodes", record.getReporter().getNumberOfDataNodes()));
				retval.append("\t\t\t").append(XMLHandler.addTagValue("numberOfRelationship", record.getReporter().getNumberOfRelationship()));
				retval.append("\t\t\t").append(XMLHandler.addTagValue("elapsedTime", record.getReporter().getElapsedTime()));
				retval.append("\t\t\t").append(XMLHandler.addTagValue("written", record.getReporter().isWritten()));
				retval.append("\t\t\t").append(XMLHandler.addTagValue("analyzerScore", record.getReporter().getAnalyzerScore()));
				retval.append("\t\t").append(XMLHandler.closeTag("reporter")).append(Const.CR);
			}

			retval.append("\t\t").append(XMLHandler.addTagValue("status", record.getStatus()));
			retval.append("\t\t").append(XMLHandler.addTagValue("beginDate", DateFormatUtils.format(record.getBeginDate(), CloudLogConst.EXEC_TIME_PATTERN)));
			retval.append("\t\t").append(XMLHandler.addTagValue("endDate", DateFormatUtils.format(record.getEndDate(), CloudLogConst.EXEC_TIME_PATTERN)));
			retval.append("\t\t").append(XMLHandler.addTagValue("logPath", record.getLogPath()));
			retval.append("\t").append(XMLHandler.closeTag(XML_TAG_ELEM)).append(Const.CR);
		}

		retval.append(XMLHandler.closeTag(XML_TAG_ROOT)).append(Const.CR);

		return retval.toString();
	}

	public void loadAnalyzerHistory() throws Exception {
		historyRecords = new ArrayList<>();

		Document doc = null;
		try {
			FileObject fileObject = KettleVFS.getFileObject(filename);
			if (fileObject.exists()) {
				doc = XMLHandler.loadXMLFile(fileObject);
			}
			fileObject.close();
		} catch (KettleXMLException | KettleFileException | FileSystemException e) {
			throw new Exception("Failed to load analyzer history file", e);
		}

		if (doc != null) {
			// Root node:
			Node rootnode = XMLHandler.getSubNode(doc, XML_TAG_ROOT);
			if (rootnode == null) {
				throw new Exception("Invalid format of " + filename + ", no root element!");
			}

			// Element nodes:
			int n = XMLHandler.countNodes(rootnode, XML_TAG_ELEM);
			for (int i = 0; i < n; i++) {
				Node elemnode = XMLHandler.getSubNodeByNr(rootnode, XML_TAG_ELEM, i);

				AnalyzerRecorder record = new AnalyzerRecorder();
				record.setTriggerId(XMLHandler.getTagValue(elemnode, "triggerId"));
				record.setMetaName(XMLHandler.getTagValue(elemnode, "name"));
				record.setUser(XMLHandler.getTagValue(elemnode, "user"));

				Node reporterNode = XMLHandler.getSubNode(elemnode, "reporter");
				if (reporterNode != null) {
					AnalyzerReporter reporter=  new AnalyzerReporter();

					//String numberOfDataNodes = XMLHandler.getTagValue(reporterNode, "numberOfDataNodes");
					//String numberOfRelationship = XMLHandler.getTagValue(reporterNode, "numberOfRelationship");

					String analyzerScore = XMLHandler.getTagValue(reporterNode, "analyzerScore");
					if (!Utils.isEmpty(analyzerScore)) {
						reporter.setAnalyzerScore(Long.parseLong(analyzerScore));
					}

					String elapsedTime = XMLHandler.getTagValue(reporterNode, "elapsedTime");
					if (!Utils.isEmpty(elapsedTime)) {
						reporter.setElapsedTime(Long.parseLong(elapsedTime));
					}

					String written = XMLHandler.getTagValue(reporterNode, "written");
					if (!Utils.isEmpty(written)) {
						reporter.setWritten(Boolean.parseBoolean(written));
					}

					record.setReporter(reporter);
				}

				record.setStatus(XMLHandler.getTagValue(elemnode, "status"));
				record.setBeginDate(DateUtils.parseDate(XMLHandler.getTagValue(elemnode, "beginDate"), CloudLogConst.EXEC_TIME_PATTERN));
				record.setEndDate(DateUtils.parseDate(XMLHandler.getTagValue(elemnode, "endDate"), CloudLogConst.EXEC_TIME_PATTERN));
				record.setLogPath(XMLHandler.getTagValue(elemnode, "logPath"));

				historyRecords.add(record);
			}
		}
	}

	public AnalyzerRecorder getAnalyzerRecord(String id) {
		return historyRecords.stream().filter(record -> (id.equals(record.getTriggerId()))).findFirst().get();
	}

	public AnalyzerRecorder getLastAnalyzerRecord() {
		if (historyRecords.size() > 0) {
			return historyRecords.get(0);
		}
		return null;
	}

	public List<AnalyzerRecorder> getAnalyzerRecords() {
		return historyRecords;
	}

	public void insertAnalyzerRecord(AnalyzerRecorder record) {
		historyRecords.add(0, record);
		if (historyRecords.size() >= MAX_RECORD_NUMBER) {
			historyRecords = historyRecords.subList(0, MAX_RECORD_NUMBER);
		}
	}

	public void deleteAnalyzerRecord(String id) {
		historyRecords.removeIf(record -> (id.equals(record.getTriggerId())));
	}

	public void createAnalyzerHistory() throws Exception {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (!fileObject.exists()) {
			fileObject.createFile();
		}
		fileObject.close();
	}

	public void renameAnalyzerHistory(String newname) throws Exception {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			FileObject newObject = KettleVFS.getFileObject(UnixPathUtil.unixPath(filepath) + newname +  filetype.getExtension());
			fileObject.moveTo(newObject); // JW: maybe failed !
			newObject.close();
		}
		fileObject.close();
	}

	public void saveAnalyzerHistory() throws Exception {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (!fileObject.exists()) {
			fileObject.createFile();
		}

		OutputStream os = KettleVFS.getOutputStream(fileObject, false);
		os.write(this.getXML().getBytes(Const.XML_ENCODING));
		os.close();
		fileObject.close();
	}

	public void deleteAnalyzerHistory() throws Exception {
		historyRecords.clear();
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			fileObject.delete();
		}
		fileObject.close();
	}

}
