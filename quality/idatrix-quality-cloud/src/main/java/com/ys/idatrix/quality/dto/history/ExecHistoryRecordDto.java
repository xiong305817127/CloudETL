/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.history;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.dom4j.Element;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;
import com.google.common.collect.Lists;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper.FieldUpperCase;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper.IgnoreField;
import com.ys.idatrix.quality.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.quality.ext.utils.StringEscapeHelper;
import com.ys.idatrix.quality.logger.CloudLogConst;

import io.swagger.annotations.ApiModel;

/**
 * TransHistoryRecordDto.java
 * @author JW
 * @since 2017年7月31日
 *
 */
@ApiModel("执行历史结果信息")
@FieldUpperCase
@Table(catalog="idatrix.exec.log.record.tableName",name="QUALITY_EXEC_RECORD")
public class ExecHistoryRecordDto {
	
	@Id
	private String execId;
	private String renterId;
	@IgnoreField(conditionMethod="isNoRenterPrivilege")
	private String owner;
	private String name;
	private String type;
	private String status;
	private String operator;
	private Date begin;
	private Date end;

	private Long readLines;
	private Long writeLines;
	
	//数据库记录时统一记录,不再分开记录
	@IgnoreField
	private Long inputLines ;
	@IgnoreField
	private Long outputLines;
	@IgnoreField
	private Long updateLines ;
	@IgnoreField
	private Long errorLines ;
	@JsonIgnore
	@com.fasterxml.jackson.annotation.JsonIgnore
	private String otherLines; //对输入输出更新错误 行数的统一记录
	
	private String successFailTimes = "0/0";
	
	private ExecConfigurationDto configuration;
	
	//表输出.插入更新输出 的数据库信息
	private String outPutSource;
	
	public ExecHistoryRecordDto(String executionId) {
		this.execId = Utils.isEmpty(executionId)?UUID.randomUUID().getMostSignificantBits()+"":executionId;
	}
	
	public ExecHistoryRecordDto() {
		this.execId = UUID.randomUUID().getMostSignificantBits()+"";
	}
	
	/**
	 * @param execId
	 * @param name
	 * @param type
	 * @param status
	 * @param operator
	 * @param begin
	 * @param end
	 * @param logPath
	 */
	public ExecHistoryRecordDto(String execId,String renterId, String owner ,String name, String type, String status, String operator, Date begin, Date end) {
		super();
		this.execId = execId;
		this.renterId = renterId;
		this.owner = owner;
		this.name = name;
		this.type = type;
		this.status = status;
		this.operator = operator;
		this.begin = begin;
		this.end = end;
		
	}

	public String getExecId() {
		return execId;
	}

	public void setExecId(String execId) {
		this.execId = execId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOutPutSource() {
		return outPutSource;
	}

	public void setOutPutSource(String outPutSource) {
		this.outPutSource = outPutSource;
	}

	public String getRenterId() {
		return renterId;
	}

	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}

