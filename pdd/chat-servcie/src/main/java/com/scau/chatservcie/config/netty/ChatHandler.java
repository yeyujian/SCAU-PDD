package com.scau.chatservcie.config.netty;

import com.scau.chatservcie.config.ServiceCenter;
import com.scau.chatservcie.entity.Message;
import com.scau.chatservcie.entity.UserChannelMap;
import com.scau.chatservcie.util.BloomFilterUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import model.Shop;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.BroadcastServcie;
import util.RedisUtil;
import util.SpringUtil;
import com.alibaba.fastjson.JSON;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @program: pdd
 * @description: 聊天处理
 * @create: 2020-11-30 10:42
 **/
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * 调测日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatHandler.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM");

    private  RedisUtil redisUtil= SpringUtil.getBean(RedisUtil.class);

    private  ServiceCenter serviceCenter=SpringUtil.getBean(ServiceCenter.class);

    private  BroadcastServcie broadcastServcie;

    private  BloomFilterUtil bloomFilterUtil=SpringUtil.getBean(BloomFilterUtil.class);


    public ChatHandler() {
        if(broadcastServcie==null) broadcastServcie=serviceCenter.getBroadcastServcie();
    }


    // 定义channel集合,管理channel,传入全局事件执行器
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {


        AttributeKey<Object> key = AttributeKey.valueOf("user::role");
        String content = textWebSocketFrame.text();
        System.out.println(content);
        Channel channel = ctx.channel(); // 发送目标的客户端
        Message message = JSON.parseObject(content, Message.class);
        Object curUser=ctx.channel().attr(key).get();
//        System.out.println(curUser);
        /**
         * 1： 客户向店铺发消息
         * 2： 店铺向客户发消息
         * 3： 发送消息到直播间
         * 4： 发送通知到客户
         * 5： 发送图片
         * type类型决定ext类型
         * 1： user信息
         * 2： shop信息
         * 3： user信息
         * 4： 发送者信息
         * 5： user信息
         */
        switch (message.getType()){
            case 1:
                //验证user是否存在，使用布隆过滤器检测身份是否存在
                if(!bloomFilterUtil.includeByBloomFilter("seller",message.getMessage().getShopname()+"::"+"seller")){
                    throw new RuntimeException("身份不匹配，非法数据  1");
                }
                message.setExt((User)curUser);
                channel = UserChannelMap.get(message.getMessage().getShopname()+"::"+"seller");
                //先尝试获取未读数据再发送
                List<Object> unreadMsgList=redisUtil.lGet("UnReadMsg:seller:"+message.getMessage().getShopname(),0,-1);

                Iterator<Object> iterator = unreadMsgList.iterator();
                while (iterator.hasNext()){
                    Message  msg=(Message) iterator.next();
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                }
                if (channel == null) { // 好友不在线
                    System.out.println(message.getMessage().getShopname().concat(":不在线"));
                    redisUtil.lSet("UnReadMsg:seller:"+message.getMessage().getShopname(),message);
                } else {
                    channel.writeAndFlush(new TextWebSocketFrame(content));// 发送消息
                }
                break;
            case 2:
                //验证user是否存在，使用布隆过滤器检测身份是否存在
                if(!bloomFilterUtil.includeByBloomFilter("user",message.getMessage().getUsername()+"::"+"user")){
                    throw new RuntimeException("身份不匹配，非法数据   2");
                }
                message.setExt((Shop)curUser);
                channel = UserChannelMap.get(message.getMessage().getShopname()+"::"+"seller");
                //先尝试获取未读数据再发送
                unreadMsgList=redisUtil.lGet("UnReadMsg:user:"+message.getMessage().getUsername(),0,-1);

                iterator = unreadMsgList.iterator();
                while (iterator.hasNext()){
                    Message  msg=(Message) iterator.next();
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                }
                if (channel == null) { // 商家不在线
                    System.out.println(message.getMessage().getUsername().concat(":不在线"));
                    redisUtil.lSet("UnReadMsg:user:"+message.getMessage().getUsername(),message);
                } else {
                    channel.writeAndFlush(new TextWebSocketFrame(content));// 发送消息
                }
                break;
            case 3:
                if(!((User)curUser).getUsername().equals(message.getMessage().getUsername())) throw new Exception("身份不匹配，非法连接   3");
                message.setExt((User)curUser);
                //获得所有列表
                Map<Object, Object> map=redisUtil.hmget(message.getMessage().getToken()+"::ZBchat");
                for(Map.Entry<Object, Object> entry:map.entrySet()){
                    String username=(String)entry.getKey();
                    channel=UserChannelMap.get(username+"::user");
                    channel.writeAndFlush(new TextWebSocketFrame(content));
                }
                break;
            case 4:
                break;
            case 5:
                break;
            case 6://获取客户未读数据  并建立连接
                String username=message.getMessage().getUsername();
                if(!((User)curUser).getUsername().equals(username)) throw new Exception("身份不匹配，非法连接   4");
                unreadMsgList=redisUtil.lGet("UnReadMsg:user:"+username,0,-1);
                iterator = unreadMsgList.iterator();
                while (iterator.hasNext()){
                    Message  msg=(Message) iterator.next();
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                }
                UserChannelMap.put(username+"::user",ctx.channel());
                break;
            case 7://获取商家未读数据  并建立连接
                String shopname=message.getMessage().getShopname();
                if(!((Shop)curUser).getShopName().equals(shopname)) throw new Exception("身份不匹配，非法连接     5");
                unreadMsgList=redisUtil.lGet("UnReadMsg:seller:"+shopname,0,-1);
                iterator = unreadMsgList.iterator();
                while (iterator.hasNext()){
                    Message  msg=(Message) iterator.next();
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                }
                UserChannelMap.put(shopname+"::seller",ctx.channel());
                break;
            case 8://建立与直播间的连接
                String token=message.getMessage().getToken();
                //检查直播是否存在
                if(!broadcastServcie.CheckZbing(token)) throw new Exception("不存在直播");
                if(!((User)curUser).getUsername().equals(message.getMessage().getUsername())) throw new Exception("身份不匹配，非法连接     6");
                redisUtil.hset(token+"::ZBchat",message.getMessage().getUsername(),1);// 加入直播间
                UserChannelMap.put(message.getMessage().getUsername()+"::user",ctx.channel());
                break;
        }
    }

    // 新客户端连接时自行调用
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("有用户尝试连接");
        channelGroup.add(ctx.channel());
    }


    // 出现异常是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        System.out.println("出现异常.....关闭通道!");
        System.out.println(cause.getMessage());
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        channelGroup.remove(ctx.channel());
        LOGGER.info(cause.getMessage());
    }

    // 当客户端关闭连接时关闭通道
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户主动关闭连接");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        channelGroup.remove(ctx.channel());
        ctx.channel().close();
    }

}

