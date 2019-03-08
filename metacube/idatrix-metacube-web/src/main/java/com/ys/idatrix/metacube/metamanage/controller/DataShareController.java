package com.ys.idatrix.metacube.metamanage.controller;

import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.metamanage.service.DataShareService;
import com.ys.idatrix.metacube.metamanage.vo.response.DBConnectionVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName DataShareController
 * @Description
 * @Author ouyang
 * @Date
 */
@Validated
@Slf4j
@RequestMapping("/data/share")
@RestController
@Api(value = "/DataShareController", tags = "元数据管理-数据共享")
public class DataShareController {

    @Autowired
    private DataShareService dataShareService;

    @GetMapping("/tableOrView/{username}")
    public ResultBean<List<DBConnectionVO>> findTableOrView(@NotBlank(message = "用户名不能为空") @PathVariable("username") String username) {
        List<DBConnectionVO> list = dataShareService.findTableOrView(username);
        return ResultBean.ok(list);
    }

}