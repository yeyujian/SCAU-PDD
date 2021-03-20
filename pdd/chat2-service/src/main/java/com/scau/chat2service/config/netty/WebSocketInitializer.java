package com.scau.chat2service.config.netty;

import com.scau.chat2service.config.netty.ChatHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.web.HttpRequestHandler;

/**
 * @program: pdd
 * @description: websocket初始化
 * @create: 2020-11-30 10:44
 **/
public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //http解码器
        pipeline.addLast(new HttpServerCodec())
                //支持写大数据流
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpObjectAggregator(1024 * 64))
//                .addLast(new LoginCheckHandler())
                //http聚合器
                .addLast(new WebSocketServerProtocolHandler("/ws"))
                //添加Netty空闲超时检查的支持
                //120:读空闲超时,300:写空闲超时,500: 读写空闲超时
//                .addLast(new IdleStateHandler(120, 300, 500))
//                .addLast(new HearBeatHandler())
                .addLast(new ChatHandler());

    }
}