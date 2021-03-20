package com.scau.groupbuyservice.serviceImpl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.scau.groupbuyservice.mapper.PddProductSkuMapper;
import model.GoodsInfo;
import model.PddOrderProduct;
import model.PddProductSku;
import model.User;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.transaction.annotation.Transactional;
import service.PddGoodsService;
import util.IdWorker;
import util.RedisUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: pdd
 * @description: 商品管理服务
 * @create: 2020-12-07 20:51
 **/
@DubboService
public class PddGoodsServiceImpl implements PddGoodsService {

    private static   Long TIMEOUT_SECOUND = 60000L;


    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PddProductSkuMapper pddProductSkuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisUtil redisUtil;

    private DefaultRedisScript<Boolean> decremeterProductSkuScript;

    private DefaultRedisScript<Boolean> createProductSkuScript;

    private DefaultRedisScript<Boolean> decremeterProductSkuListScript;
    /**
     * 准备好lua脚本
     */
    @PostConstruct
    private void init(){
        decremeterProductSkuScript = new DefaultRedisScript<Boolean>();
        decremeterProductSkuScript.setResultType(Boolean.class);
        decremeterProductSkuScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/decremeterProductSku.lua")));
        createProductSkuScript = new DefaultRedisScript<Boolean>();
        createProductSkuScript.setResultType(Boolean.class);
        createProductSkuScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/createProductSku.lua")));
        decremeterProductSkuListScript = new DefaultRedisScript<Boolean>();
        decremeterProductSkuListScript.setResultType(Boolean.class);
        decremeterProductSkuListScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/decremeterProductSkuList.lua")));
    }
    @Override
    public GoodsInfo addGoods(GoodsInfo goodsInfo) {
       return  mongoTemplate.insert(goodsInfo);
    }

//    @Cacheable(value = "goods", key= "#id")
    @Override
    public GoodsInfo getGoodsById(String id) {
        Query query=new Query(Criteria.where("_id").is(id));
        GoodsInfo goodsInfo= mongoTemplate.findOne(query, GoodsInfo.class);
        System.out.println(goodsInfo);
        return goodsInfo;
    }

    @Override
    public boolean addProductSku(PddProductSku pddProductSku) {
        try{
            GoodsInfo goodsInfo=addGoods(pddProductSku.getGoodsInfo());
            pddProductSku.setGoodsInfo(goodsInfo);
            pddProductSku.setId(idWorker.nextId());
            pddProductSku.setProductId(goodsInfo.getId());
            return pddProductSkuMapper.insertProductSku(pddProductSku)>0;
        }catch (Exception e){
            throw new RuntimeException("加入库存失败");
        }
    }

//    @Cacheable(value = "productSku", key= "#id")
    @Override
    public PddProductSku getProductSkuByProductId(String id) {
//        PddProductSku ps=new PddProductSku();
//        ps.setStock(10);
//        ps.setProductId("456");
//        if(true) return  ps;
        PddProductSku pddProductSku=pddProductSkuMapper.selectPddProductSkuByProductId(id);
        System.out.println(pddProductSku);
        pddProductSku.setGoodsInfo(getGoodsById(pddProductSku.getProductId()));
        return  pddProductSku;
    }

    /**
     * 检查并设置库存
     * @param productId
     * @return
     */
    private boolean checkRedisProductStock(String productId){
        if(!redisUtil.hasKey("product::"+productId)){ //缓存中不存在库存   第一重检验
//            System.out.println("第一重检验");
            while(!redisUtil.setnx("lock-decremeterProductSku-"+productId,String.valueOf(System.currentTimeMillis()),1)){
                Long lockTime = Long.valueOf(((String)redisUtil.get("lock-decremeterProductSku-"+productId)).substring(9));
                if (lockTime!=null && System.currentTimeMillis() > lockTime+TIMEOUT_SECOUND) {
                    redisUtil.del("lock-decremeterProductSku-"+productId);
                }
            }
            if(!redisUtil.hasKey("product::"+productId)){ //双重检验
//                System.out.println("第二重检验");
                PddProductSku pddProductSku=getProductSkuByProductId(productId);
                if(pddProductSku!=null){
                    //使用lua 脚本 减少io 提升性能 保证原子性
                    /**
                     * List设置lua的KEYS
                     */
                    List<String> keyList = new ArrayList();
                    keyList.add("product::"+productId);
                    redisUtil.execInteger(createProductSkuScript,keyList,pddProductSku.getStock());

                }else{
                    return false;
                }
            }
            redisUtil.del("lock-decremeterProductSku-"+productId);
        }
        return true;
    }

    /**
     * 1,检查是否有缓存  2,检查是否足够库存  3，减少库存
     * @param productId 商品id
     * @param num
     * @return
     */
    @Override
    public boolean decremeterProductSku(String productId, int num) {
        if(!checkRedisProductStock(productId)) return false;
        //使用lua 脚本 减少io 提升性能 保证原子性
        /**
         * List设置lua的KEYS
         */
        List<String> keyList = new ArrayList<>();
        keyList.add("product::"+productId);
        return redisUtil.execInteger(decremeterProductSkuScript,keyList,num);
    }

    @Override
    public boolean decremeterProductSkuList(List<PddOrderProduct> products) {
        List<String> keyList = new ArrayList<>();
        Integer[] values=new Integer[products.size()];
        int cur=0;
        //遍历检查
//        values.add(products.size());
        for(PddOrderProduct product:products){
            if(!checkRedisProductStock(product.getProductId())) return false;
            keyList.add("product::"+product.getProductId());
            values[cur++]=product.getNum();
        }
        System.out.println(keyList);
        return redisUtil.execInteger(decremeterProductSkuListScript,keyList,values);
    }

    @Transactional
    @CacheEvict(value = "productSku", key= "#pddProductSku.productId") //删除缓存
    @Override
    public boolean updateProductSkuStock(PddProductSku pddProductSku) {
        try{
            return pddProductSkuMapper.updateStock(pddProductSku) > 0;
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("修改库存失败");
        }
    }

    @Transactional
    @Override
    public boolean updateProductSkuStockByList(List<PddOrderProduct> products){
        for(PddOrderProduct product:products){
            PddProductSku pddProductSku=new PddProductSku();
            pddProductSku.setProductId(product.getProductId());
            pddProductSku.setStock((Integer) redisUtil.get("product::"+product.getProductId()));
            updateProductSkuStock(pddProductSku);
        }
        return true;
    }
}
