package com.ys.idatrix.metacube.common.helper;

import com.google.common.collect.ImmutableMap;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum.*;

/**
 * @ClassName: DataTypeHelper
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/8/31
 */
@Slf4j
public class DataTypeHelper {

    /**
     * 不同数据源类型的字段转换
     *
     * @param sourceDsType
     * @param targetDsType
     * @param metadataPropertyList
     */
    public static void convertDataTypeOnDiffDsType(DatabaseTypeEnum sourceDsType, DatabaseTypeEnum targetDsType, List<TableColumn> metadataPropertyList) throws Exception {

        log.info("###### 不同源数据源存储系统建表,字段类型转换 ###### 源数据源类型[{}] --> 目标数据源类型[{}]", sourceDsType,targetDsType);

        for (TableColumn property : metadataPropertyList) {

            String dataType = property.getColumnType();
            String dataLength = property.getTypeLength();
            String dataPrecision = property.getTypePrecision();

            int dataLengthNum = -1;
            if (StringUtils.isNotBlank(dataLength)) {
                dataLengthNum = Integer.valueOf(dataLength).intValue();
            }

            int dataPrecisionNum = -1;
            if (StringUtils.isNotBlank(dataPrecision)) {
                dataPrecisionNum = Integer.valueOf(dataPrecision).intValue();
            }

            Map<String, Object> convertResult = null;
            switch (sourceDsType) {
                //源数据源类型=MYSQL -> 目标数据源 ORACLE、DM
                case MYSQL:
                    convertResult = convertMySqlToOtherType(targetDsType, dataType, dataLengthNum, dataPrecisionNum);
                    break;
                //源数据源类型=ORACLE -> 目标数据源 MYSQL、DM
                case ORACLE:
                    convertResult = convertOracleToOtherType(targetDsType, dataType, dataLengthNum, dataPrecisionNum);
                    break;
                //源数据源类型=DM7 -> 目标数据源 MYSQL、ORACLE
                case DM:
                    convertResult = convertDmToOtherType(targetDsType, dataType, dataLengthNum, dataPrecisionNum);
                    break;
                //源数据源类型=POSTGRESQL -> 目标数据源 MYSQL、DM
                case POSTGRESQL:
                    convertResult = convertPostgreSqlToOtherType(targetDsType, dataType, dataLengthNum, dataPrecisionNum);
                    break;
                default:
                    log.error("暂不支持源数据库类型:[{}] 到 目标数据类型:[{}]的跨库转换",sourceDsType.getName(),targetDsType.getName());
                    throw new MetaDataException("暂不支持源数据库类型:"+sourceDsType.getName()
                            +" 到 目标数据类型:"+ targetDsType.getName()+"的跨库转换");
            }

            if (MapUtils.isEmpty(convertResult)) {
                return;
            }

            dataType = (String) convertResult.get("dataType");
            dataLengthNum = (int) convertResult.get("dataLengthNum");
            dataPrecisionNum = (int) convertResult.get("dataPrecisionNum");
            boolean hasConverted = (boolean) convertResult.get("hasConverted");

            property.setColumnType(dataType);

            //数据长度
            if (dataLengthNum == -1) {
                property.setTypeLength(null);
            } else {
                property.setTypeLength(dataLengthNum + "");
            }

            //校验类型是否该有精度
            boolean hasScale = DataTypeHelper.hasDataTypePrecision(targetDsType, dataType, dataPrecisionNum + "");
            if (!hasScale) {
                property.setTypePrecision(null);
            }

            if (hasConverted) {
                log.info("++++++ 字段columnName=" + property.getColumnName()+ " 转换后 dataType="
                        + property.getColumnType() + ",length=" + property.getTypeLength() + ",precision=" + property.getTypePrecision());
            } else {
                log.info("====== 字段columnName=" + property.getColumnName() + " 无需转换 dataType=" +
                        property.getColumnType() + ",length=" + property.getTypeLength() + ",precision=" + property.getTypePrecision());
            }
        }
    }

