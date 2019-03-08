package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.dubbo.provider.MetadataDatabaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2019/1/15.
 */
@ApiIgnore
@Slf4j
@RestController
public class IndexController {

    @Autowired
    private MetadataDatabaseServiceImpl databaseService;

    @GetMapping("/")
    public ResultBean index() {
        log.info("部署成功");
        return ResultBean.ok("部署成功");
    }

    @GetMapping("/test")
    public ResultBean test(Long renterId) {
        return databaseService.registerOrUpdatePlatformDatabaseInfo(renterId);
    }
}