	public ExecConfigurationDto getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ExecConfigurationDto configuration) {
		this.configuration = configuration;
	}

	public String getBeginStr() {
		if(begin != null ) {
			return DateFormatUtils.format(begin, CloudLogConst.EXEC_TIME_PATTERN);
		}
		return "";
	}
	public void setBeginStr(String str) {
		if( !Utils.isEmpty(str)) {
			try {
				begin = DateUtils.parseDate(str, CloudLogConst.EXEC_TIME_PATTERN);
			} catch (ParseException e) {
			}
		}
	}
	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}
	
	public String getEndStr() {
		if(end != null ) {
			return DateFormatUtils.format(end, CloudLogConst.EXEC_TIME_PATTERN);
		}
		return "";
	}
	public void setEndStr(String str) {
		if( !Utils.isEmpty(str)) {
			try {
				end = DateUtils.parseDate(str, CloudLogConst.EXEC_TIME_PATTERN);
			} catch (ParseException e) {
			}
		}
	}
	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getReadLines() {
		return readLines == null ? 0:readLines;
	}

	public void setReadLines(Long readLines) {
		this.readLines = readLines;
	}

	public Long getWriteLines() {
		return writeLines == null ? 0:writeLines;
	}

	public void setWriteLines(Long writeLines) {
		this.writeLines = writeLines;
	}
	

	public String getOtherLines() {
		this.otherLines = getInputLines()+":"+getOutputLines()+":"+getErrorLines()+":"+getUpdateLines();
		return this.otherLines ;
	}

	public void setOtherLines(String otherLines) {
		this.otherLines = otherLines;
		getInputLines();
		getOutputLines();
		getErrorLines();
		getUpdateLines();
	}
	
	public Long getInputLines() {
		if( inputLines == null && !Utils.isEmpty(otherLines) ) {
			String[] ss = otherLines.split(":", 4);
			inputLines = ss.length>0? Long.valueOf(ss[0]) : 0;
		}
		return inputLines  == null ? 0: inputLines;
	}

	public void setInputLines(Long inputLines) {
		this.inputLines = inputLines;
		getOtherLines();
	}

	public Long getOutputLines() {
		
		if( outputLines == null && !Utils.isEmpty(otherLines) ) {
			String[] ss = otherLines.split(":", 4);
			outputLines =  ss.length>1? Long.valueOf(ss[1]) : 0;
		}
		return outputLines  == null ? 0: outputLines;
	}

	public void setOutputLines(Long outputLines) {
		this.outputLines = outputLines;
		getOtherLines();
	}


	public Long getErrorLines() {
		if( errorLines == null && !Utils.isEmpty(otherLines) ) {
			String[] ss = otherLines.split(":", 4);
			errorLines = ss.length>2 ? Long.valueOf(ss[2]) : 0;
		}
		return errorLines == null ? 0:errorLines;
	}

	public void setErrorLines(Long errorLines) {
		this.errorLines = errorLines;
		getOtherLines();
	}

	public Long getUpdateLines() {
		if( updateLines == null && !Utils.isEmpty(otherLines) ) {
			String[] ss = otherLines.split(":", 4);
			updateLines = ss.length>3? Long.valueOf(ss[3]) : 0;
		}
		return updateLines == null ? 0:updateLines;
	}

	public void setUpdateLines(Long updateLines) {
		this.updateLines = updateLines;
		getOtherLines();
	}

	public void addIncreaseLines(Long inputLines, Long outputLines,Long readLines, Long writeLines, Long errorLines, Long updateLines ) {
			this.inputLines  = getInputLines() +inputLines;
			this.outputLines = getOutputLines() +outputLines;
			this.readLines 	 = getReadLines() + readLines;
			this.writeLines  = getWriteLines() + writeLines ;
			this.errorLines  = getErrorLines() + errorLines ;
			this.updateLines = getUpdateLines() + updateLines ;
			getOtherLines();
	}
	
	public String getSuccessFailTimes() {
		return successFailTimes;
	}

	public void setSuccessFailTimes(String successFailTimes) {
		this.successFailTimes = successFailTimes;
	}

	public Long getSuccessTimes() {
		if( Utils.isEmpty( successFailTimes ) ) {
			return 0L ;
		}
		return Long.valueOf( successFailTimes.split("/")[0] ) ;
	}
	
	public Long getFailTimes() {
		if( Utils.isEmpty( successFailTimes ) || !successFailTimes.contains("/") ) {
			return 0L ;
		}
		return Long.valueOf( successFailTimes.split("/")[1] ) ;
	}
	
	public void increaseTimes(boolean isSuccess) {
		Long success = getSuccessTimes();
		Long fail = getFailTimes();
		if( isSuccess ) {
			success = success+1;
		}else {
			fail = fail+1;
		}
		successFailTimes = success+"/"+fail;
	}
	
	public ExecHistoryRecordDto clone() {
		ExecHistoryRecordDto res = new ExecHistoryRecordDto(execId, renterId , owner ,name, type, status, operator, begin, end);
		res.setConfiguration(configuration);
		res.setInputLines(getInputLines());
		res.setOutputLines(getOutputLines());
		res.setReadLines(getReadLines());
		res.setWriteLines(getWriteLines());
		res.setUpdateLines(getUpdateLines());
		res.setErrorLines(getErrorLines());
		
		res.setSuccessFailTimes(successFailTimes);
		
		return res ;
	}
	
	public String readXml(String offsetPrefix,List<ExecHistorySegmentingPartDto> spList){
		StringBuilder retval = new StringBuilder(1000);

		retval.append(offsetPrefix).append(XMLHandler.addTagValue("id", getExecId()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("renterId", getRenterId()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("owner", getOwner()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("name", getName()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("type", getType()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("status", getStatus()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("begin", getBeginStr()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("end", getEndStr()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("operator", getOperator()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("outPutSource", getOutPutSource()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("lines", getInputLines()+":"+getOutputLines()+":"+getReadLines()+":"+getWriteLines()+":"+getUpdateLines()+":"+getErrorLines()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("successFailTimes", getSuccessFailTimes()));
		if(getConfiguration() != null ) {
			retval.append(offsetPrefix).append(XMLHandler.openTag("configuration")).append(Const.CR);
			retval.append(getConfiguration().readXml(offsetPrefix+"\t"));
			retval.append(offsetPrefix).append(XMLHandler.closeTag("configuration")).append(Const.CR);
		}
		
		if( spList != null && spList.size() >0) {
			retval.append(offsetPrefix).append(XMLHandler.openTag("parts")).append(Const.CR);
			for( ExecHistorySegmentingPartDto sp : spList ) {
				retval.append(offsetPrefix+"\t").append(XMLHandler.openTag("part")).append(Const.CR);
				retval.append(sp.readXml(offsetPrefix+"\t\t"));
				retval.append(offsetPrefix+"\t").append(XMLHandler.closeTag("part")).append(Const.CR);
			}
			retval.append(offsetPrefix).append(XMLHandler.closeTag("parts")).append(Const.CR);
		}
		return retval.toString();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<ExecHistorySegmentingPartDto> loadXml(Element historyNode ){
		
		setExecId(StringEscapeHelper.getDom4jElementText( historyNode,"id")); //XMLHandler.getTagValue(historyNode, "id")
		setRenterId(StringEscapeHelper.getDom4jElementText( historyNode,"renterId")); 
		setOwner(StringEscapeHelper.getDom4jElementText( historyNode,"owner")); //XMLHandler.getTagValue(historyNode, "name"));
		setName(StringEscapeHelper.getDom4jElementText( historyNode,"name")); //XMLHandler.getTagValue(historyNode, "name"));
		setType(StringEscapeHelper.getDom4jElementText( historyNode,"type")); //XMLHandler.getTagValue(historyNode, "name"));
		setStatus(StringEscapeHelper.getDom4jElementText( historyNode,"status")); //XMLHandler.getTagValue(historyNode, "status"));
		setBeginStr(StringEscapeHelper.getDom4jElementText( historyNode,"begin")); //XMLHandler.getTagValue(historyNode, "begin"));
		setEndStr(StringEscapeHelper.getDom4jElementText( historyNode,"end")); //XMLHandler.getTagValue(historyNode, "end"));
		setOperator(StringEscapeHelper.getDom4jElementText( historyNode,"operator")); //XMLHandler.getTagValue(historyNode, "operator"));
		setOutPutSource(StringEscapeHelper.getDom4jElementText( historyNode,"outPutSource")); //XMLHandler.getTagValue(historyNode, "log"));
		
		String lines = StringEscapeHelper.getDom4jElementText( historyNode,"lines");
		if(!Utils.isEmpty(lines)) {
			String[] ls = lines.split(":");
			setInputLines(Long.valueOf(ls[0]));
			setOutputLines(Long.valueOf(ls[1]));
			setReadLines(Long.valueOf(ls[2]));
			setWriteLines(Long.valueOf(ls[3]));
			setUpdateLines(Long.valueOf(ls[4]));
			setErrorLines(Long.valueOf(ls[5]));
		}
		
		setSuccessFailTimes(StringEscapeHelper.getDom4jElementText( historyNode,"successFailTimes")); 
		
		ExecConfigurationDto tecnd=  new ExecConfigurationDto();
		//Node configurationNode = XMLHandler.getSubNode(historyNode,"configuration");
		Element configurationNode = historyNode.element("configuration");
		if(configurationNode != null ) {
			tecnd.loadXml(configurationNode);
		}
		if(Utils.isEmpty(tecnd.getEngineName())) {
			tecnd.setEngineName("Default-Local");
			tecnd.setEngineType("default");
		}
		setConfiguration(tecnd);
		
		//Node partsNode = XMLHandler.getSubNode(historyNode,"parts");
		Element partsNode = historyNode.element("parts");
		if(partsNode != null ) {
			List<ExecHistorySegmentingPartDto> spList = Lists.newArrayList() ;
			List<Element> partElements = partsNode.elements();
			for ( Element partNode : partElements ) {
			//int n = XMLHandler.countNodes(partsNode, "part");
			//for (int i = 0; i < n; i++) {
			//	Node partNode = XMLHandler.getSubNodeByNr(partsNode, "part", i);
				ExecHistorySegmentingPartDto ehsp = new ExecHistorySegmentingPartDto();
				ehsp.loadXml(partNode);
				spList.add(ehsp);
			}
			
			return spList;
		}
		
		return null;
	}
	
	public static boolean isNoRenterPrivilege() {
		return !IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false) ;
	}
	
}
