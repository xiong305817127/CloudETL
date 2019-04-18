package com.idatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.ast.statement.SQLUniqueConstraint;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.util.JdbcConstants;
import com.ys.idatrix.metacube.common.enums.DBEnum;

public class DirectMiningTest {

	public static void main(String[] args) {


		//String sql = "show create table ETL_EXEC_RECORD" ; 
		//select table_name,dbms_metadata.get_ddl('TABLE','TABLEA')from dual,user_tables where table_name='TABLEA'; 
		
		//String sql = "CREATE ALGORITHM=UNDEFINED DEFINER=`wuguozhou`@`%` SQL SECURITY DEFINER VIEW `exec_view` AS select `ETL_EXEC_EXCEPTION`.`EXECID` AS `EXECID`,`ETL_EXEC_EXCEPTION`.`RENTERID` AS `RENTERID`,`ETL_EXEC_EXCEPTION`.`OWNER` AS `OWNER`,`ETL_EXEC_EXCEPTION`.`NAME` AS `NAME`,`ETL_EXEC_EXCEPTION`.`TYPE` AS `TYPE`,`ETL_EXEC_EXCEPTION`.`POSITION` AS `POSITION`,`ETL_EXEC_EXCEPTION`.`UPDATEDATE` AS `UPDATEDATE`,`ETL_EXEC_EXCEPTION`.`EXCEPTIONDETAIL` AS `EXCEPTIONDETAIL`,`ETL_EXEC_EXCEPTION`.`INPUTSOURCE` AS `INPUTSOURCE`,`ETL_EXEC_EXCEPTION`.`OUTPUTSOURCE` AS `OUTPUTSOURCE`,`ETL_EXEC_EXCEPTION`.`EXECSOURCE` AS `EXECSOURCE`,`ETL_EXEC_RECORD`.`STATUS` AS `STATUS`,`ETL_EXEC_RECORD`.`BEGIN` AS `BEGIN`,`ETL_EXEC_RECORD`.`END` AS `END`,`ETL_EXEC_RECORD`.`SUCCESSFAILTIMES` AS `SUCCESSFAILTIMES`,`ETL_EXEC_RECORD`.`READLINES` AS `READLINES`,`ETL_EXEC_RECORD`.`WRITELINES` AS `WRITELINES`,`ETL_EXEC_RECORD`.`CONFIGURATION` AS `CONFIGURATION`,`ETL_EXEC_RECORD`.`LOGPATH` AS `LOGPATH`,`ETL_EXEC_RECORD`.`INPUTLINES` AS `INPUTLINES`,`ETL_EXEC_RECORD`.`OUTPUTLINES` AS `OUTPUTLINES`,`ETL_EXEC_RECORD`.`UPDATELINES` AS `UPDATELINES`,`ETL_EXEC_RECORD`.`ERRORLINES` AS `ERRORLINES` from (`ETL_EXEC_EXCEPTION` join `ETL_EXEC_RECORD`) where (`ETL_EXEC_EXCEPTION`.`EXECID` = `ETL_EXEC_RECORD`.`EXECID`) " ;
		String sql = getCreateSql() ;
		String dbType = JdbcConstants.MYSQL ;
		
		List<SQLStatement> statement = SQLUtils.parseStatements(sql, dbType) ;
		SQLStatement stmt1 = statement.get(0);
		MySqlCreateTableStatement stmt = (MySqlCreateTableStatement)statement.get(0);
		// SQLStatement stmt = statement.get(0);
		
		stmt.findForeignKey().forEach(sqlForeign -> {
			
			MysqlForeignKey foreign = ((MysqlForeignKey)sqlForeign) ;
			
			//TableFkMysql fk = new TableFkMysql() ;
			System.out.println("fk.name : "+foreign.getName().getSimpleName());
			System.out.println("fk.columns : "+String.join(",",foreign.getReferencingColumns().stream().map(rc -> { return  rc.getSimpleName(); } ).collect(Collectors.toList())));
			System.out.println("fk.ref.schema : "+foreign.getReferencedTable().getSchema());
			System.out.println("fk.ref.table : "+foreign.getReferencedTableName().getSimpleName());
			System.out.println("fk.ref.columns : "+String.join(",",foreign.getReferencedColumns().stream().map(rc -> { return  rc.getSimpleName(); } ).collect(Collectors.toList())));
			System.out.println("fk.onDelete : "+foreign.getOnDelete().name ) ;
			System.out.println("fk.onUpdate : "+foreign.getOnUpdate().name ) ;
		});
		
		System.out.println("pk : "+ String.join(",",stmt.findPrimaryKey().getColumns().stream().map(rc -> { return  ((SQLIdentifierExpr)rc.getExpr()).getName(); } ).collect(Collectors.toList())) );
		
		stmt.getTableElementList().forEach(SQLColumnDefinition -> {
			SQLTableElement element =  SQLColumnDefinition ;
			
			boolean isUnique = false; 
			SQLName indexName = null; 
			List<String> columnStr = new ArrayList<>(); 
			
			if (element instanceof SQLUniqueConstraint) {
                SQLUniqueConstraint unique = (SQLUniqueConstraint) element;
                isUnique = true ;
                indexName = unique.getName();
                for (SQLSelectOrderByItem item : unique.getColumns()) {
                    SQLExpr columnExpr = item.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        columnStr.add(keyColumName) ;
                    }
                }

            } else if (element instanceof MySqlTableIndex) {
            	MySqlTableIndex indexs = (MySqlTableIndex) element ;
            	isUnique = false;
            	indexName = indexs.getName();
                for (SQLSelectOrderByItem orderByItem : indexs.getColumns() ) {
                    SQLExpr columnExpr = orderByItem.getExpr();
                    if (columnExpr instanceof SQLIdentifierExpr) {
                        String keyColumName = ((SQLIdentifierExpr) columnExpr).getName();
                        keyColumName = SQLUtils.normalize(keyColumName);
                        columnStr.add(keyColumName) ;
                    }
                }
            }
			if( columnStr != null && !columnStr.isEmpty()) {
				System.out.println("index============================ ");
				 if( element instanceof MySqlKey) {
	                    String type = ((MySqlKey)element ).getIndexType()  ;
	                    if( !StringUtils.isEmpty(type)) {
	                    	try {
	                    		//如果是方法
	                    		if( DBEnum.MysqlIndexMethodEnum.valueOf(type) != null ) {
	                    			System.out.println("index.uniqu.method : "+type ) ;
	                        	}
	                    	}catch(Exception ignore1) { }
	                    	try {
	                    		//如果是类型
	                    		if( DBEnum.MysqlIndexTypeEnum.valueOf(type) != null ) {
	                    			System.out.println("index.uniqu.type : "+type ) ;
	                        	}
	                    	}catch(Exception ignore1) { }
	                    	
	                    }
	                }
				if( isUnique ) {
					
					System.out.println("index.unique.name : "+ ( (indexName!= null)? indexName.getSimpleName():"-" ) ) ;
					System.out.println("index.uniqu.column : "+String.join(",",columnStr) ) ;
				}else {
					
					System.out.println("index.name : "+( (indexName!= null)? indexName.getSimpleName():"-" ) ) ;
					 System.out.println("index.column : "+String.join(",",columnStr) ) ;
				}
				 
				isUnique=false;
				indexName = null;
				columnStr.clear();
			}
			
		});
		
