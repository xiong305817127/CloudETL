package com.idatrix.unisecurity.handler;

import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@ControllerAdvice
@ResponseBody
public class WebExceptionHandler {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     * @author oyr
     * @date 2018/11/30 10.54
     * @param [e]
     * @return com.oyr.validation.vo.ResultVo
     */
    @ExceptionHandler(value = { SecurityException.class })
    @ResponseStatus(HttpStatus.OK)
    public ResultVo securityException(SecurityException e) {
        log.error("securityException：{}", e.getMessage());
        return ResultVoUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     * @author oyr
     * @date 2018/8/22 16:47
     * @param [e]
     * @return com.oyr.validation.vo.ResultVo
     */
    @ExceptionHandler(value = { ConstraintViolationException.class })
    @ResponseStatus(HttpStatus.OK)
    public ResultVo unknownException(ConstraintViolationException e) {
        log.error("参数异常 : {}", e.getMessage());
        // 处理方法参数错误抛出的异常
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuffer sb = new StringBuffer();
        for (ConstraintViolation<?> violation : constraintViolations) {
            // 收集第一个异常信息就好了
            sb.append(violation.getMessage());
            break;
        }
        return ResultVoUtils.error(ResultEnum.PARAM_ERROR.getCode(), sb.toString());
    }

    /**
     * @title httpRequestMethodNotSupportedException
     * @description 处理405异常
     * @param: e
     * @author oyr
     * @updateTime 2018/10/8 19:24
     * @return: com.idatrix.unisecurity.common.vo.ResultVo
     * @throws
     */
    @ExceptionHandler(value =  {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultVo httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e){
        log.error("405 error:{}", e.getMessage());
        return ResultVoUtils.error(ResultEnum.HTTP_METHOD_ERROR.getCode(), ResultEnum.HTTP_METHOD_ERROR.getMessage());
    }

    /**
     * @title serverException
     * @description 服务器异常
     * @param: e
     * @author oyr
     * @updateTime 2018/10/8 19:25
     * @return: com.idatrix.unisecurity.common.vo.ResultVo
     * @throws
     */
    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultVo serverException(Exception e) {
        printlnException(e);
        // 服务器异常
        log.error("500 error:{}", e.getMessage());
        return ResultVoUtils.error(ResultEnum.SERVER_ERROR.getCode(), ResultEnum.SERVER_ERROR.getMessage());
    }

    public void printlnException(Exception e){
        e.printStackTrace();
    }
}
