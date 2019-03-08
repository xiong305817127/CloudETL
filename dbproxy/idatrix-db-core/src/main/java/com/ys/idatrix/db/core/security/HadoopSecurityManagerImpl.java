package com.ys.idatrix.db.core.security;

import com.ys.idatrix.db.exception.HadoopSecurityManagerException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName: HadoopSecurityManagerImpl
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Service
public class HadoopSecurityManagerImpl implements HadoopSecurityManager {

    private final ConcurrentMap<String, UserGroupInformation> userUgiMap = new ConcurrentHashMap<>();

    @Value("${custom.security.enabled}")
    private boolean securityEnabled;

    @Value("${custom.security.hadoop.impersonate}")
    private boolean impersonate;

    @Value("${custom.security.kerberos.kbr5conf}")
    private String krb5ConfLocation;

    @Value("${custom.security.kerberos.hdfs.keytab}")
    private String hdfsKeytab;

    @Value("${custom.security.kerberos.hdfs.principal}")
    private String hdfsPrincipal;

    @Value("${custom.security.kerberos.hive.keytab}")
    private String hiveKeytab;

    @Value("${custom.security.kerberos.hive.principal}")
    private String hivePrincipal;

    @Value("${custom.security.kerberos.hbase.keytab}")
    private String hbaseKeytab;

    @Value("${custom.security.kerberos.hbase.principal}")
    private String hbasePrincipal;

    @Value("${custom.spark.thrift.server}")
    private String hiveThriftServer;

    @Value("${custom.spark.thrift.port}")
    private String hiveThriftPort;

    @Value("${custom.hbase.zk.quorum}")
    private String hbaseZookeeperQuorum;

    @Value("${custom.hbase.zk.port}")
    private String hbaseClientPort;

    @Value("${custom.hbase.zk.znode}")
    private String hbaseZnodeParent;

    @Value("${custom.hdfs.super-user}")
    private String superUser;

    private Properties hbaseJDBCProps;

    /**
     * 配置信息
     */
    private Configuration conf;

    /**
     * hdfs 代理用户（伪装）
     */
    private UserGroupInformation hdfsUser;

    /**
     * hive 代理用户（伪装）
     */
    private UserGroupInformation hiveUser;

    /**
     * hbase 代理用户（伪装）
     */
    private UserGroupInformation hbaseUser;

    /**
     * phoenix url
     */
    private String phoenixUrl;

    /**
     * 最近登录时间(ms)，等于0表示不启用安全连接
     */
    private long loginTimeMs = 0;

    /**
     * 最近登录时间(min)，小于等于0表示不重新登录
     */
    private long reloginIntervalMin = 0;

    private ReentrantLock lock = new ReentrantLock();

    @PostConstruct
    public void init() throws HadoopSecurityManagerException {
        this.conf = new Configuration();
        if (securityEnabled) {
            try {
                System.setProperty("java.security.krb5.conf", krb5ConfLocation);
                UserGroupInformation.setConfiguration(conf);
                // hdfs
                if (!StringUtils.isEmpty(hdfsKeytab) && !StringUtils.isEmpty(hdfsPrincipal)) {
                    this.hdfsUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(hdfsPrincipal, hdfsKeytab);
                }
                // hive
                if (!StringUtils.isEmpty(hiveKeytab) && !StringUtils.isEmpty(hivePrincipal)) {
                    // hive,如果和hdfs用户相同，无需再次登录
                    if (hivePrincipal.equals(hdfsPrincipal)) {
                        this.hiveUser = this.hdfsUser;
                    } else {
                        this.hiveUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(hivePrincipal, hiveKeytab);
                    }
                }
                // hbase
                if (!StringUtils.isEmpty(hbaseKeytab) && !StringUtils.isEmpty(hbasePrincipal)) {
                    // hbase,如果和hdfs用户相同，无需再次登录
                    if (hbasePrincipal.equals(hdfsPrincipal)) {
                        this.hbaseUser = this.hdfsUser;
                    } else {
                        this.hbaseUser = UserGroupInformation.loginUserFromKeytabAndReturnUGI(hbasePrincipal,
                                hbaseKeytab);
                    }
                }

                this.loginTimeMs = System.currentTimeMillis();
            } catch (IOException e) {
                throw new HadoopSecurityManagerException("Failed to login with kerberos ", e);
            }
        } else {
            System.setProperty("HADOOP_USER_NAME", superUser);
        }

        // phoenix jdbc url
        this.phoenixUrl = "jdbc:phoenix:" + hbaseZookeeperQuorum + ":" + hbaseClientPort + ":" + hbaseZnodeParent;

        //
        hbaseJDBCProps = new Properties();
        hbaseJDBCProps.put("phoenix.schema.isNamespaceMappingEnabled", true);
        hbaseJDBCProps.put("phoenix.queryserver.withRemoteUserExtractor", true);
    }

