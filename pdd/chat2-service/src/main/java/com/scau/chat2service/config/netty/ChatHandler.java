package com.scau.chat2service.config.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    // 定义channel集合,管理channel,传入全局事件执行器
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ChannelGroup getChannelGroup() {
        return channelGroup;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        System.out.println("有消息进入？？！！！！");
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
    }

    // 当客户端关闭连接时关闭通道
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("用户主动关闭连接");
        channelGroup.remove(ctx.channel());
        ctx.channel().close();
    }

}

