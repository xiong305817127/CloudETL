package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName TableViewDTO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class TableViewDTO implements Serializable {

    // 表
    private List<MetadataDTO> tableList;

    // 视图
    private List<MetadataDTO> viewList;
}