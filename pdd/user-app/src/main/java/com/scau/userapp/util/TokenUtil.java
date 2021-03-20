package com.scau.userapp.util;

import java.util.Random;

/**
 * @program: pdd
 * @description: token生成器
 * @create: 2020-12-16 21:58
 **/
public class TokenUtil {

    /**
     * 根据长度生成token
     * @param length
     * @return
     */
    private static char[] codeChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456".toCharArray();
    public String createToken(int length){
        String captcha = ""; // 存放生成的验证码
        Random random = new Random();
        for(int i = 0; i < 4; i++) { // 循环将每个验证码字符绘制到图片上
            int index = random.nextInt(codeChar.length);
            captcha += codeChar[index];
        }
        return captcha;
    }
}
