package com.scau.userapp.controller;

import annotation.JwtCheck;
import entity.ResultEntity;
import enums.ResultCodeEnum;
import exception.ServiceException;
import model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.JwtService;
import service.MailService;
import service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.ValidationException;
import java.util.Random;

/**
 * @program: pdd
 * @description: 用户管理控制层
 * @create: 2020-12-15 21:35
 **/
@RestController
@RequestMapping("/user")
public class UserController {


//    @DubboReference
    private MailService mailService;

    @DubboReference
    private JwtService jwtService;

    @DubboReference
    private UserService userService;

    @GetMapping("/login")
    public ResultEntity login(@Validated User user, HttpServletResponse response,BindingResult bindingResult,HttpServletRequest request) throws Exception {
        validData(bindingResult);
        String jwt=jwtService.login(user);
        if(!StringUtils.isBlank(jwt)){
            Cookie cookie=new Cookie("jwt",jwt);
            cookie.setPath("/");
            response.addCookie(cookie);
            return new ResultEntity.Builder().messge(jwt).code(ResultCodeEnum.SUCCESS.getCode()).build();
        }else{
            throw new RuntimeException("登录失败 jwt为空");
        }
    }

    @PostMapping("/register")
    public ResultEntity register(User user, @Validated @NotBlank(message = "验证码不能为空") String code,HttpServletRequest request) throws Exception {
        String captcha=(String) request.getSession().getAttribute("code");
        request.getSession().invalidate();
        if(captcha==null||!captcha.equals(code)) return ResultEntity.failedResult("验证码错误");
        if(!userService.addUser(user))  return ResultEntity.failedResult("注册失败");
        return new ResultEntity.Builder().data(user).code(ResultCodeEnum.SUCCESS.getCode()).build();
    }


    @GetMapping("/code")
    public ResultEntity createCode(@Validated @NotBlank(message = "邮箱不能为空") @Email String email, HttpServletRequest request){
        // 验证码中所使用到的字符
        char[] codeChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456".toCharArray();
        String captcha = ""; // 存放生成的验证码
        Random random = new Random();
        for(int i = 0; i < 4; i++) { // 循环将每个验证码字符绘制到图片上
            int index = random.nextInt(codeChar.length);
            captcha += codeChar[index];
        }
//        mailService.sendMail(email,"发送验证码给你",captcha);
        System.out.println(captcha);
        request.getSession().setAttribute("code",captcha);
        return ResultEntity.successResult();
    }

    @GetMapping("/test")
//    @JwtCheck
    public ResultEntity Test(){
        return ResultEntity.successResult();
    }


    private boolean checkCode(String jwt,String token){
        return jwtService.checkJwtToken(jwt,token,"127.0.0.1");
    }

    private void validData(BindingResult bindingResult) throws ValidationException {
        if (bindingResult.hasErrors()) {
            StringBuffer sb = new StringBuffer();
            for (ObjectError error : bindingResult.getAllErrors()) {
                sb.append(error.getDefaultMessage());
            }
            throw new ValidationException(sb.toString());
        }
    }
}