    /**
     * Mysql 转 其它数据库
     *
     * @param targetDsType
     * @param dataType
     * @param dataLengthNum
     * @param dataPrecisionNum
     * @return
     */
    private static Map<String, Object> convertMySqlToOtherType(DatabaseTypeEnum targetDsType, String dataType, int dataLengthNum, int dataPrecisionNum) {
        boolean hasConverted = true;
        //MYSQL -> ORACLE
        if (targetDsType.getName().equals(ORACLE.getName())) {
            switch (dataType) {
                //字符型
                case "VARCHAR":
                    if (dataLengthNum <= 4000) {
                        dataType = "VARCHAR2";
                    } else {
                        dataType = "LONG";
                        dataLengthNum = -1;
                    }
                    break;
                case "TINYTEXT":
                    dataType = "LONG";
                    dataLengthNum = -1;
                    break;
                case "TEXT":
                case "MEDIUMTEXT":
                case "LONGTEXT":
                    dataType = "CLOB";
                    dataLengthNum = -1;
                    break;
                case "ENUM":
                case "SET":
                    dataType = "VARCHAR2";
                    dataLengthNum = 200;
                    break;

                //数字型
                case "TINYINT":
                case "SMALLINT":
                case "BIGINT":
                case "INTEGER":
                case "INT":
                case "MEDIUMINT":
                case "NUMERIC":
                    dataType = "NUMBER";
                    if (dataLengthNum == 0) {
                        dataLengthNum = -1;
                    } else {
                        dataLengthNum = dataLengthNum > 38 ? 38 : dataLengthNum;
                    }
                    dataPrecisionNum = -1;
                    break;
                case "DECIMAL":
                case "FLOAT":
                case "DOUBLE":
                case "REAL":
                    dataType = "NUMBER";
                    if (dataLengthNum == 0) {
                        dataLengthNum = -1;
                    } else {
                        dataLengthNum = dataLengthNum > 126 ? 126 : dataLengthNum;
                    }
                    dataPrecisionNum = -1;
                    break;


                // 二进制
                case "BIT":
                case "BINARY":
                    dataType = "RAW";
                    break;
                case "VARBINARY":
                    dataType = "LONG RAW";
                    break;
                case "TINYBLOB":
                case "MEDIUMBLOB":
                case "LONGBLOB":
                    dataType = "BLOB";
                    dataLengthNum = -1;
                    break;

                // 时间日期
                case "YEAR":
                    dataType = "NUMBER";
                    dataPrecisionNum = 0;
                    break;
                case "DATETIME":
                case "TIME":
                    dataType = "DATE";
                    dataLengthNum = -1;
                    break;

                //几何类型
                case "GEOMETRY":
                case "POINT":
                case "LINESTRING":
                case "POLYGON":
                case "MULTIPOINT":
                case "MULTILINESTRING":
                case "MULTIPOLYGON":
                case "GEOMETRYCOLLECTION":
                    dataType = "LONG";
                    dataLengthNum = -1;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;

            }
        }
        //MYSQL -> DM
        if (targetDsType.getName().equals(DM.getName())) {
            switch (dataType) {
                //字符型
                case "VARCHAR":
                    if (dataLengthNum <= 8188) {
                        dataType = "VARCHAR";
                    } else {
                        dataType = "LONGVARCHAR";
                        dataLengthNum = -1;
                    }
                    break;
                case "TINYTEXT":
                case "MEDIUMTEXT":
                    dataType = "TEXT";
                    dataLengthNum = -1;
                    break;
                case "LONGTEXT":
                    dataType = "CLOB";
                    dataLengthNum = -1;
                    break;

                //数字型
                case "TINYINT":
                case "SMALLINT":
                case "BIGINT":
                case "INTEGER":
                case "INT":
                case "MEDIUMINT":
                case "REAL":
                    dataLengthNum = -1;
                    dataPrecisionNum = -1;
                    if (dataType.equals("MEDIUMINT")) {
                        dataType = "INTEGER";
                    }
                    break;
                case "DOUBLE":
                case "FLOAT":
                    dataPrecisionNum = -1;
                    dataLengthNum = dataLengthNum >= 126 ? 126 : dataLengthNum;
                    break;
                case "NUMBER":
                case "DECIMAL":
                    if (dataLengthNum == 0) {
                        dataLengthNum = -1;
                    }
                    if (dataLengthNum == -1 && dataPrecisionNum == 0) {
                        dataPrecisionNum = -1;
                    }
                    break;


                // 二进制
                case "BIT":
                    dataLengthNum = -1;
                    break;

                // 时间日期
                case "YEAR":
                    dataType = "NUMBER";
                    if (dataLengthNum == 0) {
                        dataLengthNum = 4;
                    }
                    dataPrecisionNum = -1;
                    break;

                //几何类型
                case "GEOMETRY":
                case "POINT":
                case "LINESTRING":
                case "POLYGON":
                case "MULTIPOINT":
                case "MULTILINESTRING":
                case "MULTIPOLYGON":
                case "GEOMETRYCOLLECTION":
                    dataType = "LONGVARCHAR";
                    dataLengthNum = -1;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;

            }
        }

        return ImmutableMap.of("dataType", dataType, "dataLengthNum", dataLengthNum, "dataPrecisionNum",
                dataPrecisionNum, "hasConverted", hasConverted);
    }


