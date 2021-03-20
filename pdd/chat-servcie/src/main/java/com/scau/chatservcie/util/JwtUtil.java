package com.scau.chatservcie.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
//import enums.ResultCodeEnum;
//import exception.AuthenticateException;

import java.util.Date;
import java.util.Map;

/**
 * @program: pdd
 * @description: jwt工具类
 * @create: 2020-12-01 15:09
 **/
public class JwtUtil {
    /**
     * 生成一个jwt
     * @param name 用户名
     * @param secret 密钥
     * @param timeOut 超时事件
     * @return
     */
    public static String encode(String name, String secret, long timeOut) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String token = JWT.create()
                //设置过期时间为一个小时
                .withExpiresAt(new Date(System.currentTimeMillis() + timeOut))
                //设置负载
                .withClaim("name", name)
                .sign(algorithm);
        return token;
    }

    public static Map<String, Claim> decode(String token, String secret) {
        if (token == null || token.length() == 0) {
//            throw new AuthenticateException(ResultCodeEnum.TOKEN_ISNULL);
        }
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        return decodedJWT.getClaims();
    }
}
