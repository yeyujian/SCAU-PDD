package com.scau.chatservcie.entity;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @program: pdd
 * @description: 用户聊天channel管理
 * @create: 2020-12-01 15:19
 **/
public class UserChannelMap {

    private static Map<String, Channel> userChannelMap;

    static {
        userChannelMap = new ConcurrentHashMap<>();
    }


    /**
     * 建立用户和通道直接的关联
     *
     * @param username
     * @param channel
     */
    public static void put(String username, Channel channel) {
        userChannelMap.put(username, channel);
    }

    /**
     * 解除用户和通道直接的关系
     *
     * @param username
     */
    public static void remove(String username) {
        userChannelMap.remove(username);
    }

    /**
     * 根据用户id,获取通道
     */
    public static Channel get(String username) {
        return userChannelMap.get(username);
    }

    public static void removeByChannelId(String channelId) {
        for (String username : userChannelMap.keySet()) {
            if (userChannelMap.get(username).id().asLongText().equals(channelId)) {
                System.out.println("客户端连接断开,取消用户" + username + "与通道" + channelId + "的关联");
                userChannelMap.remove(username);
                break;
            }

        }
    }
}
