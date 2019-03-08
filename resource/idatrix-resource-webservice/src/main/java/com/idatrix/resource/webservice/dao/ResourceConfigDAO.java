package com.idatrix.resource.webservice.dao;


import com.idatrix.resource.webservice.po.ResourceConfigPO;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public interface ResourceConfigDAO {

    ResourceConfigPO getConfigById(Long id);

}
