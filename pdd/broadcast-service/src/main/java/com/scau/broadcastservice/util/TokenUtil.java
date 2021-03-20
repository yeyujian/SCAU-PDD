package com.scau.broadcastservice.util;


import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @program: pdd
 * @description: token管理工具
 * @create: 2020-11-27 12:51
 **/
public class TokenUtil {

    /**
     * 生成token
     * @return
     */
    public static String  createToken(String username) {
        String token = (System.currentTimeMillis() + new Random().nextInt(999999999)) + username;
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte md5[] =  md.digest(token.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(md5);
        } catch (NoSuchAlgorithmException  e) {
            e.printStackTrace();
        }
        return null;
    }
}
