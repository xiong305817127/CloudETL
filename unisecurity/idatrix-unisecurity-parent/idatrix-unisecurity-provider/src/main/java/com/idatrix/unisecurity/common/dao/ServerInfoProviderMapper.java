package com.idatrix.unisecurity.common.dao;

import java.util.List;

import com.idatrix.unisecurity.api.domain.ServerInfo;

public interface ServerInfoProviderMapper {

	List<ServerInfo> findServerInfoByUserId(Long userId);

}
