package com.idatrix.unisecurity.controller;

import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @ClassName ErrorController
 * @Description 异常处理controller
 * @Author ouyang
 * @Date 2018/9/20 14:46
 * @Version 1.0
 **/
@ApiIgnore
@RestController
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultVo handler404() {
        return ResultVoUtils.error(ResultEnum.URL_NOT_FIND.getCode(), ResultEnum.URL_NOT_FIND.getMessage());
    }

}
