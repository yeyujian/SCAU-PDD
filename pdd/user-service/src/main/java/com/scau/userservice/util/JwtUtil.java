package com.scau.userservice.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import enums.ResultCodeEnum;
import exception.AuthenticateException;

import java.util.Date;
import java.util.Map;

/**
 * @program: pdd
 * @description: jwt工具类
 * @create: 2020-11-20 11:01
 **/
public class JwtUtil {

    /**
     * 生成一个jwt
     * @param name 用户名
     * @param role 用户角色
     * @param secret 密钥
     * @param timeOut 超时事件
     * @return
     */
    public static String encode(String name,String role, String secret, long timeOut) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                //设置过期时间为一个小时
                .withExpiresAt(new Date(System.currentTimeMillis() + timeOut))
                //设置负载
                .withClaim("name", name)
                .withClaim("role", role)
                .sign(algorithm);
        return token;
    }

    public static String encode(Map<String, Claim> map,String token,String url, String secret, long timeOut){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String name=map.get("name").asString();
        String role=map.get("role").asString();
        String tk = JWT.create()
                //设置过期时间为一个小时
                .withExpiresAt(new Date(System.currentTimeMillis() + timeOut))
                //设置负载
                .withClaim("name", name)
                .withClaim("role", role)
                .withClaim("token", token)
                .withClaim("url", url)
                .sign(algorithm);
        return tk;
    }

    public static Map<String, Claim> decode(String token, String secret) {
        if (token == null || token.length() == 0) {
            throw new AuthenticateException(ResultCodeEnum.TOKEN_ISNULL);
        }
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT.getClaims();
    }
}
