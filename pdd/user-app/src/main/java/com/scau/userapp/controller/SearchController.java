package com.scau.userapp.controller;

import entity.ResultEntity;
import model.GoodsInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import service.SearchService;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @program: pdd
 * @description: 搜索控制层
 * @create: 2020-12-17 20:53
 **/
@RestController
public class SearchController {

    @DubboReference
    private SearchService searchService;

    @GetMapping("/search/goods")
    public ResultEntity searchGoods(@Validated @NotBlank String key){
        List<GoodsInfo> list=searchService.searchGoods(key);
        return new ResultEntity.Builder().data(list).messge(key).build();
    }
}
