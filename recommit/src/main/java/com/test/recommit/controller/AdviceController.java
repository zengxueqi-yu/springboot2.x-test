package com.test.recommit.controller;

import com.test.recommit.config.BnException;
import com.test.recommit.config.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class AdviceController {

    private static final String UNIFIED_FAIL_MSG = "服务器内部错误";
    /**
     * 控制器层公共异常处理
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(BnException.class)
    public Result bnException(BnException be) {
        log.error(be.toString());
        be.printStackTrace();
        return Result.error(be.getCode(),errorMsg(be.getMessage()));
    }

    /**
     * 控制器层公共异常处理
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(Exception.class)
    public Result exception(Exception be) {
        log.error("{}", be.getMessage());
        be.printStackTrace();
        return Result.error(errorMsg(be.getMessage()));
    }

    /**
     * 方法参数校验异常
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result methodArgument(MethodArgumentNotValidException be) {
        BindingResult br = be.getBindingResult();
        FieldError error = br.getFieldError();
        StringBuffer sb = new StringBuffer("校验异常:");
        sb.append(error.getField()).append(error.getDefaultMessage());
        log.error(sb.toString());
        be.printStackTrace();
        return Result.error(errorMsg(error.getDefaultMessage()));
    }

    /**
     * 参数绑定异常
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(BindException.class)
    public Result bindException(BindException be) {
        FieldError error = be.getFieldError();
        StringBuffer sb = new StringBuffer("参数绑定异常:");
        sb.append(error.getField()).append(error.getDefaultMessage());
        log.error(sb.toString());
        be.printStackTrace();
        return Result.error(errorMsg(be.getMessage()));
    }

    /**
     * 参数校验异常
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(ValidationException.class)
    public Result constraintViolation(ValidationException be) {
        log.error(be.getMessage());
        be.printStackTrace();
        return Result.error(errorMsg(be.getMessage()));
    }

    /**
     * http消息转换异常
     * @param be 接收异常
     * @return 返回结果
     */
    @ExceptionHandler(HttpMessageConversionException.class)
    public Result parameterTypeConversionException(HttpMessageConversionException be) {
        log.error(be.getMessage());
        be.printStackTrace();
        return Result.error(errorMsg(be.getMessage()));
    }

    private String errorMsg(String message) {
        return StringUtils.isEmpty(message)?UNIFIED_FAIL_MSG:message;
    }

}