package com.scau.userapp.controller;

import annotation.JwtCheck;
import com.scau.userapp.util.JwtUtil;
import entity.ResultEntity;
import model.User;
import model.ZbInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import service.BroadcastServcie;
import service.JwtService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: pdd
 * @description: 直播控制层
 * @create: 2020-12-16 16:44
 **/
@RestController
@RequestMapping("/zb")
public class ZbController {

    @DubboReference
    private BroadcastServcie broadcastServcie;

    @DubboReference
    private JwtService jwtService;



    //此端口只对内网开放
    @GetMapping("/end")
    public ResultEntity endBroadCast(String token){
        if(broadcastServcie.endBroadcast(token)) return ResultEntity.successResult();
        else{
            return ResultEntity.failedResult();
        }
    }

    @PostMapping("")
    @JwtCheck(role = "seller")
    public ResultEntity createBroadCast(String image,String des,HttpServletRequest request){
        String jwt= JwtUtil.GetJwtFromCookies(request);
        User user=jwtService.getUserFromJwt(jwt);
        ZbInfo zbInfo=new ZbInfo();
        zbInfo.setDes(des);
        zbInfo.setImage(image);
        zbInfo.setShopid(user.getId());
        System.out.println(user);
        zbInfo=broadcastServcie.createBroadcast(zbInfo);
        return new ResultEntity.Builder().data(zbInfo).build();
    }

    @GetMapping("/token")
    @JwtCheck(role = "seller")
    public ResultEntity creatToken(HttpServletRequest request){
        String jwt= JwtUtil.GetJwtFromCookies(request);
        User user=jwtService.getUserFromJwt(jwt);
        System.out.println(user);
        if(user.getId()==null) ResultEntity.failedResult();
        String token=broadcastServcie.createBroadcastToken(user.getId());
        return new ResultEntity.Builder().data(token).build();
    }

    //此端口只对内网开放
    @GetMapping("/auth")
    public ResultEntity startBroadCast(String name, String token, HttpServletResponse response){
        System.out.println(name);
        System.out.println(token);
        if(broadcastServcie.startBroadcast(token,name)) return ResultEntity.successResult();
        else{
            response.setStatus(500);
            return ResultEntity.failedResult();
        }
    }

    public ResultEntity deleteBroadCast(){
        return null;
    }


    @GetMapping("/all")
    public ResultEntity getAllZb(){
        List<ZbInfo> zblist=broadcastServcie.getAllZbing();
        return new ResultEntity.Builder().data(zblist).build();
    }

    /**
     * shopid:1339528058163564544
     * token:6zQ94TIK9PW4rYwKkJK2aQ==
     * url: rtmp://127.0.0.1:1935/live/1339528058163564544
     * @param shopid
     * @return
     */
    @GetMapping("/url/{shopid}")
    public ResultEntity getZbUrl(@PathVariable("shopid") String shopid){
        String url=broadcastServcie.getZbUrl(shopid);
        return new ResultEntity.Builder().data(url).build();
    }
}
