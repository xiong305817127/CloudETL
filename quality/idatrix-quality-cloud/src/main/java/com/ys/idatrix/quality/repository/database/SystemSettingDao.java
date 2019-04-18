/**
 * 
 */
package com.ys.idatrix.quality.repository.database;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper;
import com.ys.idatrix.quality.repository.database.dto.SystemSettingsDto;

/**
 * Cloud repository implementation,
 *	- for transformation (ktr) and jobs (kjb) storage.
 *
 * @author JW
 * @since 05-12-2017
 * 
 */
public class SystemSettingDao {

	public static final Log  logger = LogFactory.getLog("CloudSystemSettingDao");
	
	private static final Class<SystemSettingsDto> TABLE_CLASS = SystemSettingsDto.class;
	
	private static boolean isDatabaseinit = false;
	
	public static void init() throws Exception {
		
		if(!isDatabaseinit) {
			DatabaseHelper.createOrUpdateTableifNonexist(TABLE_CLASS,  null);
			isDatabaseinit = true ;
		}
	}

	private final String orderbyConstant = " UPDATETIME DESC " ;
	private Map<String,Object> cacheSettings;
	public SystemSettingDao() {
		super();
		this.cacheSettings = Maps.newConcurrentMap();
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SystemSettingDao systemDao ;
	public static SystemSettingDao getInstance( ) {
		if (systemDao == null) {
			synchronized (SystemSettingDao.class) {
				if (systemDao == null) {
					systemDao = new SystemSettingDao();
				}
			}
		}
		return systemDao;
	}
	
	
	
	
	//=================================================== method =====================================================================
	
	
	public  List<SystemSettingsDto> getSettings( ) throws Exception{
		String renterId = CloudSession.getLoginRenterId() ;
		return getSettings(renterId);
	}
	
	@SuppressWarnings("unchecked")
	public  List<SystemSettingsDto> getSettings(String renterId  ) throws Exception{
		if(cacheSettings.containsKey(renterId)) {
			return (List<SystemSettingsDto>) cacheSettings.get(renterId);
		}
		
		List<SystemSettingsDto> settings = DatabaseHelper.queryList(TABLE_CLASS, null, null, new String[] {"RENTERID"}, new String[] {"="}, new String[] {renterId}, orderbyConstant);
		if(settings == null  ) {
			return Lists.newArrayList();
		}
		cacheSettings.put(renterId, settings);
		
		return settings;
	}
	
	public  SystemSettingsDto getSetting(String key ) throws Exception{
		String renterId = CloudSession.getLoginRenterId() ;
		return getSetting(renterId, key);
	}
	
	public  SystemSettingsDto getSetting(String renterId , String key ) throws Exception{
		
		if(cacheSettings.containsKey(renterId+"."+key)) {
			return ( SystemSettingsDto) cacheSettings.get(renterId+"."+key);
		}
		
		SystemSettingsDto res = DatabaseHelper.queryFirst(TABLE_CLASS, null, null, new String[] {"RENTERID","KEY"}, new String[] {"=","="}, new String[] {renterId,key}, orderbyConstant);
		if(res == null  ) {
			return null ;
		}
		cacheSettings.put(renterId+"."+key, res);
		
		return res ;
	}
	
	public  String getSettingValue( String key ) throws Exception{
		SystemSettingsDto dto = getSetting( key);
		if( dto == null ) {
			return null ;
		}
		return dto.getValue() ;
	}
	
	public  String getSettingValue(String renterId , String key ) throws Exception{
		SystemSettingsDto dto = getSetting(renterId, key);
		if( dto == null ) {
			return null ;
		}
		return dto.getValue() ;
	}
	

	public void addSetting( SystemSettingsDto setting ) throws Exception {
		String renterId  = CloudSession.getLoginRenterId() ;
		setting.setRenterId( renterId );
		setting.setCreateTime(new Date());
		setting.setUpdateTime(new Date());
		setting.setOperator(CloudSession.getLoginUser());
		if(Utils.isEmpty(setting.getStatus())) {
			setting.setStatus("0");
		}
		DatabaseHelper.insert(TABLE_CLASS , setting);
		
		cacheSettings.remove(renterId);
		cacheSettings.remove(renterId+"."+setting.getKey());
		
	}
	
	public void updateSetting( SystemSettingsDto setting) throws Exception {
		
		String renterId  = CloudSession.getLoginRenterId() ;
		
		setting.setRenterId( renterId );
		setting.setUpdateTime(new Date());
		setting.setOperator(CloudSession.getLoginUser());
		if(Utils.isEmpty(setting.getStatus())) {
			setting.setStatus("0");
		}
		DatabaseHelper.update(TABLE_CLASS, setting, "RENTERID","KEY");
		
		cacheSettings.remove(renterId);
		cacheSettings.remove(renterId+"."+setting.getKey());
		
	}
	
	public void updateSetting(String key ,String value) throws Exception {
		String renterId  = CloudSession.getLoginRenterId() ;
		
		SystemSettingsDto setting = getSetting(key);
		if( setting == null ) {
			setting = new SystemSettingsDto(key, value);
			addSetting(setting);
		}else {
			DatabaseHelper.update(TABLE_CLASS, new String[] {"UPDATETIME","VALUE","OPERATOR"}, new String[] {"RENTERID","KEY"}, new String[] {"=","="}, 
					new Object[] {new Date(),value,CloudSession.getLoginUser(),CloudSession.getLoginRenterId(),key});
		}
		
		cacheSettings.remove(renterId);
		cacheSettings.remove(renterId+"."+key);
		
	}

	public void deleteSetting( String key) throws Exception {
		String renterId = CloudSession.getLoginRenterId() ;
		deleteSetting(renterId, key);
		
		cacheSettings.remove(renterId);
		cacheSettings.remove(renterId+"."+key);
		
	}
	
	public void deleteSetting(String renterId , String key) throws Exception {
		DatabaseHelper.delete(TABLE_CLASS,new String[] {"RENTERID","KEY"}, new String[] {"=","="}, new String[] {renterId,key});
		
		cacheSettings.remove(renterId);
		cacheSettings.remove(renterId+"."+key);
	}
	
	public void clearCache() {
		cacheSettings.clear();
	}
	
}