    /**
     * Oracle 转 其它数据库
     *
     * @param targetDsType
     * @param dataType
     * @param dataLengthNum
     * @param dataPrecisionNum
     * @return
     */
    private static Map<String, Object> convertOracleToOtherType(DatabaseTypeEnum targetDsType, String dataType, int dataLengthNum, int dataPrecisionNum) {

        boolean hasConverted = true;

        //ORACLE -> MYSQL
        if (targetDsType.getName().equals(MYSQL.getName())) {
            switch (dataType) {
                //字符型
                case "CHAR":
                    if(dataLengthNum==0){
                        dataLengthNum=255;
                    }
                    if (dataLengthNum >= 255) {
                        dataType = "VARCHAR";
                    }
                    break;
                case "VARCHAR2":
                case "NCHAR":
                case "NVARCHAR2":
                    if(dataLengthNum==-1){
                        dataLengthNum=255;
                    }
                    dataType = "VARCHAR";
                    break;
                case "LONG":
                    dataType = "MEDIUMTEXT";
                    dataLengthNum = -1;
                    break;
                case "CLOB":
                case "NCLOB":
                    dataType = "LONGTEXT";
                    dataLengthNum = -1;
                    break;

                //数字型
                case "NUMBER":
                case "NUMERIC":
                    dataType = "NUMERIC";
                    if (dataLengthNum == 0) {
                        dataLengthNum = -1;
                    }
                    if (dataLengthNum == -1 && dataPrecisionNum == 0) {
                        dataPrecisionNum = -1;
                    }
                    break;
                case "FLOAT":
                case "BINARY_FLOAT":
                    dataType = "FLOAT";
                    dataLengthNum = 17;
                    dataPrecisionNum = 15;
                    break;
                case "BINARY_DOUBLE":
                    dataType = "DOUBLE";
                    dataLengthNum = 17;
                    dataPrecisionNum = 15;
                    break;

                // 二进制
                case "RAW":
                    dataType = "VARBINARY";
                    break;
                case "LONG_RAW":
                case "BFILE":
                    dataType = "BLOB";
                    dataLengthNum = -1;
                    break;

                // 时间日期
                case "DATE":
                    dataLengthNum = -1;
                    break;
                case "TIMESTAMP WITH TIME ZONE":
                case "TIMESTAMP WITH LOCAL TIME ZONE":
                case "INTERVAL DAY TO SECOND":
                case "INTERVAL YEAR TO MONTH":
                    dataType = "VARCHAR";
                    dataLengthNum = 50;
                    break;

                //伪列
                case "ROWID":
                case "UROWID":
                    dataType = "VARCHAR";
                    dataLengthNum = 50;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;

            }
        }

        //ORACLE -> DM
        if (targetDsType.getName().equals(DM.getName())) {
            switch (dataType) {
                //字符型
                case "NCHAR":
                case "NVARCHAR2":
                    dataType = "VARCHAR";
                    break;
                case "LONG":
                    dataType = "LONGVARCHAR";
                    dataLengthNum = -1;
                    break;
                case "NCLOB":
                    dataType = "CLOB";
                    dataLengthNum = -1;
                    break;

                //数字型
                case "FLOAT":
                case "BINARY_FLOAT":
                    dataType = "FLOAT";
                    break;
                case "BINARY_DOUBLE":
                    dataType = "DOUBLE";
                    break;
                case "NUMBER":
                case "NUMERIC":
                    if (dataLengthNum == 0) {
                        dataLengthNum = -1;
                    }
                    if (dataLengthNum == -1 && dataPrecisionNum == 0) {
                        dataPrecisionNum = -1;
                    }
                    break;

                // 二进制
                case "RAW":
                    dataType = "VARBINARY";
                    break;
                case "LONG RAW":
                    dataType = "BLOB";
                    dataLengthNum = -1;
                    break;

                // 时间日期
                case "DATE":
                    dataLengthNum = -1;
                    break;
                case "TIMESTAMP WITH TIME ZONE":
                case "TIMESTAMP WITH LOCAL TIME ZONE":
                case "INTERVAL DAY TO SECOND":
                case "INTERVAL YEAR TO MONTH":
                    dataLengthNum = -1;
                    break;

                //伪列
                case "ROWID":
                case "UROWID":
                    dataType = "VARCHAR";
                    dataLengthNum = 50;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;

            }
        }
        return ImmutableMap.of("dataType", dataType, "dataLengthNum", dataLengthNum, "dataPrecisionNum",
                dataPrecisionNum, "hasConverted", hasConverted);
    }


