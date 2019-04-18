/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.executor.logger.xml;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.dto.history.ExecHistoryRecordDto;
import com.ys.idatrix.quality.dto.history.ExecHistorySegmentingPartDto;
import com.ys.idatrix.quality.ext.CloudApp;
import com.ys.idatrix.quality.ext.utils.UnixPathUtil;
import com.ys.idatrix.quality.logger.CloudLogConst;
import com.ys.idatrix.quality.logger.CloudLogType;

/**
 * CloudExecHistory.java
 * @author JW
 * @since 2017年8月2日
 *
 */
public class FileExecHistory {
	
	public static final Log  logger = LogFactory.getLog("CloudExecHistory");
	
	/** The package name, used for internationalization of messages. */
	private static Class<?> PKG = Trans.class; // for i18n purposes, needed by Translator2!!

	private static final String XML_TAG_ROOT = "records";
	private static final String XML_TAG_ELEM = "record";

	private static final int MAX_RECORD_NUMBER = Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.exec.log.xml.max.record.number", "10000"));
	private static final int MAX_RECORD_PART_NUMBER = Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.exec.log.xml.max.record.part.number", "10000"));
	private static final int MAX_CACHE_NUMBER = Integer.valueOf(IdatrixPropertyUtil.getProperty("idatrix.exec.log.xml.max.cache.number", "8"));

	private static Map<String ,List<ExecHistoryRecordDto> > historyCache;
	private static Map<String ,List<String > > historyPartKeyCache;
	private static Map<String ,List<List<ExecHistorySegmentingPartDto>> > historyPartValueCache;
	
	private String filepath;
	private CloudLogType filetype;
	private String name;
	
	private String filename;
	
	// History records, only load once-time
	List<ExecHistoryRecordDto> historyRecords;
	List<String > SegmentingPartMapKeys;
	List<List<ExecHistorySegmentingPartDto>> SegmentingPartMapValues;
	

	public FileExecHistory(String path, String name, CloudLogType type) {
		this.filepath = UnixPathUtil.unixPath(CloudApp.getInstance().getRepositoryRootFolder() + path + type.getType() + CloudLogConst.SEPARATOR);
		this.filename = this.filepath + name + type.getExtension();
		this.filetype = type;
		this.name = name ;

		if(historyCache == null) {
			historyCache = Maps.newLinkedHashMap();
		}
		if(historyPartKeyCache ==  null ) {
			historyPartKeyCache =  Maps.newLinkedHashMap();
		}
		if(historyPartValueCache ==  null ) {
			historyPartValueCache =  Maps.newLinkedHashMap();
		}
		try {
			if(historyCache.containsKey(filename) && historyCache.get(filename) != null) {
				historyRecords = historyCache.get(filename);
				SegmentingPartMapKeys = historyPartKeyCache.get(filename);
				SegmentingPartMapValues = historyPartValueCache.get(filename);
			}else {
				loadExecHistory();
			}
		} catch (KettleXMLException e) {
			logger.error("初始化历史记录失败.",e);
		}
	}
	
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder(1000);

