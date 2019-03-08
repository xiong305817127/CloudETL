package com.ys.idatrix.metacube.common.handler;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.common.exception.MetaDataException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class MetadataExceptionHandler {

    @ExceptionHandler(value = {MetaDataException.class})
    @ResponseStatus(HttpStatus.OK)
    public ResultBean myException(MetaDataException e) {
        log.error("自定义异常 : {}", e);
        return ResultBean.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultBean unknownException(MethodArgumentNotValidException e) {
        StringBuilder builder = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            builder.append(error.getDefaultMessage());
            break;
        }
        log.error("400 error : {}", e);
        return ResultBean.error(String.valueOf(HttpStatus.BAD_REQUEST.value()), builder.toString());
    }

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResultBean httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        log.error("405 error:{}", e);
        return ResultBean.error(String.valueOf(HttpStatus.METHOD_NOT_ALLOWED),
                ResultEnum.HTTP_METHOD_ERROR.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultBean notFoundPage404(NoHandlerFoundException e) {
        log.error("404 error:{}", e);
        return ResultBean
                .error(String.valueOf(HttpStatus.NOT_FOUND), ResultEnum.URL_NOT_FIND.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultBean serverException(Exception e) {
        log.error("500 error:{}", e);
        return ResultBean
                .error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR),
                        ResultEnum.SERVER_ERROR.getMessage());
    }
}
