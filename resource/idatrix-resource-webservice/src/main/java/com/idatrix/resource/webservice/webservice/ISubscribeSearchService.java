package com.idatrix.resource.webservice.webservice;

import com.idatrix.resource.webservice.dto.ParamDTO;
import com.idatrix.resource.webservice.dto.ResultDTO;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService
public interface ISubscribeSearchService {

    /*
    *   数据库查询
    *
    *  @param: inputParams 查询条件
    *  @param: subscribeKey 查询业务校验编码
    *  @param: pageNum  页面序号
    *  @param: pageSize 页面大小
    *
    */
    @WebMethod
    ResultDTO databaseSearchByCondition(@WebParam(name="inputParams")List<ParamDTO> inputParams,
                                        @WebParam(name="subscribeKey")String subscribeKey,
                                        @WebParam(name="pageNum")Integer pageNum,
                                        @WebParam(name="pageSize")Integer pageSize);
}
