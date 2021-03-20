package com.scau.searchservice.dao;

import com.scau.searchservice.model.GoodsVo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @program: pdd
 * @description: 商品搜索数据库接口
 * @create: 2020-12-14 22:14
 **/
public interface GoodsRepository extends ElasticsearchRepository<GoodsVo, Long> {
}
