package com.scau.userservice.util;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;

/**
 * @program: pdd
 * @description: md5 加密工具
 * @create: 2020-11-20 11:27
 **/
public class MD5Util {

    private static String salt="scau_yyj_sjy";
    /**
     * 对字符串进行md5加密
     * @param strValue
     * @return
     * @throws Exception
     */
    public static String getMD5Str(String strValue) throws Exception {
        strValue += salt;
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return newstr;
    }
}
