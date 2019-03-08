package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TableColumnService
 * @Description table column 服务层
 * @Author ouyang
 * @Date
 */
public interface TableColumnService {

    // 根据表名获取字段列表
    List<TableColumn> getTableColumnListByTableId(Long tableId);

    // 根据ids获取字段
    List<TableColumn> getTableColumnListByIdList(List<String> strings);

    // 保存字段
    void insertColumnList(List<TableColumn> tableColumnList, Long tableId, String creator, Date createTime);

    // 根据表ID删除字段
    void deleteByTableId(Long id);

}
