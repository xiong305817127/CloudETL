package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.api.beans.PageResultBean;
import com.ys.idatrix.metacube.metamanage.vo.request.DBViewVO;
import com.ys.idatrix.metacube.metamanage.vo.request.MetadataSearchVo;
import com.ys.idatrix.metacube.metamanage.vo.request.MySqlTableVO;
import com.ys.idatrix.metacube.metamanage.vo.request.TableVO;

import java.util.List;

/**
 * @ClassName MysqlTableService
 * @Description mysql table service
 * @Author ouyang
 * @Date
 */
public interface MysqlTableService {

    // 搜索
    PageResultBean<TableVO> search(MetadataSearchVo searchVO);

    // 根据表ID查询表信息
    MySqlTableVO searchById(Long tableId);

    // 根据模式ID查询表集合
    List<TableVO> searchBySchemaId(Long schemaId);

    // 新增表数据，并生效
    void add(MySqlTableVO mysqlTable);
    
    // 新增表数据,只插入数据库不生成表
    void addTable(MySqlTableVO mysqlTable);
    
    //新增直采数据库
    void addMiningTable(MySqlTableVO mysqlTable ) ;

    // 修改表数据，并生效
    void update(MySqlTableVO mysqlTable);

    // 将表数据生效到实体表
    void generateOrUpdateEntityTable(Long tableId);

    // 保存草稿
    void addDraft(MySqlTableVO mysqlTable);

    // 修改草稿
    void updateDraft(MySqlTableVO mysqlTable);

    // 删除表或草稿表
    void delete(List<Long> idList);
}