    /**
     * DM 转 其它数据库
     *
     * @param targetDsType
     * @param dataType
     * @param dataLengthNum
     * @param dataPrecisionNum
     * @return
     */
    private static Map<String, Object> convertDmToOtherType(DatabaseTypeEnum targetDsType, String dataType, int dataLengthNum, int dataPrecisionNum) {

        boolean hasConverted = true;

        //DM -> MYSQL
        if (targetDsType.getName().equals(MYSQL.getName())) {
            switch (dataType) {
                //字符型
                case "CHAR":
                    if (dataLengthNum >= 255) {
                        dataType = "VARCHAR";
                    }
                    break;
                case "CHARACTER":
                    if (dataLengthNum >= 255) {
                        dataType = "VARCHAR";
                    } else {
                        dataType = "CHAR";
                    }
                    break;
                case "VARCHAR2":
                    dataType = "VARCHAR";
                    break;
                case "LONGVARCHAR":
                case "CLOB":
                    dataType = "LONGTEXT";
                    dataLengthNum = -1;
                    break;

                //数字型
                case "PLS_INTEGER":
                    dataType = "INTEGER";
                    break;
                case "BYTE":
                    dataType = "TINYINT";
                    break;
                case "NUMBER":
                    dataType = "NUMERIC";
                    break;
                case "DOUBLE PRECISION":
                    dataType = "DOUBLE";
                    break;
                case "DEC":
                    dataType = "DECIMAL";
                    break;

                // 二进制
                case "LONGVARBINARY":
                case "IMAGE":
                case "BFILE":
                    dataType = "BLOB";
                    dataLengthNum = -1;
                    break;

                // 时间日期
                case "DATE":
                    dataLengthNum = -1;
                    break;
                case "TIMESTAMP WITH TIME ZONE":
                case "TIMESTAMP WITH LOCAL TIME ZONE":
                case "TIME WITH TIME ZONE":
                case "DATETIME WITH TIME ZONE":
                case "INTERVAL YEAR":
                case "INTERVAL MONTH":
                case "INTERVAL DAY":
                case "INTERVAL HOUR":
                case "INTERVAL MINUTE":
                case "INTERVAL SECOND":
                case "INTERVAL YEAR TO MONTH":
                case "INTERVAL DAY TO HOUR":
                case "INTERVAL DAY TO MINUTE":
                case "INTERVAL DAY TO SECOND":
                case "INTERVAL HOUR TO MINUTE":
                case "INTERVAL HOUR TO SECOND":
                case "INTERVAL MINUTE TO SECOND":
                    dataType = "VARCHAR";
                    dataLengthNum = 50;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;


            }
        }

        //DM -> ORACLE
        if (targetDsType.getName().equals(ORACLE.getName())) {
            switch (dataType) {
                //字符型
                case "CHAR":
                case "CHARACTER":
                    if (2000 <= dataLengthNum && dataLengthNum <= 4000) {
                        dataType = "VARCHAR2";
                    }

                    if (dataLengthNum > 4000) {
                        dataType = "LONG";
                        dataLengthNum = -1;
                        dataPrecisionNum = -1;
                    }
                    break;
                case "VARCHAR":
                case "VARCHAR2":
                    if (dataLengthNum <= 4000) {
                        dataType = "VARCHAR2";
                        dataPrecisionNum = -1;
                    }

                    if (dataLengthNum > 4000) {
                        dataType = "LONG";
                        dataLengthNum = -1;
                        dataPrecisionNum = -1;
                    }
                    break;
                case "LONGVARCHAR":
                case "TEXT":
                    dataType = "LONG";
                    dataLengthNum = -1;
                    break;

                //数字型
                case "TINYINT":
                case "SMALLINT":
                case "BIGINT":
                case "INTEGER":
                case "INT":
                case "BYTE":
                case "PLS_INTEGER":
                case "REAL":
                    dataType = "NUMBER";
                    dataLengthNum = dataLengthNum > 38 ? 38 : dataLengthNum;
                    break;
                case "DECIMAL":
                case "DESC":
                case "NUMERIC":
                case "NUMBER":
                    dataType = "NUMBER";
                    dataLengthNum = dataLengthNum > 126 ? 126 : dataLengthNum;
                    break;
                case "DOUBLE":
                case "DOUBLE PRECISION":
                    dataType = "DOUBLE";
                    break;

                // 二进制
                case "BIT":
                    dataType = "RAW";
                    dataLengthNum = 1;
                    dataPrecisionNum = -1;
                    break;
                case "BINARY":
                case "VARBINARY":
                    dataType = "LONG RAW";
                    break;
                case "LONGVARBINARY":
                case "IMAGE":
                    dataType = "BLOB";
                    dataLengthNum = -1;
                    break;


                // 时间日期
                case "DATE":
                    dataLengthNum = -1;
                    break;
                case "TIME":
                case "DATETIME":
                    dataType = "TIMESTAMP";
                    dataLengthNum = -1;
                    break;
                case "TIMESTAMP WITH TIME ZONE":
                case "TIMESTAMP WITH LOCAL TIME ZONE":
                case "INTERVAL DAY TO SECOND":
                case "INTERVAL YEAR TO MONTH":
                    dataLengthNum = -1;
                    break;
                case "TIME WITH TIME ZONE":
                case "DATETIME WITH TIME ZONE":
                case "INTERVAL YEAR":
                case "INTERVAL MONTH":
                case "INTERVAL DAY":
                case "INTERVAL HOUR":
                case "INTERVAL MINUTE":
                case "INTERVAL SECOND":
                case "INTERVAL DAY TO HOUR":
                case "INTERVAL DAY TO MINUTE":
                case "INTERVAL HOUR TO MINUTE":
                case "INTERVAL HOUR TO SECOND":
                case "INTERVAL MINUTE TO SECOND":
                    dataType = "VARCHAR2";
                    dataLengthNum = 50;
                    break;

                //默认不改变
                default:
                    hasConverted = false;
                    break;
            }
        }

        return ImmutableMap.of("dataType", dataType, "dataLengthNum", dataLengthNum, "dataPrecisionNum",
                dataPrecisionNum, "hasConverted", hasConverted);
    }