    public void destroy() {

    }

    /**
     * 检查TGT超时，重新登陆
     */
    private void checkTGTAndReloginFromKeytab() {
        if (securityEnabled) {
            if (lock.tryLock()) {
                try {
                    long current = System.currentTimeMillis();
                    if (loginTimeMs == 0L || reloginIntervalMin == 0L) {
                        return;
                    }
                    if (current < loginTimeMs + reloginIntervalMin * 3600) {

                        if (hdfsUser != null) {
                            log.info("loginUser (" + this.hdfsUser + ") already created, refreshing tgt.");
                            hdfsUser.checkTGTAndReloginFromKeytab();
                            userUgiMap.clear();
                        }
                        if (hiveUser != null && hiveUser != hdfsUser) {
                            log.info("loginUser (" + this.hiveUser + ") already created, refreshing tgt.");
                            hiveUser.checkTGTAndReloginFromKeytab();
                        }
                        loginTimeMs = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    log.error("checkTGTAndReloginFromKeytab error", e);
                } finally {
                    lock.unlock();
                }
            }
        }

    }

    @Override
    public FileSystem getFSAsUser(String user) throws HadoopSecurityManagerException {
        final FileSystem fs;
        try {
            log.info("Getting file system as " + user);
            final UserGroupInformation ugi = getProxiedHDFSUser(user);

            if (ugi != null) {
                fs = ugi.doAs(new PrivilegedAction<FileSystem>() {

                    @Override
                    public FileSystem run() {
                        try {
                            return FileSystem.get(conf);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } else {
                fs = FileSystem.get(this.conf);
            }
        } catch (final Exception e) {
            throw new HadoopSecurityManagerException("Failed to get FileSystem. ", e);
        }
        return fs;
    }

    @Override
    public FileSystem getFSAsDefaultUser() throws HadoopSecurityManagerException {
        final FileSystem fs;
        try {
            UserGroupInformation ugi = getDefaultHDFSUser();
            if (ugi != null) {
                log.info("loginUser (" + this.hdfsUser + ") already created, refreshing tgt.");
                hdfsUser.checkTGTAndReloginFromKeytab();
                log.info("Getting file system as " + hdfsUser.getUserName());
                fs = hdfsUser.doAs(new PrivilegedAction<FileSystem>() {

                    @Override
                    public FileSystem run() {
                        try {
                            return FileSystem.get(conf);
                        } catch (final IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } else {
                fs = FileSystem.get(this.conf);
            }
        } catch (final Exception e) {
            throw new HadoopSecurityManagerException("Failed to get FileSystem. ", e);
        }
        return fs;
    }

    @Override
    public Properties getPhoenixJDBCProps() {
        return this.hbaseJDBCProps;
    }

    @Override
    public UserGroupInformation getProxiedHDFSUser(String user) {
        //不使用用户伪装
        if (!impersonate) {
            return getDefaultHDFSUser();
        }

        UserGroupInformation ugi = this.userUgiMap.get(user);
        if (ugi == null) {
            log.info("proxy user " + user + " not exist. Creating new proxy user");
        }
        if (securityEnabled) {
            ugi = UserGroupInformation.createProxyUser(user, getDefaultHDFSUser());
            this.userUgiMap.putIfAbsent(user, ugi);
        }

        return ugi;
    }

    @Override
    public UserGroupInformation getDefaultHDFSUser() {
        checkTGTAndReloginFromKeytab();
        return hdfsUser;
    }

    public UserGroupInformation getHiveUser() {
        checkTGTAndReloginFromKeytab();
        return hiveUser;
    }

    @Override
    public Connection getSparkJdbcConnection(String database) throws HadoopSecurityManagerException {
        checkTGTAndReloginFromKeytab();
        Connection connection = null;
        String baseUrl = "jdbc:hive2://" + hiveThriftServer + ":" + hiveThriftPort + "/" + database;
        if (hiveUser == null) {
            try {
                Class.forName("org.apache.hive.jdbc.HiveDriver");
                connection = DriverManager.getConnection(baseUrl, "hive", "");
            } catch (ClassNotFoundException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            } catch (SQLException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            }

        } else {
            JdbcConnectionEntry entry = hiveUser.doAs(new PrivilegedAction<JdbcConnectionEntry>() {
                @Override
                public JdbcConnectionEntry run() {
                    JdbcConnectionEntry result = new JdbcConnectionEntry();
                    try {
                        Class.forName("org.apache.hive.jdbc.HiveDriver");
                        String url = baseUrl + ";principal=" + hivePrincipal;
                        Connection conn = DriverManager.getConnection(url, "", "");
                        result.setConnection(conn);
                    } catch (SQLException e) {
                        log.error("getHiveJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    } catch (ClassNotFoundException e) {
                        log.error("getHiveJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    }
                    return result;
                }
            });
            if (entry.getConnection() == null) {
                throw new HadoopSecurityManagerException(entry.getErrorMessage());
            }
            connection = entry.getConnection();
        }

        return connection;
    }

    @Override
    public Connection getPhoenixJdbcConnection() throws HadoopSecurityManagerException {
        checkTGTAndReloginFromKeytab();
        Connection connection = null;

        if (!isSecurityEnabled()) {
            try {
                Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
                connection = DriverManager.getConnection(this.phoenixUrl, this.hbaseJDBCProps);
            } catch (ClassNotFoundException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            } catch (SQLException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            }

        } else {
            JdbcConnectionEntry entry = hbaseUser.doAs(new PrivilegedAction<JdbcConnectionEntry>() {
                @Override
                public JdbcConnectionEntry run() {
                    JdbcConnectionEntry result = new JdbcConnectionEntry();
                    try {
                        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
                        System.out.println(phoenixUrl);
                        Connection conn = DriverManager.getConnection(phoenixUrl, hbaseJDBCProps);
                        result.setConnection(conn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        log.error("getPhoenixJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    } catch (ClassNotFoundException e) {
                        log.error("getPhoenixJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    }
                    return result;
                }
            });
            if (entry.getConnection() == null) {
                throw new HadoopSecurityManagerException(entry.getErrorMessage());
            }
            connection = entry.getConnection();
        }

        return connection;
    }

    public Connection getPhoenixJdbcConnection(String user) throws HadoopSecurityManagerException {
        checkTGTAndReloginFromKeytab();
        Connection connection = null;

        if (!isSecurityEnabled()) {
            try {
                Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
                connection = DriverManager.getConnection(this.phoenixUrl, this.hbaseJDBCProps);
            } catch (ClassNotFoundException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            } catch (SQLException e) {
                throw new HadoopSecurityManagerException(e.getMessage(), e);
            }

        } else {
            JdbcConnectionEntry entry = getProxiedHDFSUser(user).doAs(new PrivilegedAction<JdbcConnectionEntry>() {
                @Override
                public JdbcConnectionEntry run() {
                    JdbcConnectionEntry result = new JdbcConnectionEntry();
                    try {
                        Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
                        System.out.println(phoenixUrl);
                        Connection conn = DriverManager.getConnection(phoenixUrl, hbaseJDBCProps);
                        result.setConnection(conn);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        log.error("getPhoenixJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    } catch (ClassNotFoundException e) {
                        log.error("getPhoenixJdbcConnection error", e);
                        result.setErrorMessage(e.getMessage());
                    }
                    return result;
                }
            });

            if (entry.getConnection() == null) {
                throw new HadoopSecurityManagerException(entry.getErrorMessage());
            }
            connection = entry.getConnection();
        }

        return connection;
    }

    @Override
    public boolean isSecurityEnabled() {
        return securityEnabled;
    }


}
