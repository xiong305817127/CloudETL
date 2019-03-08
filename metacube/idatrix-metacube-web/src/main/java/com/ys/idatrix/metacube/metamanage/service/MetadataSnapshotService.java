package com.ys.idatrix.metacube.metamanage.service;

import com.ys.idatrix.metacube.metamanage.domain.Metadata;
import com.ys.idatrix.metacube.metamanage.domain.TableColumn;

import java.util.List;

/**
 * @ClassName MetadataSnapshotService
 * @Description
 * @Author ouyang
 * @Date
 */
public interface MetadataSnapshotService {

    // 获取元数据某个版本的基本信息
    Metadata getSnapshotMetadataInfoById(Long metadataId, Integer version);

    // 获取某个版本的字段列表
    List<TableColumn> getSnapshotColumnListByTableId(Long tableId, Integer version);

}