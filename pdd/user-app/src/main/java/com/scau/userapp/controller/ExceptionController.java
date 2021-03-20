package com.scau.userapp.controller;

import controller.ExceptionHandlerController;
import entity.ResultEntity;
import exception.AuthenticateException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.ValidationException;

/**
 * @program: pdd
 * @description: 异常管理控制层
 * @create: 2020-12-15 21:37
 **/
//@RestControllerAdvice
public class ExceptionController extends ExceptionHandlerController {

    @ExceptionHandler(ValidationException.class)
    public ResultEntity authenticateException(ValidationException e, HttpServletRequest request, HttpServletResponse response){
        return ResultEntity.failedResult(e.getMessage());
    }
}