    /**
     * PostgreSql 转 其它数据库
     *
     * @param targetDsType
     * @param dataType
     * @param dataLengthNum
     * @param dataPrecisionNum
     * @return
     */
    private static Map<String, Object> convertPostgreSqlToOtherType(DatabaseTypeEnum targetDsType, String dataType, int dataLengthNum, int dataPrecisionNum) {

        boolean hasConverted = true;

        return ImmutableMap.of("dataType", dataType, "dataLengthNum", dataLengthNum, "dataPrecisionNum",
                dataPrecisionNum, "hasConverted", hasConverted);
    }

    /**
     * 根据字段类型判断是否有范围值
     *
     * @param dsType
     * @param dataType
     * @param scale
     * @return
     */
    public static boolean hasDataTypePrecision(DatabaseTypeEnum dsType, String dataType, String scale) {
        boolean hasScale = false;
        if (StringUtils.isNotBlank(scale) && Integer.valueOf(scale).intValue() > -1) {
            switch (dsType) {
                //MySql
                case MYSQL:
                    switch (dataType.toUpperCase()) {
                        case "NUMERIC":
                        case "DECIMAL":
                        case "DOUBLE":
                        case "FLOAT":
                        case "REAL":
                            hasScale = true;
                            break;
                        default:
                            break;
                    }
                    break;
                //Oracle、DM,其中Oracle没有 DECIMAL 类型
                case ORACLE:
                case DM:
                    switch (dataType.toUpperCase()) {
                        case "NUMBER":
                        case "NUMERIC":
                        case "DECIMAL":
                            hasScale = true;
                            break;
                        default:
                            break;
                    }
                    break;
                //PostgreSQL
                case POSTGRESQL:
                    switch (dataType.toUpperCase()) {
                        case "NUMERIC":
                        case "_NUMERIC":
                            hasScale = true;
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        return hasScale;
    }

    /**
     * 根据字段类型判断是否有长度
     *
     * @param dsType
     * @param dataType
     * @param length
     * @return
     */
    public static boolean hasDataTypeLength(int dsType, String dataType, String length) {
        boolean hasLength = true;
        if (StringUtils.isNotBlank(length) && Integer.valueOf(length).intValue() > -1) {
            switch (dsType) {
                //MySql
                case 3:
                    switch (dataType.toUpperCase()) {
                        case "DATE":
                        case "DATETIME":
                        case "TINYTEXT":
                        case "TEXT":
                        case "MEDIUMTEXT":
                        case "LONGTEXT":
                        case "TINYBLOB":
                        case "BLOB":
                        case "MEDIUMBLOB":
                        case "LONGBLOB":
                        case "ENUM":
                        case "SET":
                        case "TIMESTAMP":
                            hasLength = false;
                            break;
                        default:
                            break;
                    }
                    break;
                //Oracle
                case 2:
                    switch (dataType.toUpperCase()) {
                        case "BINARY_DOUBLE":
                        case "BINARY_FLOAT":
                        case "LONG":
                        case "CLOB":
                        case "NCLOB":
                        case "BLOB":
                        case "LONG RAW":
                        case "BFILE":
                        case "DATE":
                        case "TIMESTAMP":
                        case "ROWID":
                        case "UROWID":
                            hasLength = false;
                            break;
                        default:
                            break;
                    }
                    break;
                //DM
                case 14:
                    switch (dataType.toUpperCase()) {
                        case "TINYINT":
                        case "SMALLINT":
                        case "BIGINT":
                        case "INTEGER":
                        case "INT":
                        case "PLS_INTEGER":
                        case "BYTE":
                        case "REAL":
                        case "LONGVARCHAR":
                        case "BLOB":
                        case "IMAGE":
                        case "BFILE":
                        case "CLOB":
                        case "TEXT":
                        case "DATE":
                        case "TIME":
                        case "DATETIME":
                        case "TIMESTAMP":
                            hasLength = false;
                            break;
                        default:
                            break;
                    }
                    break;
                //PostgreSQL
                case 8:
                    switch (dataType.toUpperCase()) {
                        case "TEXT":
                        case "JSON":
                        case "PATH":
                        case "SMALLINT":
                        case "TINTERVAL":
                        case "UUID":
                        case "XML":
                        case "TID":
                        case "SMALLSERIAL":
                        case "RELTIME":
                        case "TEGTYPE":
                        case "REAL":
                            hasLength = false;
                            break;
                        default:
                            break;
                    }
                default:
                    break;
            }
        }
        return hasLength;
    }

}
