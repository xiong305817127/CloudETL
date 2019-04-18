package com.ys.idatrix.quality.steps.analysis.base;

import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import com.ys.idatrix.quality.logger.CloudLogger;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.AnalysisTypeFieldEnum;


public abstract class AnalysisBase extends BaseStep implements StepInterface {

	protected AnalysisBaseData data;
	protected AnalysisBaseMeta meta;

	public AnalysisBase(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (AnalysisBaseMeta) smi;
		data = (AnalysisBaseData) sdi;

		if (super.init(smi, sdi)) {
			
			data.nodeType = meta.getNodeType().toString();
			
			data.nodeName = environmentSubstitute(meta.getNodeName());
			data.nodeName = FastDateFormat.getInstance(data.nodeName).format(new Date());

			return true;
		}
		return false;

	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {
		meta = (AnalysisBaseMeta) smi;
		data = (AnalysisBaseData) sdi;

		Object[]  r = getRow(); // get row, set busy!
		
		try {
		
			if (first) {
				first = false;
	
				if ( r == null ) { // no more input to be expected...
					logBasic("未读取到数据,处理结束.");
			        setOutputDone();
			        return false;
			    }
				
				data.outputRowMeta = new RowMeta(); // start from scratch!
				meta.getFields(data.outputRowMeta, getStepname(), null, null, this, repository, metaStore);
				
				String[] fields = meta.getFieldNames() ;
				if ( !Utils.isEmpty( fields ) ) {
					data.fieldIndexes = new int[ fields.length ];
					for(int i = 0 ; i< fields.length ; i++) {
						data.fieldIndexes[ i ] = getInputRowMeta().indexOfValue( fields[i] );
						if ( data.fieldIndexes[ i ] < 0 ) {
							handleException("域名不存在.", new KettleStepException(  "Unable to find the specified fieldname '" + fields[i]  + "' for validation#" + ( i + 1 )));
						}
					}
				} else {
					handleException("域名列表为空.", new KettleStepException( "There is no name specified for field " ));
				}
			}
			
			if ( r == null ) { // no more input to be expected...
		        setOutputDone();
		        return false;
		    }
			
			if ( log.isRowLevel() ) {
				logRowlevel( "Read row #" + getLinesRead() + " : " + getInputRowMeta().getString( r ) );
			}
	
			Object[] result = new Object[data.outputRowMeta.size()];
			setFieldValue(result, AnalysisTypeFieldEnum.nodeName, data.nodeName); 
			setFieldValue(result, AnalysisTypeFieldEnum.nodeType, data.nodeType); 
			
			String[] fields = meta.getFieldNames() ;
			if ( !Utils.isEmpty( fields ) ) {
				for(int i = 0 ; i< fields.length ; i++) {
					int valueIndex = data.fieldIndexes[i];
					ValueMetaInterface valueMeta = getInputRowMeta().getValueMeta( valueIndex );
				    Object valueData = r[ valueIndex ];
					
				    setFieldValue(result,  AnalysisTypeFieldEnum.fieldName.toString() , fields[i] ); 
				    setFieldValue(result,  AnalysisTypeFieldEnum.value.toString() , valueMeta.getString(valueData) ); 
				    
				    if( ( valueData ==  null || Utils.isEmpty(valueMeta.getString(valueData))) ) {
						//值为空
				    	if(  meta.isNullable() ) {
				    		setFieldValue(result,  AnalysisTypeFieldEnum.result.toString() , true ); 
				    		setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString() , AnalysisBaseMeta.EMPTY_VALUE_REFERENCE ); 
				    	}else {
				    		setFieldValue(result,  AnalysisTypeFieldEnum.result.toString(), false ); 
				    		setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString(), AnalysisBaseMeta.EMPTY_VALUE_REFERENCE ); 
				    	}
					}else {
						 try {
					    	 validateFields(fields[i], valueMeta.getString(valueData), meta, result);
					    }catch( Exception e ) {
					    	handleException("校验域["+fields[i]+"],值["+valueMeta.getString(valueData)+"] 异常.", e);
					    	//如果忽略异常,则设置结果为校验错误
					    	setFieldValue(result,  AnalysisTypeFieldEnum.result.toString(), false ); 
					    	setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString() , AnalysisBaseMeta.ERROR_MATCH_REFERENCE ); 
					    }
					}
				    // Send out the good news: we found a row of data!
				    putRow(data.outputRowMeta, result);
				}
			} 
			
