package com.ys.idatrix.cloudetl.dm7;

import org.pentaho.di.core.database.OracleDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.plugins.DatabaseMetaPlugin;
import org.pentaho.di.core.util.IdatrixPropertyUtil;

@DatabaseMetaPlugin( type = "DM7", typeDescription = "dameng table" )
public class DM7TableDatabaseMeta extends OracleDatabaseMeta{
	
	 @Override
	  public int getDefaultDatabasePort() {
	    return 5236;
	  }
	 
	  @Override
	  public String getDriverClass() {
	     return "dm.jdbc.driver.DmDriver";
	  }
	  
	  @Override
	  public String getURL( String hostname, String port, String databaseName ) throws KettleDatabaseException {
		  return "jdbc:dm://"+hostname+":"+port+"/"+databaseName;
	  }
	  
	  @Override
	  public String[] getUsedLibraries() {
	    return new String[] { "Dm7JdbcDriver17.jar" };
	  }

	  @Override
	  public boolean isQuoteAllFields() {
		  Boolean quoteAllField = IdatrixPropertyUtil.getBooleanProperty("idatrix.database.dm7.quote.all.field",true);
		  if(quoteAllField) {
			  return true;
		  }else {
			  return super.isQuoteAllFields();
		  }
	  }
	  
	  
	  
}
