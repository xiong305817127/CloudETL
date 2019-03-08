package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

import java.util.List;

/**
 * @ClassName ConnectionConfigVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class DBConnectionVO extends  ConnectionConfigVO {

    private List<TableVO> tableList;

    private List<TableVO> viewList;

    public DBConnectionVO() {
    }

    public DBConnectionVO(String ip, String port, String dataBaseName, String username, String password, String type) {
        super(ip, port, dataBaseName, username, password, type);
    }
}