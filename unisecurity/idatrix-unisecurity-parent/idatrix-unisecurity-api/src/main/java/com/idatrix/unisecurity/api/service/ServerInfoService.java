package com.idatrix.unisecurity.api.service;

import com.idatrix.unisecurity.api.domain.ServerInfo;

import java.util.List;

/**
 * Created by james on 2017/6/12.
 */
public interface ServerInfoService {

    List<ServerInfo> findServerInfoByUserId(Long userId);

}
