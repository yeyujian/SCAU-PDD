package com.scau.userapp.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: pdd
 * @description: jwt工具类
 * @create: 2020-12-16 21:43
 **/
public class JwtUtil {
    /**
     * 从cookie中获取jwt
     * @param request
     * @return
     */
    public static String GetJwtFromCookies(HttpServletRequest request){
        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("jwt")){
                    return cookie.getValue();
                }
            }
        }

        throw new RuntimeException("jwt是空的");
    }
}
