package org.pentaho.di.core.logging;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;

import org.pentaho.di.cluster.HttpUtil;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;
import org.w3c.dom.Node;

public class SegmentingPartInfo implements Serializable {
	
	private static final long serialVersionUID = -2157067310254466534L;

	public static final String XML_TAGS = "parts";
	public static final String XML_TAG = "Part";

	private String id;
	private String begin;
	private String end;
	private String status;

	private StringBuilder log;

	private Long inputLines = 0L;
	private Long outputLines = 0L;
	private Long readLines = 0L;
	private Long writeLines = 0L;
	private Long updateLines = 0L;
	private Long errorLines = 0L;
	
	private boolean isEnable = true ;
	
	private String exceptionPosition ;
	private String exceptionDetail ;
	private String exceptionName ;
	private String exceptionType ;
	private String dataInputSource ;
	private String dataOutputSource ;


	public SegmentingPartInfo() {
		super();
		this.id = UUID.randomUUID().toString().replaceAll("-", "");
		this.log = new StringBuilder();
	}

	public SegmentingPartInfo(Node partNode) {
		this();
		id = XMLHandler.getTagValue(partNode, "id");
		begin = XMLHandler.getTagValue(partNode, "begin");
		end = XMLHandler.getTagValue(partNode, "end");
		status = XMLHandler.getTagValue(partNode, "status");
		inputLines = Long.valueOf(XMLHandler.getTagValue(partNode, "inputLines"));
		outputLines = Long.valueOf(XMLHandler.getTagValue(partNode, "outputLines"));
		readLines = Long.valueOf(XMLHandler.getTagValue(partNode, "readLines"));
		writeLines = Long.valueOf(XMLHandler.getTagValue(partNode, "writeLines"));
		updateLines = Long.valueOf(XMLHandler.getTagValue(partNode, "updateLines"));
		errorLines = Long.valueOf(XMLHandler.getTagValue(partNode, "errorLines"));
		isEnable =  "Y".equalsIgnoreCase( XMLHandler.getTagValue(partNode, "isEnable") );
		exceptionPosition = XMLHandler.getTagValue(partNode, "exceptionPosition");
		
		String exceptionDetail64 = XMLHandler.getTagValue(partNode, "exceptionDetail");
		if (!Utils.isEmpty(exceptionDetail64)) {
			// This is a CDATA block with a Base64 encoded GZIP compressed stream of data.
			String dataString64 = exceptionDetail64.substring("<![CDATA[".length(), exceptionDetail64.length() - "]]>".length());
			try {
				exceptionDetail = HttpUtil.decodeBase64ZippedString(dataString64);
			} catch (IOException e) {
				exceptionDetail = "Unable to decode logging from remote server : " + e.toString() + Const.CR + Const.getStackTracker(e);
			}
		} 
		exceptionName = XMLHandler.getTagValue(partNode, "exceptionName");
		exceptionType = XMLHandler.getTagValue(partNode, "exceptionType");
		dataInputSource = XMLHandler.getTagValue(partNode, "dataInputSource");
		dataOutputSource = XMLHandler.getTagValue(partNode, "dataOutputSource");

		String loggingString64 = XMLHandler.getTagValue(partNode, "log");
		String loggingString;
		if (!Utils.isEmpty(loggingString64)) {
			// This is a CDATA block with a Base64 encoded GZIP compressed stream of data.
			String dataString64 = loggingString64.substring("<![CDATA[".length(),
					loggingString64.length() - "]]>".length());
			try {
				loggingString = HttpUtil.decodeBase64ZippedString(dataString64);
			} catch (IOException e) {
				loggingString = "Unable to decode logging from remote server : " + e.toString() + Const.CR
						+ Const.getStackTracker(e);
			}
		} else {
			loggingString = "";
		}
		log = new StringBuilder(loggingString);

	}

