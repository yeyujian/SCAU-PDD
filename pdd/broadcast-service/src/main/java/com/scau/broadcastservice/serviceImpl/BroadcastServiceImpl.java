package com.scau.broadcastservice.serviceImpl;

import com.scau.broadcastservice.model.ZbInfoModel;
import com.scau.broadcastservice.util.TokenUtil;
import model.ZbInfo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import service.BroadcastServcie;
import util.RedisUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @program: pdd
 * @description: 直播服务
 * @create: 2020-11-27 12:10
 **/
@DubboService
public class BroadcastServiceImpl implements BroadcastServcie {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisUtil redisUtil;

    @Value("${zb.host}")
    private  String HOST;

    @Override
    public boolean invalid(String shopid) {
        try{
            redisUtil.del(shopid+"::ZB");
            return true;
        }catch (Exception e){
            throw new RuntimeException("无效化token失败");
        }
    }

    @Override
    public String createBroadcastToken(String shopid) {
        String token=(String)redisUtil.get(shopid+"::ZB");
        if(token==null){ //如果不存在token 创建并且返回
            token=TokenUtil.createToken(shopid);
            redisUtil.set(shopid+"::ZB",token);
            return  token;
        }else{// 检查当前是否开启了直播，如果开启了 就返回当前token 否则生成新的token
            if(redisUtil.get("ZBing::"+token)!=null) return token;
            token=TokenUtil.createToken(shopid);
            redisUtil.set(shopid+"::ZB",token);
            return  token;
        }
    }

    @Override
    public boolean startBroadcast(String token, String shopid) {
        String tk=(String)redisUtil.get(shopid+"::ZB");
        if(tk==null||!tk.equals(token)) return  false;
        if(!redisUtil.setnx("ZBing::"+token,shopid+":"+token)) return false;
//        redisUtil.set(token+"::ZBchat",1);
        return true;
    }

    @Override
    public boolean endBroadcast(String token) {
        redisUtil.del("ZBing::"+token);
//        redisUtil.del(token+"::ZBchat");
        return true;
    }

    @Override
    public ZbInfo createBroadcast(ZbInfo info) {
        return mongoTemplate.save(info);
    }

    @Override
    public List<ZbInfo> getAllZbing() {
        List<ZbInfo> list=new LinkedList<>();
        Set<String> set=redisUtil.keys("ZBing::*");
        System.out.println(set);
        if(set==null) return list;
        set.forEach((e)->{
            String value=(String)redisUtil.get(e);
            int flag=value.indexOf(':');
            String shopid=value.substring(0,flag);
            Query query=new Query(Criteria.where("shopid").is(shopid));
            ZbInfo info = mongoTemplate.findOne(query, ZbInfo.class);
            list.add(info);
        });
        return list;
    }

    @Override
    public String getZbUrl(String shopid) {
        //检查是否在直播
        String token=(String)redisUtil.get(shopid+"::ZB");
        if(redisUtil.get("ZBing::"+token)==null) return null;
//        return "rtmp://"+HOST+"/live/"+token;
        return "rtmp://"+HOST+"/live/"+shopid;
    }

    @Override
    public boolean CheckZbing(String token) {
        return redisUtil.get("ZBing::"+token)!=null;
    }

}
