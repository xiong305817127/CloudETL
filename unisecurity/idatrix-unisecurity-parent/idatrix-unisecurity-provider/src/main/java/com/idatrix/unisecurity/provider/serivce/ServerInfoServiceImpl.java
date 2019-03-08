package com.idatrix.unisecurity.provider.serivce;

import com.idatrix.unisecurity.api.domain.ServerInfo;
import com.idatrix.unisecurity.api.service.ServerInfoService;
import com.idatrix.unisecurity.common.dao.ServerInfoProviderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by james on 2017/6/12.
 */
@Service
public class ServerInfoServiceImpl implements ServerInfoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private ServerInfoProviderMapper serverInfoMapper;

    public List<ServerInfo> findServerInfoByUserId(Long userId){
    	logger.info("findServerInfoByUserId：{}", userId);
        try{
            return serverInfoMapper.findServerInfoByUserId(userId);
        }catch (Exception e){
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
