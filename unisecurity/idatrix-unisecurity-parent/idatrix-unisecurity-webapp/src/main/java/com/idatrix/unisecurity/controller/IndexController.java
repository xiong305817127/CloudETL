package com.idatrix.unisecurity.controller;

import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.core.shiro.session.dao.CustomShiroSessionDAO;
import com.idatrix.unisecurity.properties.BbsProperties;
import com.idatrix.unisecurity.properties.LoginProperties;
import com.idatrix.unisecurity.properties.SszProperties;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

@ApiIgnore
@RestController
public class IndexController {

    @Autowired
    private BbsProperties bbsProperties;

    @Autowired
    private SszProperties sszProperties;

    @Autowired
    private LoginProperties loginProperties;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResultVo index() {
        return ResultVoUtils.ok("部署成功");
    }

    @RequestMapping(value = "/showBbs", method = RequestMethod.GET)
    public ResultVo bbsProperties() {
        return ResultVoUtils.ok(bbsProperties);
    }

    @RequestMapping(value = "/showSsz", method = RequestMethod.GET)
    public ResultVo sszProperties() {
        return ResultVoUtils.ok(sszProperties);
    }

    @RequestMapping(value = "/showLogin", method = RequestMethod.GET)
    public ResultVo loginProperties() {
        return ResultVoUtils.ok(loginProperties);
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public ResultVo hello() {
        return ResultVoUtils.ok("hello word");
    }

}
