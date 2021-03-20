package com.scau.searchservice;

//import com.scau.searchservice.dao.GoodsDao;
import com.scau.searchservice.model.GoodsVo;
import model.GoodsInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import service.SearchService;

import javax.annotation.Resource;
import java.util.regex.Pattern;

@SpringBootTest
class SearchServiceApplicationTests {

    @Resource
    private SearchService searchService;
    @Resource
    private MongoTemplate mongoTemplate;

    @Test
    void contextLoads() {

        Pattern pattern=Pattern.compile("^.*"+"露肚脐衣服高腰上衣短款"+".*$", Pattern.CASE_INSENSITIVE);
        Query query=new Query(Criteria.where("name").regex(pattern));
        System.out.println(mongoTemplate.find(query, GoodsInfo.class));
//        goodsVo.setId(78987987L);

    }

}
