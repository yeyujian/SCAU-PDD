package com.scau.searchservice;

import com.scau.searchservice.model.GoodsVo;
import model.GoodsInfo;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import service.SearchService;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
@EnableDubbo
//@RestController
public class SearchServiceApplication {

    @Resource
    private SearchService searchService;

//    @GetMapping("/{p}")
//    public List<GoodsInfo> getSome(@PathVariable("p") String param){
//        GoodsVo goodsVo=new GoodsVo();
//        goodsVo.setName("hello world 黑");
////        goodsVo.setId(2222222L);
//        searchService.insertGoods(goodsVo);
//        return searchService.searchGoods(param);
//    }

    public static void main(String[] args) {
//        System.setProperty("es.set.netty.runtime.available.processors","false");
        SpringApplication.run(SearchServiceApplication.class, args);
    }

}
