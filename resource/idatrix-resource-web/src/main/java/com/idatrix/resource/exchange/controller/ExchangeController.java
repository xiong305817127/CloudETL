package com.idatrix.resource.exchange.controller;

import com.idatrix.resource.common.controller.BaseController;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.resource.exchange.service.IExchangeSubscribeService;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理和神马对接Restful
 */

@Controller
@RequestMapping("/exchange")
public class ExchangeController extends BaseController {

    private final Logger LOG= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IExchangeSubscribeService exchangeSubscribeService;

    /**
     *  Title: 通过Excel 批量导入 资源分类
     */
    @RequestMapping(value="/subscribe", method= RequestMethod.POST)
    @ResponseBody
    public Result subscribe(@RequestBody ExchangeSubscribeVO exchangeSubscribeVO) {
        String user = getUserName(); //"用户名按照神州数码处理，系统需要建立默认用户：神州数码";
        LOG.info("ExchangeController: ", exchangeSubscribeVO.toString());

        try{
            exchangeSubscribeService.processExchange(user, exchangeSubscribeVO);
        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(6001000, e.getMessage());
        }
        return Result.ok(true);
    }
}