		retval.append(XMLHandler.openTag(XML_TAG_ROOT)).append(Const.CR);
		for (ExecHistoryRecordDto record : historyRecords) {
			
			List<ExecHistorySegmentingPartDto> partList = null ;
			if( SegmentingPartMapKeys.contains( record.getExecId() ) ) {
				partList  = SegmentingPartMapValues.get( SegmentingPartMapKeys.indexOf(record.getExecId()) );
			}
			
			retval.append("\t").append(XMLHandler.openTag(XML_TAG_ELEM)).append(Const.CR);
			retval.append(record.readXml("\t\t",partList));
			retval.append("\t").append(XMLHandler.closeTag(XML_TAG_ELEM)).append(Const.CR);
		}
		retval.append(XMLHandler.closeTag(XML_TAG_ROOT)).append(Const.CR);
		return retval.toString();
	}

	@SuppressWarnings("unchecked")
	private synchronized void loadExecHistory() throws KettleXMLException {
		historyRecords = new ArrayList<>();
		SegmentingPartMapKeys = new ArrayList<>();
		SegmentingPartMapValues = new ArrayList<>();
		//使用dom4j方式，对大文档性能比较好
		FileObject fileObject = null ;
		try {
			Document doc = null;
			fileObject = KettleVFS.getFileObject(filename);
			if (fileObject.exists()) {
				  SAXReader reader = new SAXReader();
				  doc = reader.read(fileObject.getContent().getInputStream());
				//doc = XMLHandler.loadXMLFile(fileObject);
			}
			if (doc != null) {
				// Root node:
				Element root = doc.getRootElement();
				//Node rootnode = XMLHandler.getSubNode(doc, XML_TAG_ROOT);
				if (root == null) {
					throw new KettleXMLException(BaseMessages.getString( PKG, "CloudExecHistory.Exception.NotValidTransformationXML", filename));
				}

				// Element nodes:
				 List<Element> elements = root.elements();
				 for(Element elemnode:elements){
//				int n = XMLHandler.countNodes(rootnode, XML_TAG_ELEM);
//				for (int i = 0; i < n; i++) {
					//Node elemnode = XMLHandler.getSubNodeByNr(rootnode, XML_TAG_ELEM, i);
					ExecHistoryRecordDto record = new ExecHistoryRecordDto("");
					List<ExecHistorySegmentingPartDto> parts = record.loadXml(elemnode);
					historyRecords.add(record);
					if(parts != null && parts.size() >0) {
						SegmentingPartMapValues.add( parts);
						SegmentingPartMapKeys.add(record.getExecId());
					}
				}
				 
				 if( MAX_CACHE_NUMBER > 0 ) {
					 if( historyCache.size() > MAX_CACHE_NUMBER) {
						 String firstKey = (String) historyCache.keySet().toArray()[0] ;
						 historyCache.remove(firstKey);
						 historyPartKeyCache.remove(firstKey);
						 historyPartValueCache.remove(firstKey);
					 }

					 historyCache.put(filename,historyRecords);
					 historyPartKeyCache.put(filename,SegmentingPartMapKeys);
					 historyPartValueCache.put(filename,SegmentingPartMapValues);
				 }
			} 
			
		} catch (Exception e) {
			throw new KettleXMLException(BaseMessages.getString( PKG, "CloudExecHistory.Exception.ErrorOpeningOrValidatingTheXMLFile", filename), e);
		}finally {
			if(fileObject != null) {
				try {
					fileObject.close();
				} catch (FileSystemException e) {
				}
			}
		}
	}
	
	public synchronized void saveExecHistory() throws Exception {
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (!fileObject.exists()) {
			fileObject.createFile();
		}

		OutputStream os = KettleVFS.getOutputStream(fileObject, false);
		os.write(this.getXML().getBytes(Const.XML_ENCODING));
		os.close();
		fileObject.close();
	}

	/*============================================ExecHistoryRecordDto=================================================================*/
	
	public ExecHistoryRecordDto getExecRecord(String execId) {
		Optional<ExecHistoryRecordDto> opt = historyRecords.stream().filter(record -> (record.getExecId().equals(execId))).findFirst();
		if(opt.isPresent()) {
			return opt.get() ;
		}
		return null;
	}
	
	public ExecHistoryRecordDto getLastExecRecord() {
		if (historyRecords.size() > 0) {
			return historyRecords.get(0);
		}
		return null;
	}
	
	public ExecHistoryRecordDto getTotalExecRecord() {
		if(historyRecords.size() > 1) {
			ExecHistoryRecordDto lastResult = getLastExecRecord();
			ExecHistoryRecordDto oldestResult = historyRecords.get(historyRecords.size()-1);
			ExecHistoryRecordDto result = new ExecHistoryRecordDto(lastResult.getExecId(),lastResult.getRenterId(),lastResult.getOwner(), lastResult.getName(), lastResult.getType(), lastResult.getStatus(), lastResult.getOperator(), oldestResult.getBegin(), lastResult.getEnd());
			result.setConfiguration(lastResult.getConfiguration());
			historyRecords.stream().forEach(h -> {
				result.addIncreaseLines(h.getInputLines(), h.getOutputLines(),h.getReadLines(),h.getWriteLines(), h.getErrorLines(), h.getUpdateLines());
			});
			
			return result;
		}else {
			return getLastExecRecord();
		}
	}
	
	public List<ExecHistoryRecordDto> getExecRecords( ) {
		return historyRecords;
	}

	public void insertExecRecord(ExecHistoryRecordDto record) throws Exception{
		record.setName(name);
		record.setType(filetype.getType());
		historyRecords.add(0, record);
		if (MAX_RECORD_NUMBER > 0 && historyRecords.size() >= MAX_RECORD_NUMBER) {
			historyRecords = historyRecords.subList(0, MAX_RECORD_NUMBER);
		}
		saveExecHistory();
	}
	
	public void updateExecRecord(ExecHistoryRecordDto record)  throws Exception{
		//进行保存
		saveExecHistory();
	}
	
	public void renameExecHistory(String newname) throws  Exception {
		
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			FileObject newObject = KettleVFS.getFileObject(UnixPathUtil.unixPath(filepath) + newname +  filetype.getExtension());
			fileObject.moveTo(newObject); // JW: maybe failed !
			newObject.close();
		}
		fileObject.close();
		
		this.filename = this.filepath + newname + filetype.getExtension();
	}


	public void deleteExecRecord(String execId) throws  Exception {
		historyRecords.removeIf(record -> (record.getExecId().equals(execId)));
		saveExecHistory();
	}
	
	public void clearExecHistory() throws Exception {
		historyRecords.clear();
		FileObject fileObject = KettleVFS.getFileObject(filename);
		if (fileObject.exists()) {
			fileObject.delete();
		}
		fileObject.close();
	}
	
	/*============================================ExecHistorySegmentingPartDto=================================================================*/
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart() throws Exception {
		
		if(  SegmentingPartMapValues.size() > 0 ) {
			 List<ExecHistorySegmentingPartDto> it = SegmentingPartMapValues.get(0) ;
			 if(it != null && it.size() >0 ) {
				return it.get(0);
			 }
		}
		return null;
	}
	
	public ExecHistorySegmentingPartDto getLastSegmentingPart(String execId )  throws Exception {
		if(  SegmentingPartMapKeys.contains(execId) ) {
			List<ExecHistorySegmentingPartDto> parts = SegmentingPartMapValues.get(SegmentingPartMapKeys.indexOf(execId)) ;
			 if(parts!= null && parts.size() >0 ) {
				return parts.get(0);
			 }
		}
		return null;
	}
	
	public ExecHistorySegmentingPartDto getSegmentingPart(String execId ,String runId)  throws Exception {
		if(  SegmentingPartMapKeys.contains(execId) ) {
			List<ExecHistorySegmentingPartDto> parts = SegmentingPartMapValues.get(SegmentingPartMapKeys.indexOf(execId)) ;
			 if(parts!= null && parts.size() >0 ) {
				 Optional<ExecHistorySegmentingPartDto> opt = parts.stream().filter(part -> (part.getId().equals(runId))).findFirst();
				 if(opt.isPresent()) {
					 return  opt.get();
				 }
			 }
		}
		return null;
	}
	
	public ExecHistorySegmentingPartDto getSegmentingPart( String runId)  throws Exception {
		if(  SegmentingPartMapValues.size() >0 ) {
			for( List<ExecHistorySegmentingPartDto> parts  : SegmentingPartMapValues) {
				if(parts!= null && parts.size() >0 ) {
					 Optional<ExecHistorySegmentingPartDto> opt = parts.stream().filter(part -> (part.getId().equals(runId))).findFirst() ;
					if(opt.isPresent() ) {
						return opt.get();
					}
				 }
			}
		}
		return null;
	}

	public List<ExecHistorySegmentingPartDto> getSegmentingParts( )  throws Exception{
		if(  SegmentingPartMapValues.size() >0 ) {
			List<ExecHistorySegmentingPartDto> result = new ArrayList<ExecHistorySegmentingPartDto>();
			SegmentingPartMapValues.stream().forEach( oneValue ->{ result.addAll(oneValue); });
			return result;
		}
		return null;
	}
	
	public List<ExecHistorySegmentingPartDto> getSegmentingPartsByExecId( String execId)  throws Exception{
		if( SegmentingPartMapKeys.contains(execId) ) {
			return SegmentingPartMapValues.get(SegmentingPartMapKeys.indexOf(execId)) ;
		}
		return null;
	}

	public void insertSegmentingPart(ExecHistorySegmentingPartDto part)  throws Exception{
		part.setName(name);
		part.setType(filetype.getType());
		
		 List<ExecHistorySegmentingPartDto> list = null ;
		if( SegmentingPartMapKeys.contains( part.getExecId()) ) {
			 list = SegmentingPartMapValues.get( SegmentingPartMapKeys.indexOf(part.getExecId())) ;
		}
		if(list == null) {
			list = Lists.newArrayList() ;
			
			SegmentingPartMapValues.add(0, list);
			SegmentingPartMapKeys.add(0, part.getExecId());
		}
		list.add(0,part);
		if (MAX_RECORD_PART_NUMBER>0 && list.size() >= MAX_RECORD_PART_NUMBER) {
			list = list.subList(0, MAX_RECORD_PART_NUMBER);
		}
		//更新ExecRecord 会一起更新 SegmentingPart ,不需要再次更新
		//saveExecHistory();
	}
	
	public void updateSegmentingPart( ExecHistorySegmentingPartDto part )  throws Exception{
		//更新ExecRecord 会一起更新 SegmentingPart ,不需要再次更新
		//saveExecHistory();
	}
	

	public void renameSegmentingPart( String newname) throws Exception {
		//nothing
	}
	
	public void deleteSegmentingPartByExecId(String execId)  throws Exception{
		if(SegmentingPartMapKeys.contains(execId)) {
			SegmentingPartMapValues.remove(SegmentingPartMapKeys.indexOf( execId ));
			SegmentingPartMapKeys.remove(execId);
			
			saveExecHistory();
		}
		
	}
	
	public void deleteSegmentingPartById(String execId,String runId)  throws Exception{
		if(SegmentingPartMapKeys.contains(execId)) {
			List<ExecHistorySegmentingPartDto> list = SegmentingPartMapValues.get(SegmentingPartMapKeys.indexOf( execId ));
			Optional<ExecHistorySegmentingPartDto> opt = list.stream().filter(part -> (part.getId().equals(runId))).findFirst();
			if(opt.isPresent()) {
				ExecHistorySegmentingPartDto p = opt.get();
				list.remove(p);
				saveExecHistory();
			}
		}
	}
	
	public void deleteSegmentingPartById( String runId)  throws Exception{
		if(SegmentingPartMapValues.size() >0 ) {
			for(List<ExecHistorySegmentingPartDto> list : SegmentingPartMapValues) {
				Optional<ExecHistorySegmentingPartDto> opt = list.stream().filter(part -> (part.getId().equals(runId))).findFirst();
				if(opt.isPresent()) {
					list.remove(opt.get());
					break ;
				}
				
			}
			saveExecHistory();
		}
	}
	
	public void clearSegmentingPart() throws Exception {
		SegmentingPartMapKeys.clear();
		SegmentingPartMapValues.clear();
		saveExecHistory();
	}

}
