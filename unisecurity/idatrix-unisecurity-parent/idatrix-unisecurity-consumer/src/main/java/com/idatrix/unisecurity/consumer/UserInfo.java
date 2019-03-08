package com.idatrix.unisecurity.consumer;

/*
import com.ys.idatrix.cloudetl.metacube.api.dto.DbTablesDto;
import com.ys.idatrix.cloudetl.metacube.api.dto.MetaCubeDbDto;
import com.ys.idatrix.cloudetl.metacube.api.service.CloudDbInfoService;

*/
/**
 */
 	/*
public class UserInfo {
	
	
    public static void main(String[] args) throws Exception {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:consumer-spring.xml");
        context.start();
        
        if(true){
        // 获取服务信息
        CloudDbInfoService service = (CloudDbInfoService)context.getBean("cloudDbInfoService");
        
        MetaCubeDbDto dto = new MetaCubeDbDto();
        dto.setHostname("172.16.22.25");//172.16.22.25,10.0.0.88
        dto.setName("ORCL");
//        dto.setAccessType("Native");
        dto.setDatabaseName("ORCL");
        dto.setPluginId("ORACLE");
        dto.setType("ORACLE");
        dto.setUsername("qxdsj");
        dto.setPassword("qxdsj_2017");
        dto.setPort("1521");
//        dto.setSchemaName("ORCL");
//        dto.setTableName("idatrix_unisecurity_mail_log");
        
//        DbSchemaDto resl = service.getDbSchema(dto);
//        DbTableFieldsDto resl = service.getTableFields(dto);
        
//        System.out.println("DEBUG >>> DUBBO TESTING ... >>> " + JSON.toJSONString(dto));
          DbTablesDto resl = service.getDbTables(dto);
        
//        MovieDubboService  movieDubboService = (MovieDubboService)context.getBean("movieDubboService"); 
//        MovieDto movieDto = movieDubboService.getMovieByTitle("The Matrix 2000");
		System.out.println("DEBUG >>> DUBBO TESTING ... >>> " + JSON.toJSONString(resl));
        
        }
        
//        
//        GraphDubboService service = (GraphDubboService)context.getBean("graphDubboService");
//        
//        NodeSaveRequestDto req = new NodeSaveRequestDto();
//        
//        HeadDto head = new HeadDto();
//        head.setFlag(GraphConst.FLAG_SAVE_NODE);
//        req.setHead(head);
//        
//        
//        List<NodeDto> nodes = new ArrayList<NodeDto>();
//        NodeDto node = new NodeDto();
//        
//        node.setLevel(20);
//        
//        
//        	// Label Properties
//    	HashMap<String, Object> lp = new HashMap<>();
// 		lp.put("catalog","test...");
// 		lp.put("flag","slave");
// 		lp.put("graph","ETL1");
// 		node.setLabelProperties(lp);
//        
//    	// Base Properties
//    	HashMap<String, Object> bp = new HashMap<>();
//    	bp.put("name","table tst1");
// 		bp.put("remark","table 1");
// 		bp.put("status","Unavailable");
// 		bp.put("title","数据库1");
// 		bp.put("user","中华曲库");
// 		bp.put("createTime",new Date());
// 		bp.put("updateTime",new Date());
// 		node.setBaseProperties(bp);
//     		
//        
// 		HashMap<String, Object> nodeProperties = new HashMap<>();
// 		nodeProperties.put("owner", "graph neo4j");
// 		nodeProperties.put("type", "mysql");
// 		nodeProperties.put("charset", "utf8");
//        node.setNodeProperties(nodeProperties);
//        
//        // Other Properties
// 		HashMap<String, Object> ops = new HashMap<>();
// 		ops.put("key1", "value1");
// 		node.setOtherProperties(ops);
// 		
// 		
// 		// guid 
// 		HashMap<String, Object> guid = new HashMap<>();
// 		guid.put("system", "data map");
// 		guid.put("database", "neo4j");
//        node.setGuid(guid);
//        
//        nodes.add(node);
//        req.setNodes(nodes);
//        
//        service.saveNode(req);
    
    }
}
*/