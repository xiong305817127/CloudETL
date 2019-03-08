/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.metastore;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.plugins.DatabasePluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.metastore.api.IMetaStore;
import org.pentaho.metastore.api.IMetaStoreAttribute;
import org.pentaho.metastore.api.IMetaStoreElement;
import org.pentaho.metastore.api.IMetaStoreElementType;
import org.pentaho.metastore.api.exceptions.MetaStoreException;
import org.pentaho.metastore.util.MetaStoreUtil;

public class DatabaseMetaStoreUtil extends MetaStoreUtil {

  public static List<DatabaseMeta> getDatabaseElements( IMetaStore metaStore ) throws MetaStoreException {
    List<DatabaseMeta> databases = new ArrayList<DatabaseMeta>();

    // If the data type doesn't exist, it's an empty list...
    //
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
    if ( elementType == null ) {
      return databases;
    }

		List<IMetaStoreElement> elements = metaStore.getElements(MetaStoreConst.NAMESPACE_IDATRIX, elementType);
    for ( IMetaStoreElement element : elements ) {
      try {
        DatabaseMeta databaseMeta = loadDatabaseMetaFromDatabaseElement( metaStore, element );
        databases.add( databaseMeta );
      } catch ( Exception e ) {
        throw new MetaStoreException( "Unable to load database from element with name '"
          + element.getName() + "' and type '" + elementType.getName() + "'", e );
      }
    }

    return databases;
  }

