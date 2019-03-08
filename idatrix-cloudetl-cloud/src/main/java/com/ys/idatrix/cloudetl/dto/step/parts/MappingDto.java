/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

import java.util.List;
import java.util.Map;

import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Lists;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.hbase.shim.api.HBaseValueMeta;

/**
 *  SPHBaseOutput 的  org.pentaho.hbase.shim.api.Mapping 域DTO,
 * @author XH
 * @since 2017年6月21日
 *
 */
public class MappingDto {
	
	private String mappingName;
	private String tableName;
	private String keyword;
	private String keyType;
	private List<MappedColumnDto> mappedColumns;
	
	private boolean tupleMapping;
	private String  tupleFamilies;
	
	public String getMappingName() {
		return mappingName;
	}
	public void setMappingName(String mappingName) {
		this.mappingName = mappingName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getKeyType() {
		return keyType;
	}
	public void setKeyType(String keyType) {
		this.keyType = keyType;
	}
	public List<MappedColumnDto> getMappedColumns() {
		return mappedColumns;
	}
	public void setMappedColumns(List<MappedColumnDto> mappedColumns) {
		this.mappedColumns = mappedColumns;
	}
	public boolean isTupleMapping() {
		return tupleMapping;
	}
	public void setTupleMapping(boolean tupleMapping) {
		this.tupleMapping = tupleMapping;
	}
	public String getTupleFamilies() {
		return tupleFamilies;
	}
	public void setTupleFamilies(String tupleFamilies) {
		this.tupleFamilies = tupleFamilies;
	}
	
	/**
	 * 将当前 MappingDto对象转换为  org.pentaho.bigdata.api.hbase.mapping.Mapping 对象
	 * @param hbaseService org.pentaho.bigdata.api.hbase.HBaseService
	 * @return org.pentaho.bigdata.api.hbase.mapping.Mapping
	 * @throws Exception
	 */
	public Object transToHbaseMapping(Object hbaseService ) throws Exception { 
		if(hbaseService == null|| Utils.isEmpty(getTableName()) || Utils.isEmpty(getMappingName())) {
			return null;
		}
		
		//Mapping mapping = hbaseService.getMappingFactory().createMapping(getTableName(), getMappingName());
		Object mapping = OsgiBundleUtils.invokeOsgiMethod( OsgiBundleUtils.invokeOsgiMethod(hbaseService, "getMappingFactory"),"createMapping",getTableName(), getMappingName());
		//mapping.setKeyName(getKeyword());
		OsgiBundleUtils.invokeOsgiMethod(mapping, "setKeyName", getKeyword());
		//mapping.setKeyTypeAsString(getKeyType());
		OsgiBundleUtils.invokeOsgiMethod(mapping, "setKeyTypeAsString", getKeyType());
		//mapping.setTupleFamilies(getTupleFamilies());
		OsgiBundleUtils.invokeOsgiMethod(mapping, "setTupleFamilies", getTupleFamilies());
		//mapping.setTupleMapping(isTupleMapping());
		OsgiBundleUtils.invokeOsgiMethod(mapping, "setTupleMapping", isTupleMapping());
		
		List<MappedColumnDto> columns = getMappedColumns();
		if(columns != null && columns.size() >0 ) {
			
			//com.pentaho.big.data.bundles.impl.shim.hbase.meta.HBaseValueMetaInterfaceFactoryImpl hbaseValueMetaInterfaceFactory =  hbaseService.getHBaseValueMetaInterfaceFactory() ;
			Object hbaseValueMetaInterfaceFactory = OsgiBundleUtils.invokeOsgiMethod(hbaseService, "getHBaseValueMetaInterfaceFactory");
			
			for(MappedColumnDto col : columns ) {
				 String alias = col.getAlias();
				 if(Utils.isEmpty(alias)) {
					 throw new Exception("列属性别名不能为空!");
				 }
			     String colFam = col.getColumnFamily();
			     if(colFam == null) { 
			          colFam = ""; 
			     }
			     String colName = col.getColumnName();
			     if(colName == null) {
			    	 colName = "";
			     }
			     
			     String combined = colFam + "," + colName + "," + alias; 
			     HBaseValueMeta hbvm = new HBaseValueMeta(combined, 0, -1, -1); 
			     
			     String type = col.getType(); 
			     hbvm.setHBaseTypeFromString(type); 
			     
			     String indexedV = col.getIndex();
			     if(!Utils.isEmpty(indexedV)) {// 512
			           Object[] ex = HBaseValueMeta.stringIndexListToObjects(indexedV);
			           hbvm.setIndex(ex);
			           hbvm.setStorageType(2);
			     }

			     //HBaseValueMetaInterface column = hhbaseValueMetaInterfaceFactory.copy(hbvm) ;
			     Object column = OsgiBundleUtils.invokeOsgiMethod(hbaseValueMetaInterfaceFactory, "copy", hbvm);
			     //mapping.addMappedColumn(column, isTupleMapping());
			     OsgiBundleUtils.invokeOsgiMethod(mapping, "addMappedColumn", column, isTupleMapping()) ;
			}
		}
		
		return mapping ;
	}
	
	/**
	 * 将  org.pentaho.bigdata.api.hbase.mapping.Mapping 对象 转换为 当前 MappingDto对象
	 * @param mapping org.pentaho.bigdata.api.hbase.mapping.Mapping
	 */
	@SuppressWarnings("rawtypes")
	public void transFromHbaseMapping( Object mapping  ) {
		if( mapping == null ) {
			return ;
		}
		setTableName( (String)OsgiBundleUtils.invokeOsgiMethod(mapping, "getTableName"));// mapping.getTableName());
		setMappingName( (String)OsgiBundleUtils.invokeOsgiMethod(mapping, "getMappingName"));//mapping.getMappingName());
		setKeyType( OsgiBundleUtils.invokeOsgiMethod(mapping, "getKeyType").toString());//mapping.getKeyType().toString());
		setKeyword( (String)OsgiBundleUtils.invokeOsgiMethod(mapping, "getKeyName"));//mapping.getKeyName());
		setTupleMapping( (boolean)OsgiBundleUtils.invokeOsgiMethod(mapping, "isTupleMapping"));//mapping.isTupleMapping());
		setTupleFamilies( (String)OsgiBundleUtils.invokeOsgiMethod(mapping, "getTupleFamilies"));//mapping.getTupleFamilies());
		
		//Map<String, HBaseValueMetaInterface> columnMappings = mapping.getMappedColumns();
		Map	columnMappings = (Map) OsgiBundleUtils.invokeOsgiMethod(mapping, "getMappedColumns" );
		if(columnMappings != null && columnMappings.size() >0) {
			
			List<MappedColumnDto> columns = Lists.newArrayList();
			for(Object col : columnMappings.values()) {
				
				MappedColumnDto colDto =  new MappedColumnDto();
				colDto.setAlias( (String)OsgiBundleUtils.invokeOsgiMethod(col, "getAlias"));//col.getAlias());
				colDto.setColumnFamily( (String)OsgiBundleUtils.invokeOsgiMethod(col, "getColumnFamily"));//col.getColumnFamily());
				colDto.setColumnName( (String)OsgiBundleUtils.invokeOsgiMethod(col, "getColumnName"));//col.getColumnName());
				colDto.setType( (String)OsgiBundleUtils.invokeOsgiMethod(col, "getTypeDesc"));//col.getTypeDesc());
				colDto.setIndex(HBaseValueMeta.objectIndexValuesToString( (Object[])OsgiBundleUtils.invokeOsgiMethod(col, "getIndex")));//col.getIndex()));
				columns.add( colDto);
			}
			setMappedColumns(columns);
		}
		
		
	}
	
	
	
}
