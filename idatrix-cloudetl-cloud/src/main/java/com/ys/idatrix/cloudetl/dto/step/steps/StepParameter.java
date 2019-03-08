/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import com.google.common.collect.Lists;

/**
 * A common interface for all steps to implement parameters parser,
 * basically it's implemented to encode and decode parameters per type.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public interface StepParameter {
	
	public static final Log  steplogger = LogFactory.getLog("StepParameter");
	
	/**
	 * 在新增组件时,为初始化步骤对象赋初值
	 * @param stepMeta
	 */
	default public void initParamObject(StepMeta stepMeta) {
	}
	
	/**
	 * 根据前端接口传递过来的JSON对象，获取封装后的参数对象
	 * @param json
	 * @return
	 */
	public Object getParameterObject(Object json);
	
	/**
	 * 把插件Meta编码成参数对象，其中插件Meta由OSGI容器加载的，需用反射方式调用成员方法
	 * @param stepMetaInterface
	 * @return
	 * @throws Exception
	 */
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception;
	
	/**
	 * 解码传递过来的参数对象，装入插件Meta中，其中插件Meta由OSGI容器加载的，需用反射方式调用成员方法
	 * @param stepMetaInterface
	 * @param po
	 * @param databases
	 * @param transMeta
	 * @throws Exception
	 */
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta) throws Exception;
	
	default  void setToAttribute(StepMeta stepMeta , String key , Object value ) {
		if(value == null ) {
			return ;
		}
		stepMeta.setAttribute("idatrix", key, value.toString());
	}

	default  String getToAttribute(StepMeta stepMeta , String key  ) {
		return stepMeta.getAttribute("idatrix", key);
	}
	
	default  Long getToAttributeLong(StepMeta stepMeta , String key  ) {
		String val = stepMeta.getAttribute("idatrix", key);
		if( Utils.isEmpty(val) ) {
			return null;
		}
		return Long.valueOf(val) ;
	}
	
	default  Object setToObject(Object source, Object target) throws Exception {      
		if (source == null || target ==  null ) {
			return target;
		}
		if(target instanceof Class) {
			target = OsgiBundleUtils.newOsgiInstance(target, null, null,null);
		}

		List<String> fields = OsgiBundleUtils.getOsgiFieldNames(source);
		if(fields != null && fields.size() >0) {
			for(String fieldName : fields) {
				Object sourceFieldVlaue = OsgiBundleUtils.getOsgiField(source, fieldName, true);
				if(sourceFieldVlaue != null ) {
					Field targetField = OsgiBundleUtils.seekOsgiField(target.getClass(), fieldName, true);
					if( targetField != null && OsgiBundleUtils.isSameClass(targetField.getType(), sourceFieldVlaue.getClass()) ) {
						OsgiBundleUtils.setOsgiField(target, fieldName, sourceFieldVlaue, true);
					}
				}
			}
		}
		return target;      
	} 
	
	default <T> List<T> transListToList(List<?> sources,DtoTransData<T> dtd){
		if(sources ==null || sources.size() ==0){
			return Lists.newArrayList();
		}
		List<T> result=Lists.newArrayList();
		for(int i=0;i<sources.size();i++){
			result.add( dtd.dealData( sources.get(i),i));
		}
		return result;
	}
	
	default <T> List<T> transArrayToList(Object[] sources,DtoTransData<T> dtd){
		if(sources ==null || sources.length ==0){
			return Lists.newArrayList();
		}
		List<T> result=Lists.newArrayList();
		for(int i=0;i<sources.length;i++){
			result.add( dtd.dealData( sources[i],i));
		}
		return result;
	}
	
	default <K,T> void transListToArray(List<K> sources,DtoTransData<T> dtd){
		if(sources ==null || sources.size() ==0){
			return ;
		}
		for(int i=0;i<sources.size();i++){
			dtd.dealData( sources.get(i),i);
		}
	}
	
	public interface DtoTransData<T>{
		 
		 T dealData(Object obj,int index);
		
	}
	
	
	
	
}



