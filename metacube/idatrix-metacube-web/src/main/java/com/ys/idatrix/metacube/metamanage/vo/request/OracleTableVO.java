package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import com.ys.idatrix.metacube.metamanage.domain.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName OracleTableVO
 * @Description oracle 表实体类
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "OracleTableVO", description = "oracle table 实体类")
@Data
public class OracleTableVO extends TableVO {

    @Valid
    private List<TableColumn> columnList; // 字段

    @NotNull(message = "主键不能为空")
    @Valid
    private TablePkOracle primaryKey; // 主键

    @Valid
    private List<TableIdxOracle> indexList; // 索引

    @Valid
    private List<TableFkOracle> foreignKeyList; // 外键

    @Valid
    private List<TableUnOracle> uniqueList; // 唯一约束

    @Valid
    private List<TableChOracle> checkList; // 检查约束

    @NotNull(message = "表设置不能为空")
    @Valid
    private TableSetOracle tableSetting; // 表设置

    public OracleTableVO() {
        this.setDatabaseType(DatabaseTypeEnum.ORACLE.getCode());
        this.setResourceType(1);// 1为表
    }

    public OracleTableVO(Metadata table, List<TableColumn> columnList, TablePkOracle primaryKey, List<TableIdxOracle> indexList,
                         List<TableFkOracle> foreignKeyList, List<TableUnOracle> uniqueList, List<TableChOracle> checkList,
                         TableSetOracle tableSetting) {
        BeanUtils.copyProperties(table, this);
        this.setColumnList(columnList);
        this.setPrimaryKey(primaryKey);
        this.setIndexList(indexList);
        this.setForeignKeyList(foreignKeyList);
        this.setUniqueList(uniqueList);
        this.setCheckList(checkList);
        this.setTableSetting(tableSetting);
    }

}