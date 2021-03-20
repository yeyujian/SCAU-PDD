## pdd_product_category

商品分类表

| 字段名称      | 字段类型 | 说明                     |
| ------------- | -------- | ------------------------ |
| parent_id     | string   | 父类id                   |
| category_name | string   | 名字                     |
| level         | int      | 分类级别：0->1级；1->2级 |
| icon          | string   | 图标                     |
| description   | string   | 描述                     |



## pdd_product_comment

商品评价表

| 字段名称       | 字段类型 | 说明                              |
| -------------- | -------- | --------------------------------- |
| product_id     | string   | 商品id                            |
| user_nick_name | string   | 用户昵称                          |
| user_id        | string   | 用户id                            |
| images         | array    | 评价图片                          |
| content        | string   | 评价内容                          |
| show_status    | int      | 显示状态 1显示  0不显示   默认为1 |
| create_time    | date     | 创建时间                          |
| order_id       | string   | 订单id                            |



## pdd_product_info

商品信息表

| 字段名称     | 字段类型 | 说明     |
| ------------ | -------- | -------- |
| product_name | string   | 商品名称 |
| shop_id      | string   | 商铺id   |
| create_time  | date     | 创建时间 |
| property     | array    | 商品属性 |
|              |          |          |
|              |          |          |
|              |          |          |

