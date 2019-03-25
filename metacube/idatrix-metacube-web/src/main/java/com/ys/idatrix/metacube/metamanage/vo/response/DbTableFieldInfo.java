package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**   
 * @ClassName:  tableInfo   
 * @Description:TODO(这里用一句话描述这个类的作用)   
 * @author: chl 
 * @date:   2017年6月7日 下午1:57:48   
 *     
 */
@Data
public class DbTableFieldInfo implements Serializable {

    /**
     * 
     * table id
     */

    private Long id;


    /**
     * 表名称
     */
    private String tableName;

    /**
     * 字段列表
     */
    private List<DbFieldBean> fields;

}
