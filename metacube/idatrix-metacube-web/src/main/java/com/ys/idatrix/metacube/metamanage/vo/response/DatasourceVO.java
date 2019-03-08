package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

/**
 * 数据源VO 包含了服务器和数据库的一些属性
 *
 * @author wzl
 */
@Data
public class DatasourceVO {

    private String ip;

    private String port;

    private String type;

    private String username;

    private String password;

    private String dbName;
}
