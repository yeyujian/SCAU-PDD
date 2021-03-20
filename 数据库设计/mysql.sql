use scau;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `pdd_product_sku`;
CREATE TABLE `pdd_product_sku` (
  `id` varchar(20) NOT NULL AUTO_INCREMENT COMMENT 'id，主键',
  `product_idd` varchar(50) NOT NULL COMMENT '商品编号存储在mongodb',
  `product_name` varchar(50) DEFAULT NULL COMMENT '商品名称',
  `price` decimal(9,2) NOT NULL COMMENT '商城售价',
  `promotion_price` decimal(9,2) DEFAULT NULL COMMENT '拼团售价',
  `stock` int(10) NOT NULL COMMENT '库存',
  `sold` int(10) NOT NULL COMMENT '已售',
  `shop_id` varchar(20) NOT NULL COMMENT '商铺id',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='商品库存表';


DROP TABLE IF EXISTS `pdd_shop_info`;

CREATE TABLE `pdd_shop_info` (
  `id` varchar(20) unsigned NOT NULL AUTO_INCREMENT,
  `shop_name` varchar(50) NOT NULL COMMENT '店铺名称',
  `last_modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='店铺表';


DROP TABLE IF EXISTS `pdd_order`;
CREATE TABLE `pdd_order` (
  `order_id` varchar(50) COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '订单id',
  `payment` decimal(9,2) COLLATE utf8_bin DEFAULT NULL COMMENT '实付金额。精确到2位小数;单位:元。如:200.07，表示:200元7分',
  `payment_type` int(2) DEFAULT NULL COMMENT '支付类型，1、在线支付，2、货到付款',
  `status` int(10) DEFAULT NULL COMMENT '状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭',
  `create_time` datetime DEFAULT NULL COMMENT '订单创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '订单更新时间',
  `payment_time` datetime DEFAULT NULL COMMENT '付款时间',
  `consign_time` datetime DEFAULT NULL COMMENT '发货时间',
  `end_time` datetime DEFAULT NULL COMMENT '交易完成时间',
  `close_time` datetime DEFAULT NULL COMMENT '交易关闭时间',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户id',
  `buyer_message` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '买家留言',
  `buyer_nick` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '买家昵称',
  `buyer_rate` int(2) DEFAULT NULL COMMENT '买家是否已经评价',
  PRIMARY KEY (`order_id`),
  KEY `create_time` (`create_time`),
  KEY `buyer_nick` (`buyer_nick`),
  KEY `status` (`status`),
  KEY `payment_type` (`payment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8 COMMENT='订单表';


DROP TABLE IF EXISTS `pdd_order_product`;
CREATE TABLE `pdd_order_product` (
  `id` varchar(20) COLLATE utf8_bin NOT NULL,
  `product_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '商品id',
  `order_id` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '订单id',
  `num` int(10) DEFAULT NULL COMMENT '商品购买数量',
  `title` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品标题',
  `price` decimal(9,2) DEFAULT NULL COMMENT '商品单价',
  `total_fee` decimal(9,2) DEFAULT NULL COMMENT '商品总金额',
  `pic_path` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片地址',
  PRIMARY KEY (`id`),
  KEY `product_id` (`product_id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8 COMMENT='订单商品关系表';

DROP TABLE IF EXISTS `pdd_order_shipping`;
CREATE TABLE `pdd_order_shipping` (
  `order_id` varchar(20) NOT NULL COMMENT '订单ID',
  `receiver_name` varchar(20) DEFAULT NULL COMMENT '收货人全名',
  `receiver_mobile` varchar(30) DEFAULT NULL COMMENT '移动电话',
  `receiver_state` varchar(10) DEFAULT NULL COMMENT '省份',
  `receiver_city` varchar(10) DEFAULT NULL COMMENT '城市',
  `receiver_district` varchar(20) DEFAULT NULL COMMENT '区/县',
  `receiver_address` varchar(200) DEFAULT NULL COMMENT '收货地址，如：xx路xx号',
  `created` datetime DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='收件人地址表';


DROP TABLE IF EXISTS `pdd_cart`;
CREATE TABLE order_cart(
`cart_id` varchar(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
`user_id` varchar(20) UNSIGNED NOT NULL COMMENT '用户ID',
`product_id` varchar(20) UNSIGNED NOT NULL COMMENT '商品ID',
`product_num` INT NOT NULL COMMENT '加入购物车商品数量',
`price` DECIMAL(9,2) NOT NULL COMMENT '商品总价格',
`add_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入购物车时间',
`modified_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
PRIMARY KEY pk_cartid(`cart_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '购物车表';
/*//////////////////////////////////////////////////////////////*/
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `roleId` varchar(20) NOT NULL AUTO_INCREMENT COMMENT '角色Id',
  `roleName` varchar(100) DEFAULT NULL COMMENT '角色名称',
  `roleDesc` varchar(100) DEFAULT NULL COMMENT '角色描述',
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='用户角色';
insert  into `sys_role`(`roleId`,`roleName`,`roleDesc`,`role`) values (1,'用户','普通用户','user'),(2,'商家','商家可以管理店铺','seller'),(3,'客服','客服拥有资源管理权限','service'),(4,'管理员','管理员拥有所有权限','admin');

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` varchar(20) NOT NULL AUTO_INCREMENT COMMENT '用户Id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机',
  `sex` varchar(6) DEFAULT NULL COMMENT '性别',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `lastLogin` date DEFAULT NULL COMMENT '最后一次登录时间',
  `loginIp` varchar(30) DEFAULT NULL COMMENT '登录ip',
  `imageUrl` varchar(100) DEFAULT NULL COMMENT '头像图片路径',
  `regTime` TIMESTAMP NOT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_u_1` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='用户表';


DROP TABLE IF EXISTS `sys_user_role`;

CREATE TABLE `sys_user_role` (
  `userId` varchar(20) NOT NULL COMMENT '用户Id,联合主键',
  `roleId` varchar(20) NOT NULL COMMENT '角色Id，联合主键',
  PRIMARY KEY (`userId`,`roleId`),
  KEY `ur_fk_2` (`roleId`),
  CONSTRAINT `ur_fk_1` FOREIGN KEY (`userId`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `ur_fk_2` FOREIGN KEY (`roleId`) REFERENCES `sys_role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户与角色关系表';


DROP TABLE IF EXISTS `sys_user_money`;

CREATE TABLE `sys_user_money` (
  `id` varchar(20) NOT NULL COMMENT '钱包id',
  `userId` varchar(20) NOT NULL COMMENT '用户id',
  `money` decimal(9,2) DEFAULT 100.00 COMMENT '用户余额',
  `modified_time` TIMESTAMP NOT NULL COMMENT DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP '更新时间',
    PRIMARY KEY (`id`),
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户钱包';