/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.db;

import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.PartitionDatabaseMeta;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据库连接dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库连接信息")
public class DbConnectionDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	private Long schemaId;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("类型")
    private String type;
	
	@ApiModelProperty("")
    private String access;
	
	@ApiModelProperty("主机地址(IP)")
    private String hostname;
	
	@ApiModelProperty("端口")
    private String port;
	
	@ApiModelProperty("数据库名称")
    private String databaseName;
	
	@ApiModelProperty("用户名")
    private String username;
	
	@ApiModelProperty("密码")
    private String password;
	
	@ApiModelProperty("url ,Only for Generic")
    private String url; // Only for Generic
	
	@ApiModelProperty("driver ,Only for Generic")
    private String driver; // Only for Generic
	
	@ApiModelProperty(" sqlServerInstance ,Only for MSSQL")
    private String sqlServerInstance; // Only for MSSQL
	
	@ApiModelProperty(" useIntegratedSecurity ,Only for MSSQL")
    private boolean useIntegratedSecurity; // Only for MSSQL
	
	@ApiModelProperty(" useDoubleDecimalSeparator ,Only for MSSQL")
    private boolean useDoubleDecimalSeparator; // Only for MSSQL
	
	@ApiModelProperty("表空间 , For Oracle & perhaps others")
    private String dataTableSpace; // data storage location, For Oracle & perhaps others
	
	@ApiModelProperty("表索引 , For Oracle & perhaps others")
    private String indexTableSpace; // index storage location, For Oracle & perhaps others
    
	@ApiModelProperty("参数")
    private List<DbOption> options;
	
	//高级选项
	private DbAdvanceOption advanceOption ;
	
	//连接池选项
	private boolean isUsePooling = false;
	private int poolMaximumSize ; //MAXIMUM_POOL_SIZE
	private int poolInitialSize; //INITIAL_POOL_SIZE
	private  Map<Object,Object> poolOptions ; //BaseDatabaseMeta.poolingParameters ;
	
	//集群选项
	private boolean isClustered = false ; //IS_CLUSTERED
    private List<PartitionDatabaseMeta> partitionOptions ;
	
    
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
    
    public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setAccess(String access) {
        this.access = access;
    }
    public String getAccess() {
        return access;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public String getHostname() {
        return hostname;
    }

    public void setPort(String port) {
        this.port = port;
    }
    public String getPort() {
        return port;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
    public String getDatabaseName() {
        return databaseName;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    
    public void setDriver(String driver) {
        this.driver = driver;
    }
    public String getDriver() {
        return driver;
    }

    public void setSqlServerInstance(String sqlServerInstance) {
        this.sqlServerInstance = sqlServerInstance;
    }
    public String getSqlServerInstance() {
        return sqlServerInstance;
    }

    public void setUseIntegratedSecurity(boolean useIntegratedSecurity) {
        this.useIntegratedSecurity = useIntegratedSecurity;
    }
    public boolean getUseIntegratedSecurity() {
        return useIntegratedSecurity;
    }
    
    /**
	 * @return useDoubleDecimalSeparator
	 */
	public boolean isUseDoubleDecimalSeparator() {
		return useDoubleDecimalSeparator;
	}
	/**
	 * @param useDoubleDecimalSeparator 要设置的 useDoubleDecimalSeparator
	 */
	public void setUseDoubleDecimalSeparator(boolean useDoubleDecimalSeparator) {
		this.useDoubleDecimalSeparator = useDoubleDecimalSeparator;
	}
    
    /**
	 * @return dataTableSpace
	 */
	public String getDataTableSpace() {
		return dataTableSpace;
	}
	/**
	 * @param dataTableSpace 要设置的 dataTableSpace
	 */
	public void setDataTableSpace(String dataTableSpace) {
		this.dataTableSpace = dataTableSpace;
	}
	/**
	 * @return indexTableSpace
	 */
	public String getIndexTableSpace() {
		return indexTableSpace;
	}
	/**
	 * @param indexTableSpace 要设置的 indexTableSpace
	 */
	public void setIndexTableSpace(String indexTableSpace) {
		this.indexTableSpace = indexTableSpace;
	}
    
	/**
	 * @return options
	 */
	public List<DbOption> getOptions() {
		return options;
	}
	/**
	 * @param options 要设置的 options
	 */
	public void setOptions(List<DbOption> options) {
		this.options = options;
	}
	
	public DbAdvanceOption getAdvanceOption() {
		return advanceOption;
	}
	public void setAdvanceOption(DbAdvanceOption advanceOption) {
		this.advanceOption = advanceOption;
	}
	public boolean isUsePooling() {
		return isUsePooling;
	}
	public void setUsePooling(boolean isUsePooling) {
		this.isUsePooling = isUsePooling;
	}
	public int getPoolMaximumSize() {
		return poolMaximumSize;
	}
	public void setPoolMaximumSize(int poolMaximumSize) {
		this.poolMaximumSize = poolMaximumSize;
	}
	public int getPoolInitialSize() {
		return poolInitialSize;
	}
	public void setPoolInitialSize(int poolInitialSize) {
		this.poolInitialSize = poolInitialSize;
	}
	public Map<Object,Object> getPoolOptions() {
		return poolOptions;
	}
	public void setPoolOptions( Map<Object,Object> poolOptions) {
		this.poolOptions = poolOptions;
	}
	public boolean isClustered() {
		return isClustered;
	}
	public void setClustered(boolean isClustered) {
		this.isClustered = isClustered;
	}
	public List<PartitionDatabaseMeta> getPartitionOptions() {
		return partitionOptions;
	}
	public void setPartitionOptions(List<PartitionDatabaseMeta> partitionOptions) {
		this.partitionOptions = partitionOptions;
	}
	@Override
	public String toString() {
		return "DbConnectionDto [owner=" + owner + ", schemaId=" + schemaId + ", name=" + name + ", type=" + type
				+ ", access=" + access + ", hostname=" + hostname + ", port=" + port + ", databaseName=" + databaseName
				+ ", username=" + username + ", password=" + password + ", url=" + url + ", driver=" + driver
				+ ", sqlServerInstance=" + sqlServerInstance + ", useIntegratedSecurity=" + useIntegratedSecurity
				+ ", useDoubleDecimalSeparator=" + useDoubleDecimalSeparator + ", dataTableSpace=" + dataTableSpace
				+ ", indexTableSpace=" + indexTableSpace + ", options=" + options + ", advanceOption=" + advanceOption
				+ ", isUsePooling=" + isUsePooling + ", poolMaximumSize=" + poolMaximumSize + ", poolInitialSize="
				+ poolInitialSize + ", poolOptions=" + poolOptions + ", isClustered=" + isClustered
				+ ", partitionOptions=" + partitionOptions + "]";
	}

}
