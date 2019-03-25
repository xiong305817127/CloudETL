package com.idatrix.resource.webservice.dao;


import com.idatrix.resource.webservice.po.ServicePO;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public interface ServiceDAO {

    ServicePO getServiceById(Long id);
}
