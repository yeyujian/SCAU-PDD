package com.scau.chatservcie.entity;

import java.util.Date;

/**
 * @program: pdd
 * @description: 消息类
 * @create: 2020-12-02 20:06
 **/
public class Message {

    /**
     * 1： 客户向店铺发消息
     * 2： 店铺向客户发消息
     * 3： 发送消息到直播间
     * 4： 发送通知到客户
     * 5： 发送图片
     */
    private Integer type;// 消息类型

    private ChatMessage message;// 聊天信息

    /**
     * type类型决定ext类型
     * 1： user信息
     * 2： shop信息
     * 3： user信息
     * 4： 发送者信息
     * 5： user信息
     */
    private Object ext;// 附加信息类型

    private Date createtime;

    public Message() {
        createtime = new Date();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public ChatMessage getMessage() {
        return message;
    }

    public void setMessage(ChatMessage message) {
        this.message = message;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    @Override
    public String toString() {
        return "Message{" + "type=" + type + ", message=" + message + ", ext=" + ext + '}';
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }
}

