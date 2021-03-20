package com.scau.chatservcie.entity;

/**
 * @program: pdd
 * @description: 聊天消息类型
 * @create: 2020-12-02 20:07
 **/
public class ChatMessage {

    private String username;

    private String shopname;

    private String message;

    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