	public static DatabaseMeta getDatabaseElement(IMetaStore metaStore, String name) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, name);
			if (element != null) {
				try {
					return loadDatabaseMetaFromDatabaseElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load database from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}

		return null;
	}
	
	public static DatabaseMeta getDatabaseElementById(IMetaStore metaStore, String id) throws MetaStoreException {
		// If the data type doesn't exist, it's an empty list...
		//
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
		if (elementType != null) {
			IMetaStoreElement element = metaStore.getElement(MetaStoreConst.NAMESPACE_IDATRIX, elementType, id);
			if (element != null) {
				try {
					return loadDatabaseMetaFromDatabaseElement(metaStore, element);
				} catch (KettlePluginException e) {
					throw new MetaStoreException("Unable to load database from element with name '"
							+ element.getName() + "' and type '" + elementType.getName() + "'", e);
				}
			}
		}

		return null;
	}

  public static void createDatabaseElement( IMetaStore metaStore, DatabaseMeta databaseMeta ) throws MetaStoreException {

    // If the Pentaho namespace doesn't exist, create it!
    //
		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			metaStore.createNamespace(MetaStoreConst.NAMESPACE_IDATRIX);
    }

    // If the database connection element type doesn't exist, create it
    //
    IMetaStoreElementType elementType =
      metaStore.getElementTypeByName(
        MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
    if ( elementType == null ) {
      elementType = populateDatabaseElementType( metaStore );
      metaStore.createElementType(  MetaStoreConst.NAMESPACE_IDATRIX, elementType );
    }

    // populate an element, store it.
    //
    IMetaStoreElement databaseElement = populateDatabaseElement( metaStore, databaseMeta );

    // Store the element physically
    //
    metaStore.createElement(  MetaStoreConst.NAMESPACE_IDATRIX, elementType, databaseElement );
  }

  public static IMetaStoreElementType populateDatabaseElementType( IMetaStore metaStore ) throws MetaStoreException {

    // The new type will typically have an ID so all we need to do is give the type a name and a description.
    //
    IMetaStoreElementType elementType = metaStore.newElementType(  MetaStoreConst.NAMESPACE_IDATRIX );

    // Name and description...
    //
		elementType.setName(MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
		elementType.setDescription(MetaStoreConst.ELEMENT_TYPE_DESCRIPTION_DATABASE_CONNECTION);
    return elementType;
  }

  public static IMetaStoreElement populateDatabaseElement( IMetaStore metaStore, DatabaseMeta databaseMeta ) throws MetaStoreException {

		if (!metaStore.namespaceExists(MetaStoreConst.NAMESPACE_IDATRIX)) {
			throw new MetaStoreException("Namespace '" + MetaStoreConst.NAMESPACE_IDATRIX + "' doesn't exist.");
    }
		if (databaseMeta.getName() == null) {
			throw new MetaStoreException("Database connection name '" + MetaStoreConst.NAMESPACE_IDATRIX + "' can't be empty.");
		}

    // If the data type doesn't exist, error out...
    //
    IMetaStoreElementType elementType =
      metaStore.getElementTypeByName(
        MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION );
    if ( elementType == null ) {
      throw new MetaStoreException( "Unable to find the database connection type" );
    }

    elementType = populateDatabaseElementType( metaStore );

    // generate a new database element and populate it with metadata
    //
    IMetaStoreElement element = metaStore.newElement( elementType, databaseMeta.getName(), null );

    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_PLUGIN_ID, databaseMeta.getPluginId() ) );

    element.setName( databaseMeta.getName() );

    element.addChild( metaStore
      .newAttribute( MetaStoreConst.DB_ATTR_ID_DESCRIPTION, databaseMeta.getDescription() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_ACCESS_TYPE, databaseMeta
      .getAccessTypeDesc() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_HOSTNAME, databaseMeta.getHostname() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_PORT, databaseMeta
      .getDatabasePortNumberString() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_DATABASE_NAME, databaseMeta
      .getDatabaseName() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_USERNAME, databaseMeta.getUsername() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_PASSWORD, metaStore
      .getTwoWayPasswordEncoder().encode( databaseMeta.getPassword() ) ) );
    element
      .addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_SERVERNAME, databaseMeta.getServername() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_DATA_TABLESPACE, databaseMeta
      .getDataTablespace() ) );
    element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_INDEX_TABLESPACE, databaseMeta
      .getIndexTablespace() ) );

    IMetaStoreAttribute attributesChild = metaStore.newAttribute( MetaStoreConst.DB_ATTR_ID_ATTRIBUTES, null );
    element.addChild( attributesChild );

    // Now add a list of all the attributes set on the database connection...
    //
    Properties attributes = databaseMeta.getAttributes();
    Enumeration<Object> keys = databaseMeta.getAttributes().keys();
    while ( keys.hasMoreElements() ) {
      String code = (String) keys.nextElement();
      String attribute = (String) attributes.get( code );
      // Add it to the attributes child
      //
      attributesChild.addChild( metaStore.newAttribute( code, attribute ) );
    }

    // Extra information for 3rd-party tools:
    //
    // The driver class
    //
    element
      .addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_DRIVER_CLASS, databaseMeta.getDriverClass() ) );

    // The URL
    //
    try {
      element.addChild( metaStore.newAttribute( MetaStoreConst.DB_ATTR_JDBC_URL, databaseMeta.getURL() ) );
    } catch ( Exception e ) {
      throw new MetaStoreException( "Unable to assemble URL from database '" + databaseMeta.getName() + "'", e );
    }

    return element;
  }

  public static DatabaseMeta loadDatabaseMetaFromDatabaseElement( IMetaStore metaStore, IMetaStoreElement element ) throws KettlePluginException {
    DatabaseMeta databaseMeta = new DatabaseMeta();
    PluginRegistry pluginRegistry = PluginRegistry.getInstance();

    // Load the appropriate database plugin (database interface)
    //
    String pluginId = getChildString( element, MetaStoreConst.DB_ATTR_ID_PLUGIN_ID );
    String driverClassName = getChildString( element, MetaStoreConst.DB_ATTR_DRIVER_CLASS );
    if ( Utils.isEmpty( pluginId ) && Utils.isEmpty( driverClassName ) ) {
      throw new KettlePluginException( "The attributes 'plugin_id' and 'driver_class' can't be both empty" );
    }
    if ( Utils.isEmpty( pluginId ) ) {
      // Determine pluginId using the plugin registry.
      //
      List<PluginInterface> plugins = pluginRegistry.getPlugins( DatabasePluginType.class );
      for ( PluginInterface plugin : plugins ) {
        DatabaseInterface databaseInterface = (DatabaseInterface) pluginRegistry.loadClass( plugin );
        if ( driverClassName.equalsIgnoreCase( databaseInterface.getDriverClass() ) ) {
          pluginId = plugin.getIds()[0];
        }
      }
    }
    if ( Utils.isEmpty( pluginId ) ) {
      throw new KettlePluginException(
        "The 'plugin_id' attribute could not be determined using 'driver_class' value '" + driverClassName + "'" );
    }

    // Look for the plugin
    //
    PluginInterface plugin = PluginRegistry.getInstance().getPlugin( DatabasePluginType.class, pluginId );
    DatabaseInterface databaseInterface = (DatabaseInterface) PluginRegistry.getInstance().loadClass( plugin );
    databaseInterface.setPluginId( pluginId );
    databaseMeta.setDatabaseInterface( databaseInterface );

    databaseMeta.setObjectId( new StringObjectId( element.getId() ) );
    databaseMeta.setName( element.getName() );
    databaseMeta.setDescription( getChildString( element, MetaStoreConst.DB_ATTR_ID_DESCRIPTION ) );

    String accessTypeString = getChildString( element, MetaStoreConst.DB_ATTR_ID_ACCESS_TYPE );
    if ( Utils.isEmpty( accessTypeString ) ) {
      accessTypeString = DatabaseMeta.getAccessTypeDesc( DatabaseMeta.TYPE_ACCESS_NATIVE );
    }
    databaseMeta.setAccessType( DatabaseMeta.getAccessType( accessTypeString ) );

    databaseMeta.setHostname( getChildString( element, MetaStoreConst.DB_ATTR_ID_HOSTNAME ) );
    databaseMeta.setDBPort( getChildString( element, MetaStoreConst.DB_ATTR_ID_PORT ) );
    databaseMeta.setDBName( getChildString( element, MetaStoreConst.DB_ATTR_ID_DATABASE_NAME ) );
    databaseMeta.setUsername( getChildString( element, MetaStoreConst.DB_ATTR_ID_USERNAME ) );
    databaseMeta.setPassword( metaStore.getTwoWayPasswordEncoder().decode(
      getChildString( element, MetaStoreConst.DB_ATTR_ID_PASSWORD ) ) );
    databaseMeta.setServername( getChildString( element, MetaStoreConst.DB_ATTR_ID_SERVERNAME ) );
    databaseMeta.setDataTablespace( getChildString( element, MetaStoreConst.DB_ATTR_ID_DATA_TABLESPACE ) );
    databaseMeta.setIndexTablespace( getChildString( element, MetaStoreConst.DB_ATTR_ID_INDEX_TABLESPACE ) );

    Properties attributes = databaseMeta.getAttributes();
    IMetaStoreAttribute attributesChild = element.getChild( MetaStoreConst.DB_ATTR_ID_ATTRIBUTES );
    if ( attributesChild != null ) {
      // Now add a list of all the attributes set on the database connection...
      //
      for ( IMetaStoreAttribute attr : attributesChild.getChildren() ) {
        String code = attr.getId();
        String value = getAttributeString( attr );
        attributes.put( code, Const.NVL( value, "" ) );
      }
    }
    
    // Override properties defined in the idatrix configuration file.
    if ("true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("db.parameter.override"))) {
    	// Override properties
    	attributes.put("STREAM_RESULTS", "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("db.parameter.stream.results", "true")) ? "Y" : "N");
    	
    	// Override extra properties
    	databaseMeta.addExtraOption(databaseInterface.getPluginId(), "defaultFetchSize", IdatrixPropertyUtil.getProperty("db.parameter.defaultFetchSize", "1000"));
    	databaseMeta.addExtraOption(databaseInterface.getPluginId(),"useCursorFetch", IdatrixPropertyUtil.getProperty("db.parameter.useCursorFetch", "true"));
    }

    return databaseMeta;
  }

	public static void updateElement(IMetaStore metaStore, DatabaseMeta databaseMeta) throws MetaStoreException {
		// Populate element
		IMetaStoreElement databaseElement = populateDatabaseElement(metaStore, databaseMeta);

		// Find the existing element
		IMetaStoreElement de = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX,
				databaseElement.getElementType(), databaseElement.getName());
		if (de != null) {
			// Update the existing element
			metaStore.updateElement(MetaStoreConst.NAMESPACE_IDATRIX,
					metaStore.getElementTypeByName(MetaStoreConst.NAMESPACE_IDATRIX, databaseElement.getElementType().getName()),
					de.getId(), databaseElement);
		} else {
			// Create it newly
			metaStore.createElement(MetaStoreConst.NAMESPACE_IDATRIX, databaseElement.getElementType(), databaseElement);
		}
	}

	public static void deleteElement(IMetaStore metaStore, DatabaseMeta databaseMeta) throws MetaStoreException {
		IMetaStoreElementType elementType = metaStore.getElementTypeByName(
				MetaStoreConst.NAMESPACE_IDATRIX, MetaStoreConst.ELEMENT_TYPE_NAME_DATABASE_CONNECTION);
		if (elementType == null) {
			return;
		}
		
		// Find the existing element
		IMetaStoreElement databaseElement = metaStore.getElementByName(MetaStoreConst.NAMESPACE_IDATRIX, elementType, databaseMeta.getName());

		if (databaseElement != null) {
			metaStore.deleteElement(MetaStoreConst.NAMESPACE_IDATRIX, elementType, databaseElement.getId());
		}
	}
}
