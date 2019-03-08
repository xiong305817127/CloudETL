package com.ys.idatrix.cloudetl.ext.utils;


import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	public final static ObjectMapper mapper = new ObjectMapper();
	static {
//		mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static ObjectMapper getMapper() {
		return mapper ;
	}
	
	public static <T> T transJsonToObject(String content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		 return mapper.readValue(content, valueType);
	}
	
	public static String transObjectTojson(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj) ;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T transObjectToObject(Object content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		if(content.getClass()==valueType) {
			return (T) content;
		}
		String json ="{}";
		if(content instanceof String) {
			json  = (String) content;
		}else {
			json = transObjectTojson(content);
		}
		return mapper.readValue(json, valueType);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> transJsontToList(String content, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		JavaType collectionType = mapper.getTypeFactory().constructParametricType(List.class, valueType);
		return (List<T>)mapper.readValue(content, collectionType) ;
	}
	
	
	
	
}
