package com.idatrix.resource.exchange.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.common.utils.UserUtils;
import com.idatrix.resource.exchange.exception.InnerTerminalConfigException;
import com.idatrix.resource.exchange.exception.RequestDataException;
import com.idatrix.resource.exchange.service.IExchangeSubscribeService;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 处理和神马对接Restful
 */

@Controller
@RequestMapping("/exchange")
@Api(value = "/exchange" , tags="第三方处理-数据交换管理接口")
public class ExchangeController extends BaseController {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IExchangeSubscribeService exchangeSubscribeService;

    @Autowired
    private UserUtils userUtils;

    /**
     * Title: 提供给第三方对接
     * @param exchangeSubscribeVO
     * @return
     */
    @ApiOperation(value = "提供给第三方订阅接口", notes="提供给第三方订阅接口，需要分配用户和配置好资源代码前缀", httpMethod = "POST")
    @RequestMapping(value="/subscribe", method= RequestMethod.POST)
    @ResponseBody
    public Result subscribe(@RequestBody ExchangeSubscribeVO exchangeSubscribeVO) {
        String user = getUserName(); //"用户名按照神州数码处理，系统需要建立默认用户：神州数码";

        LOG.info("ExchangeController: ", exchangeSubscribeVO.toString());
        Long rentId = userUtils.getCurrentUserRentId();
        try{
            exchangeSubscribeService.processExchange(rentId, user, exchangeSubscribeVO);
        }catch (RequestDataException requestException) {
            requestException.printStackTrace();
            return Result.error(requestException.getErrorCode(), requestException.getMessage());
        }catch (InnerTerminalConfigException innerException){
            innerException.printStackTrace();
            return Result.error(innerException.getErrorCode(), innerException.getMessage());
        }catch(Exception e){
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.ok(true);
    }
}
