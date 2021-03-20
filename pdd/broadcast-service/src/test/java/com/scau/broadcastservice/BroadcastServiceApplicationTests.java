package com.scau.broadcastservice;

import com.scau.broadcastservice.model.ZbInfoModel;
import model.ZbInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import service.BroadcastServcie;

import javax.annotation.Resource;

@SpringBootTest
class BroadcastServiceApplicationTests {

    @Resource
    private BroadcastServcie broadcastServcie;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    void contextLoads() {
        ZbInfo zbInfo=new ZbInfo();
        zbInfo.setShopid("hello");
        zbInfo.setImage("dfsafads");
        zbInfo.setDes("我爱你");
//        zbInfo.setId("1");
        broadcastServcie.createBroadcast(zbInfo);
        Query query=new Query(Criteria.where("shopid").is("hello"));
        ZbInfo info = mongoTemplate.findOne(query, ZbInfo.class);
        System.out.println(info);
//        zbInfo.setProductid("123456");
//        mongoTemplate.save(zbInfo);
    }

}
