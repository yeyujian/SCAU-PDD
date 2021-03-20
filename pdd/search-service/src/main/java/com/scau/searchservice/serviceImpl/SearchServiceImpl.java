package com.scau.searchservice.serviceImpl;

//import com.scau.searchservice.dao.GoodsDao;
import com.scau.searchservice.dao.GoodsRepository;
import com.scau.searchservice.model.GoodsVo;
import model.GoodsInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import service.SearchService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @program: pdd
 * @description: 搜索服务
 * @create: 2020-12-14 22:14
 **/
@DubboService
public class SearchServiceImpl implements SearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GoodsRepository goodsDao;

    private List<GoodsInfo> searchGoodsFromMongoDB(String key){
        Pattern pattern=Pattern.compile("^.*"+key+".*$", Pattern.CASE_INSENSITIVE);
        Query query=new Query(Criteria.where("name").regex(pattern));
        return mongoTemplate.find(query,GoodsInfo.class);
    }

    @Override
    public boolean insertGoods(GoodsInfo goodsInfo) {

        try{
            goodsDao.save(GoodsVo.goodsInfoToGoodsVo(goodsInfo));
            return true;
        }catch(Exception e){
            throw  new RuntimeException("插入数据到elasticsearch失败");
        }
    }

    @Override
    public List<GoodsInfo> searchGoods(String searchContent) {

        // 1.创建查询对象, 查询所有数据
        QueryStringQueryBuilder builder = new QueryStringQueryBuilder(searchContent);

        List<GoodsInfo> list=new ArrayList<>();
        // 2、判断查询参数
        if (!StringUtils.isEmpty(searchContent)) {
            Iterable<GoodsVo> searchResult = goodsDao.search(builder);
            Iterator<GoodsVo> iterator = searchResult.iterator();
            if(iterator.hasNext()){
                while (iterator.hasNext()) {
                    list.add(GoodsVo.goodsVoToGoodsInfo(iterator.next()));
                }
            }else{
                List<GoodsInfo> goodsInfoList=searchGoodsFromMongoDB(searchContent);
                for(GoodsInfo goodsInfo:goodsInfoList){
                    insertGoods(goodsInfo);
                }
                return goodsInfoList;
            }
        }
        return list;
    }
}