			return true;
			
		} catch (Exception e) {
			logError("步骤处理异常:",e);
			setErrors(1);
			stopAll();
			return false;
		}

	}

	/**
	 * 校验值 并给result赋值 
	 * @param fieldName 校验的域名
	 * @param valueMeta  校验的域类型
	 * @param valueData 校验的域值
	 * @param meta  AnalysisBaseMeta 对象
	 * @param result  结果对象,至少需要使用 setFieldValue方法为  referenceValue 和  result 域值赋值.
	 * @throws Exception
	 */
	public void validateFields(String fieldName ,String valueData ,Object smi ,Object[] result )  throws Exception{
		
		String standardValue = meta.getStandardValue();
		if( !Utils.isEmpty(standardValue) && validate(valueData, standardValue, true)){
			
			setFieldValue(result,  AnalysisTypeFieldEnum.result.toString(), true ); 
			setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString(), standardValue ); 
			return ;
		}
		
		String[] referenceValues = meta.getReferenceValues() ;
		if(referenceValues != null && referenceValues.length > 0 ) {
			for( String referenceValue : referenceValues) {
				if(!Utils.isEmpty(referenceValue) && validate(valueData, referenceValue, false)){
					
					setFieldValue(result,  AnalysisTypeFieldEnum.result.toString() , true ); 
					setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString() , referenceValue ); 
					return ;
				}
			}
		}
		//没有匹配上,格式错误
		setFieldValue(result,  AnalysisTypeFieldEnum.result.toString(), false ); 
		//猜测可能的参考值
		String guessReference = guessReference(valueData);
		if(Utils.isEmpty(guessReference)) {
			setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString() , AnalysisBaseMeta.NON_MATCH_REFERENCE ); 
		}else {
			setFieldValue(result,  AnalysisTypeFieldEnum.referenceValue.toString() , guessReference ); 
		}
		
		
	}
	
	/**
	 * 校验 数据值 和 数据模式 是否匹配 ,
	 * <br>
	 * validateFields方法调用,当重写validateFields方法时,自由实现
	 * <br>
	 * @param valueData  数据值
	 * @param format  数据模式 
	 * @param isStandard 是否是 标准值
	 * @return  true:匹配 , false:不匹配会校验下一个模式
	 */
	public abstract boolean validate(String valueData ,String format,boolean isStandard) ;

	/**
	 * 猜测参考值
	 *  <br>
	 * validateFields方法调用,当重写validateFields方法时,自由实现
	 * <br>
	 * @param valueData
	 * @return 返回猜测的参考值,可以为空
	 */
	public abstract String guessReference(String valueData ) ;
	
	
	public void setFieldValue( Object[] result , String analysisTypeField , Object value ) { 
		
		String fieldName = analysisTypeField ;
		switch(analysisTypeField) {
			case "nodeName" : fieldName = getVariable(data.nodeName+".NodeName", AnalysisTypeFieldEnum.nodeName.toString()) ;break;
			case "nodeType" : fieldName = getVariable(data.nodeName+".NodeType", AnalysisTypeFieldEnum.nodeType.toString()) ;break;
			case "fieldName" : fieldName =  getVariable(data.nodeName+".FieldName" , AnalysisTypeFieldEnum.fieldName.toString());break;
			case "value" : fieldName =  getVariable(data.nodeName+".Value" , AnalysisTypeFieldEnum.value.toString());break;
			case "referenceValue" : fieldName = getVariable(data.nodeName+".ReferenceValue" , AnalysisTypeFieldEnum.referenceValue.toString());break;
			case "result" : fieldName =  getVariable(data.nodeName+".Result" , AnalysisTypeFieldEnum.result.toString());break;
		}
		
		result[data.outputRowMeta.indexOfValue(fieldName)] = value;
	}
	
	private void handleException(String message , Exception e) throws Exception {

		message = Const.NVL(message, "处理异常:");
		
		if ( !meta.isIgnoreError()) {
			logError( message,e);
			throw e;
		}else {
			logBasic("[WARN]忽略异常! "+message+","+CloudLogger.getExceptionMessage(e));
		}
	}

	@Override
	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (AnalysisBaseMeta) smi;
		data = (AnalysisBaseData) sdi;

		super.dispose(smi, sdi);
	}
	
	
	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{},|）
	 * 
	 * @param keyword
	 * @return
	 */
	public  String escapeExprSpecialWord(String keyword) {
	    if (!Utils.isEmpty(keyword)) {
	        String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
	        for (String key : fbsArr) {
	            if (keyword.contains(key)) {
	                keyword = keyword.replace(key, "\\" + key);
	            }
	        }
	    }
	    return keyword;
	}
	
}
