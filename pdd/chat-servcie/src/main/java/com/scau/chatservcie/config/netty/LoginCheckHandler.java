package com.scau.chatservcie.config.netty;

import com.auth0.jwt.interfaces.Claim;
import com.scau.chatservcie.config.ServiceCenter;
import com.scau.chatservcie.entity.UserChannelMap;
import com.scau.chatservcie.util.BloomFilterUtil;
import com.scau.chatservcie.util.JwtUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import model.User;
import org.apache.commons.lang3.StringUtils;
import service.UserService;
import util.RedisUtil;
import util.SpringUtil;

import java.util.List;
import java.util.Map;

/**
 * @program: pdd
 * @description: 聊天登录检查
 * @create: 2020-11-30 10:43
 **/
public class LoginCheckHandler extends ChannelInboundHandlerAdapter {

    private static RedisUtil redisUtil = SpringUtil.getBean(RedisUtil.class);

    private static ServiceCenter serviceCenter=SpringUtil.getBean(ServiceCenter.class);

    private static BloomFilterUtil bloomFilterUtil=SpringUtil.getBean(BloomFilterUtil.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        UserService userService=serviceCenter.getUserService();
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String token = getValueByParam(request,"jwt");
            System.out.println(token);
            String role=getValueByParam(request,"role");
            System.out.println(role);
            AttributeKey<Object> key = AttributeKey.valueOf("user::role");
            try{
                if (redisUtil.get(token) != null&&role != null&&role.equals("user")||role.equals("seller")) {
                    String secret=(String) redisUtil.get(token);
                    System.out.println(secret);
                    Map<String, Claim> map = JwtUtil.decode(token,secret);
                    String username=map.get("name").asString();
                    if(StringUtils.isBlank(username)) {
                        System.out.println("非法jwt");
                        ctx.close();//非法登录关闭
                    }
                    //处理多设备登录
                    Channel channel = UserChannelMap.get(username+"::"+role);
                    User user=userService.getUserByUsername(username);
                    System.out.println(user.getUsername());
                    if (channel != null) {
                        System.out.println("多设备登录，强制关闭连接");
                        channel.close();
                        UserChannelMap.remove(username+"::"+role);
                    }
                    if(role.equals("user"))
                        ctx.channel().attr(key).setIfAbsent(user);
                    else {
                        ctx.channel().attr(key).setIfAbsent(userService.getShopById(user.getId()));
                    }
                    //加入布隆过滤器 减钱离线缓存错误
                    bloomFilterUtil.addByBloomFilter(role,username+"::"+role);
                    ctx.fireChannelRead(request.retain());
                } else {
                    System.out.println("未登录，非法链接");
                    ctx.close();//非法登录关闭
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("登录失败，认证发生错误");
                ctx.close();//非法登录关闭
            }
        }
        ctx.fireChannelRead(msg);
    }


    private String getValueByParam(FullHttpRequest req,String param){
        if(param=="role") return "user";

        String cookie = (req.headers().get("Cookie"));
        int flag=cookie.indexOf(";");
        if(flag>0) return cookie.substring(cookie.indexOf("=") + 1,flag);
        if(true) return cookie.substring(cookie.indexOf("=") + 1);
        HttpMethod method = req.method();
        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            for(Map.Entry<String, List<String>> entry: decoder.parameters().entrySet()){
                if(entry.getKey().equals(param)) return entry.getValue().get(0);
            }
        }
        return "";
    }
}
