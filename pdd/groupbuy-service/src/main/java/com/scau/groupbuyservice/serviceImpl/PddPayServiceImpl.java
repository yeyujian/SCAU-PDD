package com.scau.groupbuyservice.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scau.groupbuyservice.mapper.UserMoneyMapper;
import model.Order;
import model.UserMoney;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import service.PddPayService;
import util.IdWorker;

/**
 * @program: pdd
 * @description: 支付管理服务
 * @create: 2020-12-07 20:52
 **/
@DubboService
public class PddPayServiceImpl implements PddPayService {
    @Autowired
    private UserMoneyMapper userMoneyMapper;
    @Autowired
    private IdWorker idWorker;

    @Override
    public boolean createUserMoney(UserMoney userMoney) {
        userMoney.setId(idWorker.nextId());
        return userMoneyMapper.insert(userMoney)>0;
    }

    @Override
    public UserMoney getMoneyByUserid(String userid) {
        QueryWrapper<UserMoney> su = new QueryWrapper<>();
        su.eq("user_id",userid);
        return userMoneyMapper.selectOne(su);
    }

    @Override
    public boolean updateMoney(UserMoney userMoney) {
        return userMoneyMapper.updateUserMoneyByUserId(userMoney.getUserId(),userMoney.getMoney())>0;
    }

    @Override
    public boolean payByOrder(Order order) {
        return false;
    }
}
