package com.ys.idatrix.cloudetl.dto.history;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.dom4j.Element;
import org.pentaho.di.core.logging.SegmentingPartInfo;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.xml.XMLHandler;

import com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper;
import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper.FieldUpperCase;
import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper.IgnoreField;
import com.ys.idatrix.cloudetl.logger.CloudLogConst;

/**
 * 执行历史单次执行记录
 * @author xionghan
 *
 */
@FieldUpperCase
@Table(catalog="idatrix.exec.log.record.segmentingpart.tableName",name="ETL_EXEC_RECORD_PART")
public class ExecHistorySegmentingPartDto {
	
	@Id
	private String id;
	private String execId;
	private String operator;
	private String renterId;
	@IgnoreField(conditionMethod="isNoRenterPrivilege")
	private String owner;
	private String name;
	private String type;
	private Date begin;
	private Date end;
	private String status;
	
	private Long readLines ;
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
	
	public ExecHistorySegmentingPartDto() {
		super();
		this.id =  UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public ExecHistorySegmentingPartDto(SegmentingPartInfo partDto) {
		
		this.id = partDto.getId() ;
		setBeginStr(partDto.getBegin());
		setEndStr( partDto.getEnd() );
		this.status = partDto.getStatus() ;
		
		this.inputLines = partDto.getInputLines() ;
		this.outputLines = partDto.getOutputLines() ;
		this.readLines = partDto.getReadLines() ;
		this.writeLines = partDto.getWriteLines() ;
		this.updateLines = partDto.getUpdateLines() ;
		this.errorLines = partDto.getErrorLines() ;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	
	public Date getBegin() {
		return begin;
	}
	public void setBegin(Date begin) {
		this.begin = begin;
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
	
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRenterId() {
		return renterId;
	}

	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getReadLines() {
		return readLines== null ? 0:readLines;
	}
	public void setReadLines(Long readLines) {
		this.readLines = readLines;
	}

	public Long getWriteLines() {
		return writeLines== null ? 0:writeLines;
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
	
	public void fullLines(Long inputLines, Long outputLines, Long readLines ,Long writeLines ,Long errorLines, Long updateLines) {
		this.inputLines = inputLines;
		this.outputLines = outputLines;
		this.readLines = readLines ;
		this.writeLines = writeLines;
		this.errorLines = errorLines;
		this.updateLines = updateLines;
		getOtherLines();
	}

	public String readXml(String offsetPrefix){
		StringBuilder retval = new StringBuilder(1000);
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("id",id));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("execId",execId));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("begin",getBeginStr()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("end",getEndStr()));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("status",status));
		retval.append(offsetPrefix).append(XMLHandler.addTagValue("lines",getInputLines()+":"+getOutputLines()+":"+getReadLines()+":"+getWriteLines()+":"+getUpdateLines()+":"+getErrorLines()));
		
		return retval.toString();
	}
	
	
	public void loadXml(Element partNode){
		setId( StringEscapeHelper.getDom4jElementText(partNode,"id") );
		setExecId( StringEscapeHelper.getDom4jElementText(partNode,"execId"));
		setBeginStr( StringEscapeHelper.getDom4jElementText(partNode,"begin"));
		setEndStr( StringEscapeHelper.getDom4jElementText(partNode,"end"));
		setStatus( StringEscapeHelper.getDom4jElementText(partNode,"status"));
		String lines = StringEscapeHelper.getDom4jElementText(partNode,"lines");
		if(!Utils.isEmpty(lines)) {
			String[] ls = lines.split(":");
			setInputLines(Long.valueOf(ls[0]));
			setOutputLines(Long.valueOf(ls[1]));
			setReadLines(Long.valueOf(ls[2]));
			setWriteLines(Long.valueOf(ls[3]));
			setUpdateLines(Long.valueOf(ls[4]));
			setErrorLines(Long.valueOf(ls[5]));
		}
		
	}
	
	public static boolean isNoRenterPrivilege() {
		return !IdatrixPropertyUtil.getBooleanProperty("idatrix.renter.super.privilege.enable", false) ;
	}
	
}
