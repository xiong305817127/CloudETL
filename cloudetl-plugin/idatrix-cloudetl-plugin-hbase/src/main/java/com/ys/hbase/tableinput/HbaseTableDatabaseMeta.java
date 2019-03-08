package com.ys.hbase.tableinput;

import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;

@DatabaseMetaPlugin( type = "HBASETABLE", typeDescription = "Phoenix Hbase Table" )
public class HbaseTableDatabaseMeta extends GenericDatabaseMeta{
	
	public HbaseTableDatabaseMeta(){
		super();
		getAttributes().put("EXTRA_OPTION_HBASETABLE.phoenix.schema.isNamespaceMappingEnabled", "true");
	}

	 @Override
	  public String getDriverClass() {
		 return "org.apache.phoenix.jdbc.PhoenixDriver";
	 }
	 
	  @Override
	  public boolean supportsOptionsInURL() {
		  // JW: set false for generic database connecting to HBase with phoenix driver.
		  return false;
	  }
	 
}
