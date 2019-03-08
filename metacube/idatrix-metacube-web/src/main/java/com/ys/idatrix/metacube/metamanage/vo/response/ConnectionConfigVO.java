package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

/**
 * @ClassName ConnectionConfigVO
 * @Description 数据库连接参数
 * @Author ouyang
 * @Date
 */
@Data
public class ConnectionConfigVO {

    private String ip; // ip

    private String port; // 端口

    private String dataBaseName; // 数据库名，也就是实例名（oracle对应的就是SID）

    private String username; // 用户名

    private String password; // 密码

    private String type;// 数据库类型

    public ConnectionConfigVO() {

    }

    public ConnectionConfigVO(String ip, String port, String dataBaseName, String username, String password, String type) {
        this.ip = ip;
        this.port = port;
        this.dataBaseName = dataBaseName;
        this.username = username;
        this.password = password;
        this.type = type;
    }
}
