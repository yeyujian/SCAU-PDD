package com.scau.searchservice.model;

import model.GoodsInfo;
import model.GoodsProperty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @program: pdd
 * @description: 商品试图
 * @create: 2020-12-14 21:57
 **/
@Document(indexName = "goodsindex", type = "goods")
public class GoodsVo   {

    public static GoodsInfo goodsVoToGoodsInfo(GoodsVo goodsVo){
        GoodsInfo goodsInfo=new GoodsInfo();
        goodsInfo.setId(goodsVo.getId());
        goodsInfo.setDescription(goodsVo.getDescription());
        goodsInfo.setImages(goodsVo.getImages());
        goodsInfo.setProperties(goodsVo.getProperties());
        goodsInfo.setName(goodsVo.getName());
        return goodsInfo;
    }

    public static GoodsVo goodsInfoToGoodsVo(GoodsInfo goodsVo){
        GoodsVo goodsInfo=new GoodsVo();
        goodsInfo.setId(goodsVo.getId());
        goodsInfo.setDescription(goodsVo.getDescription());
        goodsInfo.setImages(goodsVo.getImages());
        goodsInfo.setProperties(goodsVo.getProperties());
        goodsInfo.setName(goodsVo.getName());
        return goodsInfo;
    }

    @Id
    private String  id;

    private String name;

    private String properties;

    private String images;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String  getId() {
        return id;
    }

    public void setId(String  id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "GoodsInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", properties=" + properties +
                ", iamges=" + images +
                ", description='" + description + '\'' +
                '}';
    }

}
