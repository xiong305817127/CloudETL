package com.ys.idatrix.metacube.metamanage.service.impl;

import com.ys.idatrix.metacube.metamanage.domain.TableColumn;
import com.ys.idatrix.metacube.metamanage.mapper.TableColumnMapper;
import com.ys.idatrix.metacube.metamanage.service.TableColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TableColumnServiceImpl
 * @Description 表字段服务层实现
 * @Author ouyang
 * @Date
 */
@Transactional
@Service
public class TableColumnServiceImpl implements TableColumnService {

    @Autowired
    private TableColumnMapper tableColumnMapper;

    public List<TableColumn> getTableColumnListByTableId(Long tableId) {
        List<TableColumn> list = tableColumnMapper.findTableColumnListByTableId(tableId);
        return list;
    }

    @Override
    public List<TableColumn> getTableColumnListByIdList(List<String> idList) {
        List<TableColumn> list = tableColumnMapper.getTableColumnListByIdList(idList);
        return list;
    }

    @Override
    public void insertColumnList(List<TableColumn> tableColumnList, Long tableId, String creator, Date createTime) {
        int maxLocation = tableColumnMapper.selectMaxLocationByTableId(tableId);
        for (TableColumn column : tableColumnList) {
            // 补全参数
            column.setTableId(tableId);
            column.setCreator(creator);
            column.setCreateTime(createTime);
            column.setModifier(creator);
            column.setModifyTime(createTime);
            column.setIsDeleted(false);
            column.setLocation(++maxLocation);
            // insert table column
            tableColumnMapper.insertSelective(column);
        }
    }

    @Override
    public void deleteByTableId(Long id) {
        tableColumnMapper.deleteByTableId(id);
    }

}
