package com.ys.idatrix.quality.steps.analysis.character;

import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.alibaba.dubbo.rpc.RpcException;
import com.idatrix.es.api.dto.resp.AnalyzedTokenDto;
import com.ys.idatrix.quality.analysis.dao.NodeDictDao;
import com.ys.idatrix.quality.analysis.dao.NodeDictDataDao;
import com.ys.idatrix.quality.analysis.dto.NodeDictDataDto;
import com.ys.idatrix.quality.analysis.dto.NodeDictDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.PluginFactory;
import com.ys.idatrix.quality.reference.agentproxy.EsAgentService;
import com.ys.idatrix.quality.steps.analysis.base.AnalysisBase;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.AnalysisTypeFieldEnum;


public class CharacterAnalysis extends AnalysisBase implements StepInterface {
	
	private CharacterAnalysisData data;
	private CharacterAnalysisMeta meta;

	public CharacterAnalysis(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		
		meta = (CharacterAnalysisMeta) smi;
		data = (CharacterAnalysisData) sdi;

		if (super.init(smi, sdi)) {
			
			if( !Utils.isEmpty( meta.getStandardKey() ) ) {
				
				try {
					
					data.userName = CloudSession.getLoginUser();
					data.analyzedDicId = meta.getStandardKey();
					NodeDictDto dict = NodeDictDao.getInstance().getDictById(data.analyzedDicId);
					if(dict == null || dict.getStatus() != 1 ) {
						logError( "字典["+ ( dict == null?meta.getStandardKey():dict.getDictName() )+"] 不存在或者不是生效状态,请检查!"  );
						return false ;
					}
					data.dictName = dict.getDictName();
					data.esAgentService =(EsAgentService) PluginFactory.getBean(EsAgentService.class);
					if( data.esAgentService == null ) {
						return initLocalSynonyms();
					}
					return true;
					
				} catch (Exception e) {
					logError( "字典查询["+meta.getStandardKey()+"] 失败!" , e );
					return false ;
				}
				
			}else {
				logError( "字典配置为空,请检查!" );
				return false;
			}
			
			
		}
		return false;

	}

	
	@Override
	public void validateFields(String fieldName ,String valueData ,Object smi ,Object[] result )  throws Exception{
		
		setDictName(result);
		
		if( data.esAgentService != null ) {
			
			try {
				List<AnalyzedTokenDto> analyzedResults = data.esAgentService.analyzedContentByDicName(data.userName, data.analyzedDicId, valueData);
				if( analyzedResults!= null && analyzedResults.size() >0 ) {
					
					Optional<AnalyzedTokenDto> positionOpt = analyzedResults.stream().filter(token -> { return valueData.equals( token.getToken() ) ;}).findAny();
					if(positionOpt.isPresent()) {
						int positon = positionOpt.get().getPosition() ;
						Optional<AnalyzedTokenDto> synonymOpt = analyzedResults.stream().filter(token -> { return positon == token.getPosition() && "SYNONYM".equals(token.getType()) ;}).findAny();
						if(synonymOpt.isPresent()) {
							setFieldValue(result, AnalysisTypeFieldEnum.result.toString() , true ); 
							setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(), valueData ); 
							
							return ;
						}
					}
				}else {
					logDetailed("[WARN] 域["+fieldName+"]进行ES校验,返回分析结果为空.");
				}
			}catch(RpcException e) {
				//没有提供者异常
				logRowlevel("[WARN] 域["+fieldName+"]ES服务校验未找到服务提供者,使用本地分析器分析.");
				initLocalSynonyms();
				localSynonymsAnalyzer(valueData, result);
				return ;
			}
			
			setFieldValue(result, AnalysisTypeFieldEnum.result.toString(), false ); 
			//setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(),  AnalysisBaseMeta.NON_MATCH_REFERENCE  ); 
			setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(),  valueData  ); 
			
		}else {
			localSynonymsAnalyzer(valueData, result);
		}
	}
	
	private void setDictName(Object[] result) {
		int dictIndex = data.outputRowMeta.indexOfValue( "dictName" );
		if( dictIndex != -1) {
			result[dictIndex] = data.dictName;
		}
	}
	
	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (CharacterAnalysisMeta) smi;
		data = (CharacterAnalysisData) sdi;
		
		super.dispose(smi, sdi);
		
		if(data.analyzer != null ) {
			data.analyzer.close();
			data.analyzer = null ;
		}
		if(data.synonymsFile != null ) {
			data.synonymsFile.delete();
			data.synonymsFile =  null;
		}
		
		if(data.esAgentService != null ) {
			data.esAgentService = null ;
		}
		
	}
	
	@Override
	public  boolean validate(String valueData ,String format,boolean isStandard)  {
		//实现validateFields方法,忽略
		return true;
	}

	@Override
	public String guessReference(String valueData) {
		//实现validateFields方法,忽略
		return null;
	}
	
	
	
	//RpcException
	private  boolean initLocalSynonyms() throws Exception {
		
		if(data.analyzer != null ) {
			return true ;
		}
		logBasic("es服务没有找到提供者,使用本地同义词解析器.");
		 List<NodeDictDataDto> dicts = NodeDictDataDao.getInstance().getDictDataListBySearch(meta.getStandardKey(), null, 1, Integer.MAX_VALUE-1);
		 if(dicts == null || dicts.size() == 0) {
			logError("字典["+meta.getStandardKey()+"]中未找到字典数据信息!" );
			return false ;
		 }
		 writeSynonymsFile(dicts);
		
		data.analyzer =  new SynonymsAnalyzer(data.synonymsPath);
		
		return true ;
	}
	
	
	private String writeSynonymsFile( List<NodeDictDataDto> dicts ) throws Exception {
		if(dicts == null || dicts.size() == 0) {
			return null ;
		}
		data.synonymsFile = File.createTempFile(Thread.currentThread().getName()+"_"+data.nodeName+"_synonyms", ".txt");
		data.synonymsPath = data.synonymsFile.getAbsolutePath();
		
		OutputStream outStream = KettleVFS.getOutputStream(data.synonymsPath, true);
		try {
			
			for(NodeDictDataDto dict : dicts) {
				StringBuffer sb = new StringBuffer();
				if(!Utils.isEmpty(dict.getStdVal1())) {
					sb.append(dict.getStdVal1());
				}
				if(!Utils.isEmpty(dict.getSimVal2())) {
					sb.append(",").append(dict.getSimVal2());
				}
				if(!Utils.isEmpty(dict.getSimVal3())) {
					sb.append(",").append(dict.getSimVal3());
				}
				if(!Utils.isEmpty(dict.getSimVal4())) {
					sb.append(",").append(dict.getSimVal4());
				}
				if(!Utils.isEmpty(dict.getSimVal5())) {
					sb.append(",").append(dict.getSimVal5());
				}
				if(!Utils.isEmpty(dict.getSimVal6())) {
					sb.append(",").append(dict.getSimVal6());
				}
				if(!Utils.isEmpty(dict.getSimVal7())) {
					sb.append(",").append(dict.getSimVal7());
				}
				if(!Utils.isEmpty(dict.getSimVal8())) {
					sb.append(",").append(dict.getSimVal8());
				}
				if(!Utils.isEmpty(dict.getSimVal9())) {
					sb.append(",").append(dict.getSimVal9());
				}
				if(!Utils.isEmpty(dict.getSimVal10())) {
					sb.append(",").append(dict.getSimVal10());
				}
				
				sb.append("\n");
				
				outStream.write(sb.toString().getBytes());
			}
			
		}finally {
			outStream.flush();
			outStream.close();
		}
		
		return data.synonymsPath ;
	}
	
	
	private void localSynonymsAnalyzer(String valueData , Object[] result) throws Exception {
		String synonyms = data.analyzer.getSynonyms(valueData);
		if(!Utils.isEmpty(synonyms)) {
			setFieldValue(result, AnalysisTypeFieldEnum.result.toString(), true ); 
			setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(), synonyms ); 
		}else {
			setFieldValue(result, AnalysisTypeFieldEnum.result.toString(), false ); 
			//setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(),  AnalysisBaseMeta.NON_MATCH_REFERENCE  ); 
			setFieldValue(result, AnalysisTypeFieldEnum.referenceValue.toString(),  valueData  ); 
		}
	}
	
	
}
