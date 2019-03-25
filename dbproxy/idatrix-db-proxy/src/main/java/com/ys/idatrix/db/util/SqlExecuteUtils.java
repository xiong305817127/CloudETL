package com.ys.idatrix.db.util;

import com.ys.idatrix.db.api.rdb.dto.RdbEnum;
import com.ys.idatrix.db.api.rdb.dto.RdbLinkDto;
import com.ys.idatrix.db.api.sql.dto.SqlExecReqDto;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import org.apache.commons.lang.StringUtils;

import java.text.MessageFormat;

/**
 * @ClassName: SqlExecuteUtils
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/7/21
 */
public class SqlExecuteUtils {

    /**
     * MetaDatabase 创建 RdbLinkDto
     *
     * @param database
     * @return
     */
    public static RdbLinkDto generateRdbLink(SqlExecReqDto database) {
        RdbLinkDto config = new RdbLinkDto(database.getSchemaDetails().getUsername(), database.getSchemaDetails().getPassword(),
                database.getSchemaDetails().getType(), database.getSchemaDetails().getIp(),
                database.getSchemaDetails().getPort(), database.getSchemaDetails().getSchemaName());
        //hbase 和 hive 类型 不做重构处理
        if (DatabaseTypeEnum.HBASE.getName().equalsIgnoreCase(config.getType())
                || DatabaseTypeEnum.HIVE.getName().equalsIgnoreCase(config.getType())) {
            return config;
        }
        return rebuildRdbLink(config);

    }

    /**
     * RdbLinkDto 重构 RdbLinkDto
     * <p>
     * 构建RDBMS的db-link
     *
     * @param linkDto
     * @return
     */
    public static RdbLinkDto rebuildRdbLink(RdbLinkDto linkDto) {
        String url;
        String driverName;
        switch (DatabaseTypeEnum.valueOf(linkDto.getType())) {
            case MYSQL:
                if (StringUtils.isBlank(linkDto.getDbName())) {
                    url = MessageFormat.format(RdbEnum.RDBLink.MYSQL_NO_DB.getLinkUrl(), linkDto.getIp(), linkDto.getPort());
                } else {
                    url = MessageFormat.format(RdbEnum.RDBLink.MYSQL.getLinkUrl(), linkDto.getIp(), linkDto.getPort(), linkDto.getDbName());
                }
                driverName = RdbEnum.RDBLink.MYSQL.getDriverName();
                break;
            case ORACLE:
                url = MessageFormat.format(RdbEnum.RDBLink.ORACLE.getLinkUrl(), linkDto.getIp(), linkDto.getPort(), linkDto.getDbName());
                driverName = RdbEnum.RDBLink.ORACLE.getDriverName();
                break;
            case DM:
                url = MessageFormat.format(RdbEnum.RDBLink.DM7.getLinkUrl(), linkDto.getIp(), linkDto.getPort(), linkDto.getDbName());
                driverName = RdbEnum.RDBLink.DM7.getDriverName();
                break;
            /*case DB2:
                url = MessageFormat.format(RdbEnum.RDBLink.DB2.getLinkUrl(), rdbConfig.getIp(), rdbConfig.getPort(), rdbConfig.getSchemaName());
                driverName = RdbEnum.RDBLink.DB2.getDriverName();
                break;
            case SQLSERVER:
                url = MessageFormat.format(RdbEnum.RDBLink.SQLSERVER.getLinkUrl(), rdbConfig.getIp(), rdbConfig.getPort(), rdbConfig.getSchemaName());
                driverName = RdbEnum.RDBLink.SQLSERVER.getDriverName();
                break;*/
            case POSTGRESQL:
                url = MessageFormat.format(RdbEnum.RDBLink.POSTGRESQL.getLinkUrl(), linkDto.getIp(), linkDto.getPort(), linkDto.getDbName());
                driverName = RdbEnum.RDBLink.POSTGRESQL.getDriverName();
                break;
            /*case SYBASE:
                url = MessageFormat.format(RdbEnum.RDBLink.SYBASE.getLinkUrl(), rdbConfig.getIp(), rdbConfig.getPort(), rdbConfig.getSchemaName());
                driverName = RdbEnum.RDBLink.SYBASE.getDriverName();
                break;*/
            default:
                throw new DbProxyException("db-link不支持当前存储系统类型:" + linkDto.getType());
        }

        linkDto.setUrl(url);
        linkDto.setDriverClassName(driverName);

        return linkDto;
    }

}
