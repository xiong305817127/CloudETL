/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.ext.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.StringUtils;

/**
 * @deprecated - to be removed.
 * @author JW
 */
public class JsonArray extends ArrayList<Object> {
	
	private static final long serialVersionUID = -5155726035624596396L;

	public JsonArray() {
		
	}
	
	public JsonArray(int initialCapacity) {
		super(initialCapacity);
	}

	public JsonArray(List<Object> list) {
		if(list != null)
			this.addAll(list);
	}

	public static JsonArray fromObject(String json) throws JsonParseException, JsonMappingException, IOException {
		JsonArray jsonArray = new JsonArray();
		if(!StringUtils.hasText(json))
			return jsonArray;
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, JsonArray.class);
	}
	
	public static JsonArray fromObject(List<Object> list) throws JsonParseException, JsonMappingException, IOException {
		return new JsonArray(list);
	}

	public String getString(int i) {
		return (String) get(i);
	}
	
	public JsonObject getJsonObject(int i) {
		@SuppressWarnings("unchecked")
		Map<String, Object> m = (Map<String, Object>) get(i);
		return new JsonObject(m);
	}
	
	public String toString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	public static void main(String[] args) throws IOException {
		String fragment = null;
		ClassPathResource cpr = new ClassPathResource("common_jndi.json", DatabaseController.class);
		System.out.println(fragment = FileUtils.readFileToString(cpr.getFile(), "utf-8"));
		JsonArray jsonArray = JsonArray.fromObject(fragment);
	}
	*/
	
}
