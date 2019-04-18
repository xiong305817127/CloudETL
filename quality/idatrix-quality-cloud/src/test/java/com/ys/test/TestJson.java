package com.ys.test;

import org.codehaus.jackson.map.ObjectMapper;
import org.pentaho.di.core.util.OsgiBundleUtils;

import com.ys.idatrix.quality.dto.step.steps.report.SPAnalysisReport;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TestJson{

	public static void main(String[] args) throws Exception {

		Class<?> clazz = SPAnalysisReport.class ;
		
		Object obj = OsgiBundleUtils.newOsgiInstance(clazz,null);
		//对域赋值
		for( String fieldName : OsgiBundleUtils.getOsgiFieldNames(obj) ) {
			Field field = OsgiBundleUtils.seekOsgiField(clazz, fieldName, false);
			Class<?> fieldType =  field.getType() ;
			if( OsgiBundleUtils.baseTypeTransfor.containsKey(fieldType)) {
				continue ;
			}else if (fieldType.getSimpleName().contains("List")) {
				List<Object> fieldValue = new ArrayList<>();
				Type fc = field.getGenericType();
				if (fc instanceof ParameterizedType) {
					ParameterizedType pt = (ParameterizedType) fc;
					Class<?> genericClazz = (Class<?>) pt.getActualTypeArguments()[0];
					fieldValue.add(genericClazz.newInstance());
				}
				OsgiBundleUtils.setOsgiField(obj, fieldName, fieldValue, true);
			}else if(fieldType.isArray()) {
				
				Class<?> targetdtoClass = Class.forName(fieldType.getCanonicalName().replaceAll("\\[\\]", ""));
				Object[] fieldValue = (Object[]) Array.newInstance(targetdtoClass, 1);
				fieldValue[0] = targetdtoClass.newInstance() ;
				OsgiBundleUtils.setOsgiField(obj, fieldName, fieldValue, true);
			}else {
				OsgiBundleUtils.setOsgiField(obj, fieldName, fieldType.newInstance(), true);
			}
			
		}

		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(obj) ;
		System.out.println(json);
	}

}
