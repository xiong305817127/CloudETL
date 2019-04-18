import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

public class TestESHttp {

	public static void main(String[] args) throws IOException {

		  String serverIp= "10.0.0.85";
		  int serverPort = 9202;
		  
		  RestClient client = RestClient.builder( new HttpHost(serverIp, serverPort, "http")).build(); 

		  //createIndex(client);
		 //updateIndex(client);
		  
		  Map<String, String> paramMap = new HashMap<String, String>();
		  paramMap.put("analyzer", "my_synonyms");
		  paramMap.put("text", "全世界");
		  paramMap.put("pretty", "true");
		  Response response = client.performRequest("GET",   "/synonym_index/_analyze", paramMap);
		  System.out.println(EntityUtils.toString(response.getEntity()));

		  client.close();

	}
	
	
	public static void updateIndex(  RestClient client ) throws IOException {
		
		Response response = client.performRequest("POST",  "/synonym_index/_close", new HashMap<>());
		 System.out.println(EntityUtils.toString(response.getEntity()));
		 
		 String cofigStr = "{" + 
		 		"			    \"analysis\": {" + 
		 		"			      \"filter\": {" + 
		 		"			        \"my_synonym_filter\": {" + 
		 		"			          \"type\": \"synonym\", " + 
		 		"			          \"synonyms\": [ " + 
		 		"			            \"中国,地球,全世界,中华人民共和国\"" + 
		 		"			          ]" + 
		 		"			        }" + 
		 		"			      }," + 
		 		"			      \"analyzer\": {" + 
		 		"			        \"my_synonyms\": {" + 
		 		"			          \"tokenizer\": \"ik_smart\"," + 
		 		"			          \"filter\": [" + 
		 		"			            \"lowercase\"," + 
		 		"			            \"my_synonym_filter\" " + 
		 		"			          ]" + 
		 		"			        }" + 
		 		"			      }" + 
		 		"			    }" + 
		 		"			}" ;
				 
		 HttpEntity entity = new NStringEntity(cofigStr, ContentType.APPLICATION_JSON);
		response = client.performRequest("PUT",  "/synonym_index/_settings", new HashMap<>(),entity);
		 System.out.println(EntityUtils.toString(response.getEntity()));
		 
		 response = client.performRequest("POST",  "/synonym_index/_open", new HashMap<>());
		 System.out.println(EntityUtils.toString(response.getEntity()));
		
	}
	
	public static void createIndex(  RestClient client ) throws IOException {
		
		 
		 String cofigStr = "{" + 
				 "			 \"settings\": {" + 
		 		"			    \"analysis\": {" + 
		 		"			      \"filter\": {" + 
		 		"			        \"my_synonym_filter\": {" + 
		 		"			          \"type\": \"synonym\", " + 
		 		"			          \"synonyms\": [ " + 
		 		"			            \"tdtd,土豆,马铃薯,tudou,td,TD => 土豆\"," + 
		 		"			            \"字母,a,b,c,d,e,=>字母\"," + 
		 		"			            \"中国,地球,全世界,中华人民共和国\"" + 
		 		"			          ]" + 
		 		"			        }" + 
		 		"			      }," + 
		 		"			      \"analyzer\": {" + 
		 		"			        \"my_synonyms\": {" + 
		 		"			          \"tokenizer\": \"ik_smart\"," + 
		 		"			          \"filter\": [" + 
		 		"			            \"lowercase\"," + 
		 		"			            \"my_synonym_filter\" " + 
		 		"			          ]" + 
		 		"			        }" + 
		 		"			      }" + 
		 		"			    }" + 
		 		"			  }" + 
		 		"			}" ;
				 
		 HttpEntity entity = new NStringEntity(cofigStr, ContentType.APPLICATION_JSON);
		Response response = client.performRequest("PUT",  "/synonym_index", new HashMap<>(),entity);
		 System.out.println(EntityUtils.toString(response.getEntity()));
		 
	}

}