	public String getXML() throws KettleException, IOException {

		StringBuilder xml = new StringBuilder();

		xml.append("  ").append(XMLHandler.openTag(XML_TAG)).append(Const.CR);
		xml.append("    ").append(XMLHandler.addTagValue("id", id));
		xml.append("    ").append(XMLHandler.addTagValue("begin", begin));
		xml.append("    ").append(XMLHandler.addTagValue("end", end));
		xml.append("    ").append(XMLHandler.addTagValue("status", status));
		xml.append("    ").append(XMLHandler.addTagValue("inputLines", inputLines));
		xml.append("    ").append(XMLHandler.addTagValue("outputLines", outputLines));
		xml.append("    ").append(XMLHandler.addTagValue("readLines", readLines));
		xml.append("    ").append(XMLHandler.addTagValue("writeLines", writeLines));
		xml.append("    ").append(XMLHandler.addTagValue("updateLines", updateLines));
		xml.append("    ").append(XMLHandler.addTagValue("errorLines", errorLines));
		xml.append("    ").append(XMLHandler.addTagValue("isEnable", isEnable));
		xml.append("    ").append(XMLHandler.addTagValue("exceptionPosition", exceptionPosition));
		xml.append("    ").append(XMLHandler.addTagValue("exceptionDetail", exceptionDetail!= null ?XMLHandler.buildCDATA(HttpUtil.encodeBase64ZippedString(exceptionDetail)):"" ));
		xml.append("    ").append(XMLHandler.addTagValue("exceptionName", exceptionName));
		xml.append("    ").append(XMLHandler.addTagValue("exceptionType", exceptionType));
		xml.append("    ").append(XMLHandler.addTagValue("dataInputSource", dataInputSource));
		xml.append("    ").append(XMLHandler.addTagValue("dataOutputSource", dataOutputSource));

		xml.append("    ").append(XMLHandler.addTagValue("log", log!= null ?XMLHandler.buildCDATA(HttpUtil.encodeBase64ZippedString(log.toString())):"" ));
		xml.append("  ").append(XMLHandler.closeTag(XML_TAG));

		return xml.toString();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param 设置
	 *            id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the begin
	 */
	public String getBegin() {
		return begin;
	}

	/**
	 * @param 设置
	 *            begin
	 */
	public void setBegin(String begin) {
		this.begin = begin;
	}

	/**
	 * @return the end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * @param 设置
	 *            end
	 */
	public void setEnd(String end) {
		this.end = end;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param 设置
	 *            status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the log
	 */
	public String getLog() {
		return log.toString();
	}

	/**
	 * @param 设置
	 *            log
	 */
	public void appendLog(String logstr) {
		if (!logstr.endsWith(Const.CR)) {
			logstr += Const.CR;
		}
		this.log.append(logstr);
	}

	/**
	 * @return the inputLines
	 */
	public Long getInputLines() {
		return inputLines;
	}

	/**
	 * @param 设置
	 *            inputLines
	 */
	public void setInputLines(Long inputLines) {
		this.inputLines = inputLines;
	}

	/**
	 * @return the outputLines
	 */
	public Long getOutputLines() {
		return outputLines;
	}

	/**
	 * @param 设置
	 *            outputLines
	 */
	public void setOutputLines(Long outputLines) {
		this.outputLines = outputLines;
	}

	/**
	 * @return the readLines
	 */
	public Long getReadLines() {
		return readLines;
	}

	/**
	 * @param 设置
	 *            readLines
	 */
	public void setReadLines(Long readLines) {
		this.readLines = readLines;
	}

	/**
	 * @return the writeLines
	 */
	public Long getWriteLines() {
		return writeLines;
	}

	/**
	 * @param 设置
	 *            writeLines
	 */
	public void setWriteLines(Long writeLines) {
		this.writeLines = writeLines;
	}

	/**
	 * @return the updateLines
	 */
	public Long getUpdateLines() {
		return updateLines;
	}

	/**
	 * @param 设置
	 *            updateLines
	 */
	public void setUpdateLines(Long updateLines) {
		this.updateLines = updateLines;
	}

	/**
	 * @return the errorLines
	 */
	public Long getErrorLines() {
		return errorLines;
	}

	/**
	 * @param 设置
	 *            errorLines
	 */
	public void setErrorLines(Long errorLines) {
		this.errorLines = errorLines;
	}

	public String getDataInputSource() {
		return dataInputSource;
	}

	public void setDataInputSource(String dataInputSource) {
		this.dataInputSource = dataInputSource;
	}

	public String getDataOutputSource() {
		return dataOutputSource;
	}

	public void setDataOutputSource(String dataOutputSource) {
		this.dataOutputSource = dataOutputSource;
	}

	public String getExceptionPosition() {
		return exceptionPosition;
	}

	public void setExceptionPosition(String exceptionPosition) {
		this.exceptionPosition = exceptionPosition;
	}

	public String getExceptionDetail() {
		return exceptionDetail;
	}

	public void setExceptionDetail(String exceptionDetail) {
		this.exceptionDetail = exceptionDetail;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getExceptionType() {
		return exceptionType;
	}

	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SegmentingPartInfo other = (SegmentingPartInfo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
