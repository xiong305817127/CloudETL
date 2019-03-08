package com.idatrix.unisecurity.common.utils;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.idatrix.unisecurity.anotation.IdatrixMaxLen;
import com.idatrix.unisecurity.anotation.IdatrixPattern;
import com.idatrix.unisecurity.anotation.NotBlank;

public class ValidateUtil {
	      
	/**
	 * 循环遍历每个field，根据 IdatrixPattern，NotBlank
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static <T> String validate(T obj)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder errors = new StringBuilder();
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(NotBlank.class)) {
				NotBlank annotation = field.getAnnotation(NotBlank.class);
				Object value = field.get(obj);
				if (value == null || value=="") {
					errors.append(";"+annotation.message());
				}
			}
			 if(field.isAnnotationPresent(IdatrixMaxLen.class)){
				IdatrixMaxLen annotation = field.getAnnotation(IdatrixMaxLen.class);
				String value = (String) field.get(obj);
				// 如value 为空。直接continue
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				int maxLen = annotation.maxLen();
				if(value.length()>maxLen){
					errors.append(";"+annotation.message());
				}
			}
			 if (field.isAnnotationPresent(IdatrixPattern.class)) {
				IdatrixPattern annotation = field.getAnnotation(IdatrixPattern.class);
				String value = (String) field.get(obj);
				// 如value 为空。直接continue
				if (StringUtils.isEmpty(value)) {
					continue;
				}

				String regexp = annotation.regexp();
				Pattern p = Pattern.compile(regexp);
				Matcher m = p.matcher(value);
				if (!m.matches()) {
					errors.append(";"+annotation.message());
				}
			}
		}
		// 
		String result  = errors.toString();
		if(StringUtils.isNotEmpty(result)){
			result=result.substring(1);
		}
		return result;
	}  
}
