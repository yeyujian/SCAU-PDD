package com.scau.chatservcie.config.netty;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @program: pdd
 * @description: netty启动控制器
 * @create: 2020-11-30 10:45
 **/
@Component
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("尝试初始化netty服务器");
        //事件获得上下文对象化后启动服务器
        System.out.println(event.getApplicationContext().getParent());
        if(event.getApplicationContext().getParent()!=null){
            WebSocketServer.getInstance().start();
            //关闭
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    WebSocketServer.getInstance().destroy();
                }
            });
        }
    }
}