		stmt.forEachColumn(SQLColumnDefinition ->{
			
			System.out.println("column============================ ");
			
			SQLColumnDefinition element =  SQLColumnDefinition ;
			System.out.println("column.name : "+element.getName().getSimpleName());
			System.out.println("column.type : "+element.getDataType().getName());
			System.out.println("column.length : "+ ( element.getDataType().getArguments().size()>0? element.getDataType().getArguments().get(0): null ) );
			System.out.println("column.typePrecision : "+ ( element.getDataType().getArguments().size()>1? element.getDataType().getArguments().get(1): null ) );
			System.out.println("column.isPk : "+ stmt.isPrimaryColumn(element.getName().getSimpleName()) );
			System.out.println("column.isAutoIncrement : "+ element.isAutoIncrement() );
			System.out.println("column.isNull : "+ (!element.containsNotNullConstaint()) );
			System.out.println("column.defaultValue : "+ element.getDefaultExpr());
			System.out.println("column.isUnsigned : "+ ( ( element.getDataType() instanceof SQLDataTypeImpl )? ((SQLDataTypeImpl)element.getDataType()).isUnsigned() : "" ) );
			System.out.println("column.description : "+ element.getComment());
			
			
		});
		
		
//		SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
//		stmt.accept(statVisitor);
//		System.out.println(statVisitor.getRelationships());
//		System.out.println(statVisitor.getTables()); //{t_org=Create}
//		System.out.println(statVisitor.getColumns()); // [t_org.fid, t_org.name]
		
		
//		System.out.println(statement);
//		create.get
//		
//	     SQLStatement statement = parser.parseStatement();
//	        MySqlInsertStatement insert = (MySqlInsertStatement)statement;
//	        String tableName = StringUtil.removeBackquote(insert.getTableName().getSimpleName());
		

	}

	private static String getCreateSql() {
		
		return "CREATE TABLE `sz_administrative_division` (\r\n" + 
				"  `administrative_division_id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '行政区域id',\r\n" + 
				"  `level` tinyint(3) NOT NULL DEFAULT '0' COMMENT '行政区域级别，1：省，2：市，3：区县，4：镇，5：村',\r\n" + 
				"  `node` varchar(5000) NOT NULL DEFAULT '' COMMENT '节点关系，上级到下级，1，2,3,4',\r\n" + 
				"  `parent` int(11) NOT NULL DEFAULT '0' COMMENT '上级行政区域id',\r\n" + 
				"  `name` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区域名称',\r\n" + 
				"  `code` varchar(255) NOT NULL DEFAULT '' COMMENT '行政区域编号',\r\n" + 
				"  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',\r\n" + 
				"  `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '行政区域状态，1：启用，0：禁用',\r\n" + 
				"  `delete` tinyint(2) NOT NULL DEFAULT '1' COMMENT '是否删除，1：正常，0删除',\r\n" + 
				"  `created_at` int(11) DEFAULT '0' COMMENT '创建时间',\r\n" + 
				"  `order` int(11) unsigned DEFAULT '0' COMMENT '序号',\r\n" + 
				"  `farmer_number` int(11) unsigned DEFAULT '0' COMMENT '农户数量（户）',\r\n" + 
				"  `node_self` text COMMENT '包含自身id的节点关系',\r\n" + 
				"  PRIMARY KEY (`administrative_division_id`),\r\n" + 
				"  KEY `parent` (`parent`),\r\n" + 
				"  FULLTEXT KEY `node` (`node`)\r\n" + 
				") ENGINE=InnoDB AUTO_INCREMENT=1912 DEFAULT CHARSET=utf8 COMMENT='行政区域表' ";
		
		
//		return ""
//				+ "CREATE TABLE `ETL_EXEC_RECORD_PART` (\r\n" + 
//				"  `EXECID` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `OPERATOR` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `NAME` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `TYPE` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `BEGIN` datetime DEFAULT NULL,\r\n" + 
//				"  `END` datetime DEFAULT NULL,\r\n" + 
//				"  `STATUS` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `READLINES` decimal(15,2) DEFAULT NULL,\r\n" + 
//				"  `WRITELINES` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '临时主键',\r\n" + 
//				"  `ID` varchar(255) DEFAULT 'aaaa' COMMENT '真正的主键',\r\n" + 
//				"  `LOGPATH` varchar(255) DEFAULT NULL,\r\n" + 
//				"  `INPUTLINES` bigint(20) unsigned DEFAULT NULL,\r\n" + 
//				"  `OUTPUTLINES` bigint(20) unsigned DEFAULT NULL,\r\n" + 
//				"  `UPDATELINES` bigint(20) DEFAULT NULL,\r\n" + 
//				"  `ERRORLINES` bigint(20) DEFAULT NULL,\r\n" + 
//				"  PRIMARY KEY (`WRITELINES`),\r\n" + 
//				"  UNIQUE KEY `id_idex` (`ID`),\r\n" + 
//				"  KEY `EXECID` (`EXECID`),\r\n" + 
//				"  KEY `ont_index_part` (`OPERATOR`,`NAME`,`TYPE`) USING HASH,\r\n" + 
//				"  CONSTRAINT `for_execId` FOREIGN KEY (`EXECID`) REFERENCES `kettleDB`.`ETL_EXEC_RECORD_PART` (`ID`) ON DELETE CASCADE ON UPDATE SET NULL\r\n" + 
//				") ENGINE=InnoDB DEFAULT CHARSET=utf8" ;
	}
	
	
	
	
}
