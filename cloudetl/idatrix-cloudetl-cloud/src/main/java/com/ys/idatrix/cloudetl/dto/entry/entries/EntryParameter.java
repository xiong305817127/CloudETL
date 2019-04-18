/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.entries;

import java.util.List;

import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import mondrian.olap.Util;

/**
 * A common interface for all entries to implement parameters parser,
 * basically it's implemented to encode and decode parameters per type.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public interface EntryParameter {

	/**
	 * 根据前端接口传递过来的JSON对象，获取封装后的参数对象
	 * @param json
	 * @return
	 */
	public Object getParameterObject(Object json);

	/**
	 * 把插件Meta编码成参数对象，其中插件Meta由OSGI容器加载的，需用反射方式调用成员方法
	 * @param entryMetaInterface
	 * @return
	 * @throws Exception
	 */
	public Object encodeParameterObject(JobEntryCopy jobEntryCopy) throws Exception;

	/**
	 * 解码传递过来的参数对象，装入插件Meta中，其中插件Meta由OSGI容器加载的，需用反射方式调用成员方法
	 * @param entryMetaInterface
	 * @param po
	 * @param jobMeta
	 * @throws Exception
	 */
	public void decodeParameterObject(JobEntryCopy jobEntryCopy, Object po, JobMeta jobMeta) throws Exception;
	
	
	default  void setToAttribute(JobEntryCopy jobEntryCopy , String key , Object value ) {
		jobEntryCopy.setAttribute("idatrix", key, value.toString());
	}

	default  String getToAttribute(JobEntryCopy jobEntryCopy , String key  ) {
		return jobEntryCopy.getAttribute("idatrix", key);
	}
	
	default  Long getToAttributeLong(JobEntryCopy jobEntryCopy , String key  ) {
		String val = jobEntryCopy.getAttribute("idatrix", key);
		if( Utils.isEmpty(val) ) {
			return null;
		}
		return Long.valueOf(val) ;
	}

	default  Object objectToObject(Object source, Object target) throws Exception {      
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
				if(sourceFieldVlaue != null && !Util.isEmpty(sourceFieldVlaue.toString())) {
					OsgiBundleUtils.setOsgiField(target, fieldName, sourceFieldVlaue, true);
				}
			}
		}
		return target;      
	}      

}
