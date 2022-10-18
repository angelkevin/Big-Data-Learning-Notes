--DWD
--日志表

-- 启动日志表
DROP TABLE IF EXISTS dwd_start_log;
CREATE EXTERNAL TABLE dwd_start_log
(
    `area_code`       STRING COMMENT '地区编码',
    `brand`           STRING COMMENT '手机品牌',
    `channel`         STRING COMMENT '渠道',
    `is_new`          STRING COMMENT '是否首次启动',
    `model`           STRING COMMENT '手机型号',
    `mid_id`          STRING COMMENT '设备id',
    `os`              STRING COMMENT '操作系统',
    `user_id`         STRING COMMENT '会员id',
    `version_code`    STRING COMMENT 'app版本号',
    `entry`           STRING COMMENT 'icon手机图标 notice 通知 install 安装后启动',
    `loading_time`    BIGINT COMMENT '启动加载时间',
    `open_ad_id`      STRING COMMENT '广告页ID ',
    `open_ad_ms`      BIGINT COMMENT '广告总共播放时间',
    `open_ad_skip_ms` BIGINT COMMENT '用户跳过广告时点',
    `ts`              BIGINT COMMENT '时间'
) COMMENT '启动日志表'
    PARTITIONED BY (`dt` STRING) -- 按照时间创建分区
    STORED AS PARQUET -- 采用parquet列式存储
    LOCATION '/warehouse/gmall/dwd/dwd_start_log' -- 指定在HDFS上存储位置
    TBLPROPERTIES ('parquet.compression' = 'lzo');-- 采用LZO压缩

insert overwrite table dwd_start_log partition (dt = '2020-06-14')
select get_json_object(line, '$.common.ar'),
       get_json_object(line, '$.common.ba'),
       get_json_object(line, '$.common.ch'),
       get_json_object(line, '$.common.is_new'),
       get_json_object(line, '$.common.md'),
       get_json_object(line, '$.common.mid'),
       get_json_object(line, '$.common.os'),
       get_json_object(line, '$.common.uid'),
       get_json_object(line, '$.common.vc'),
       get_json_object(line, '$.start.entry'),
       get_json_object(line, '$.start.loading_time'),
       get_json_object(line, '$.start.open_ad_id'),
       get_json_object(line, '$.start.open_ad_ms'),
       get_json_object(line, '$.start.open_ad_skip_ms'),
       get_json_object(line, '$.ts')
from ods_log
where dt = '2020-06-14'
  and get_json_object(line, '$.start') is not null;

-- 页面日志表
DROP TABLE IF EXISTS dwd_page_log;
CREATE EXTERNAL TABLE dwd_page_log
(
    `area_code`      STRING COMMENT '地区编码',
    `brand`          STRING COMMENT '手机品牌',
    `channel`        STRING COMMENT '渠道',
    `is_new`         STRING COMMENT '是否首次启动',
    `model`          STRING COMMENT '手机型号',
    `mid_id`         STRING COMMENT '设备id',
    `os`             STRING COMMENT '操作系统',
    `user_id`        STRING COMMENT '会员id',
    `version_code`   STRING COMMENT 'app版本号',
    `during_time`    BIGINT COMMENT '持续时间毫秒',
    `page_item`      STRING COMMENT '目标id ',
    `page_item_type` STRING COMMENT '目标类型',
    `last_page_id`   STRING COMMENT '上页类型',
    `page_id`        STRING COMMENT '页面ID ',
    `source_type`    STRING COMMENT '来源类型',
    `ts`             bigint
) COMMENT '页面日志表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_page_log'
    TBLPROPERTIES ('parquet.compression' = 'lzo');


insert overwrite table dwd_page_log partition (dt = '2020-06-14')
select get_json_object(line, '$.common.ar'),
       get_json_object(line, '$.common.ba'),
       get_json_object(line, '$.common.ch'),
       get_json_object(line, '$.common.is_new'),
       get_json_object(line, '$.common.md'),
       get_json_object(line, '$.common.mid'),
       get_json_object(line, '$.common.os'),
       get_json_object(line, '$.common.uid'),
       get_json_object(line, '$.common.vc'),
       get_json_object(line, '$.page.during_time'),
       get_json_object(line, '$.page.item'),
       get_json_object(line, '$.page.item_type'),
       get_json_object(line, '$.page.last_page_id'),
       get_json_object(line, '$.page.page_id'),
       get_json_object(line, '$.page.source_type'),
       get_json_object(line, '$.ts')
from ods_log
where dt = '2020-06-14'
  and get_json_object(line, '$.page') is not null;


--行为日志表

DROP TABLE IF EXISTS dwd_action_log;
CREATE EXTERNAL TABLE dwd_action_log
(
    `area_code`      STRING COMMENT '地区编码',
    `brand`          STRING COMMENT '手机品牌',
    `channel`        STRING COMMENT '渠道',
    `is_new`         STRING COMMENT '是否首次启动',
    `model`          STRING COMMENT '手机型号',
    `mid_id`         STRING COMMENT '设备id',
    `os`             STRING COMMENT '操作系统',
    `user_id`        STRING COMMENT '会员id',
    `version_code`   STRING COMMENT 'app版本号',
    `during_time`    BIGINT COMMENT '持续时间毫秒',
    `page_item`      STRING COMMENT '目标id ',
    `page_item_type` STRING COMMENT '目标类型',
    `last_page_id`   STRING COMMENT '上页类型',
    `page_id`        STRING COMMENT '页面id ',
    `source_type`    STRING COMMENT '来源类型',
    `action_id`      STRING COMMENT '动作id',
    `item`           STRING COMMENT '目标id ',
    `item_type`      STRING COMMENT '目标类型',
    `ts`             BIGINT COMMENT '时间'
) COMMENT '动作日志表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_action_log'
    TBLPROPERTIES ('parquet.compression' = 'lzo');


-- 自定义udtf
create function explode_json_array
    as 'com.hive.ExplodeJSONArray' using jar 'hdfs://192.168.170.130:9000/user/hive/jars/Hive-1.0-SNAPSHOT.jar';


select *
from ods_log lateral view explode_json_array(get_json_object(line, '$.actions')) tmp as action
where dt = '2020-06-14'
  and get_json_object(line, '$.actions') is not null;



insert overwrite table dwd_action_log partition (dt = '2020-06-14')
select get_json_object(line, '$.common.ar'),
       get_json_object(line, '$.common.ba'),
       get_json_object(line, '$.common.ch'),
       get_json_object(line, '$.common.is_new'),
       get_json_object(line, '$.common.md'),
       get_json_object(line, '$.common.mid'),
       get_json_object(line, '$.common.os'),
       get_json_object(line, '$.common.uid'),
       get_json_object(line, '$.common.vc'),
       get_json_object(line, '$.page.during_time'),
       get_json_object(line, '$.page.item'),
       get_json_object(line, '$.page.item_type'),
       get_json_object(line, '$.page.last_page_id'),
       get_json_object(line, '$.page.page_id'),
       get_json_object(line, '$.page.source_type'),
       get_json_object(action, '$.action_id'),
       get_json_object(action, '$.item'),
       get_json_object(action, '$.item_type'),
       get_json_object(action, '$.ts')
from ods_log lateral view explode_json_array(get_json_object(line, '$.actions')) tmp as action
where dt = '2020-06-14'
  and get_json_object(line, '$.actions') is not null;

-- 曝光日志表
DROP TABLE IF EXISTS dwd_display_log;
CREATE EXTERNAL TABLE dwd_display_log
(
    `area_code`      STRING COMMENT '地区编码',
    `brand`          STRING COMMENT '手机品牌',
    `channel`        STRING COMMENT '渠道',
    `is_new`         STRING COMMENT '是否首次启动',
    `model`          STRING COMMENT '手机型号',
    `mid_id`         STRING COMMENT '设备id',
    `os`             STRING COMMENT '操作系统',
    `user_id`        STRING COMMENT '会员id',
    `version_code`   STRING COMMENT 'app版本号',
    `during_time`    BIGINT COMMENT 'app版本号',
    `page_item`      STRING COMMENT '目标id ',
    `page_item_type` STRING COMMENT '目标类型',
    `last_page_id`   STRING COMMENT '上页类型',
    `page_id`        STRING COMMENT '页面ID ',
    `source_type`    STRING COMMENT '来源类型',
    `ts`             BIGINT COMMENT 'app版本号',
    `display_type`   STRING COMMENT '曝光类型',
    `item`           STRING COMMENT '曝光对象id ',
    `item_type`      STRING COMMENT 'app版本号',
    `order`          BIGINT COMMENT '曝光顺序',
    `pos_id`         BIGINT COMMENT '曝光位置'
) COMMENT '曝光日志表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_display_log'
    TBLPROPERTIES ('parquet.compression' = 'lzo');

insert overwrite table dwd_display_log partition (dt = '2020-06-14')
select get_json_object(line, '$.common.ar'),
       get_json_object(line, '$.common.ba'),
       get_json_object(line, '$.common.ch'),
       get_json_object(line, '$.common.is_new'),
       get_json_object(line, '$.common.md'),
       get_json_object(line, '$.common.mid'),
       get_json_object(line, '$.common.os'),
       get_json_object(line, '$.common.uid'),
       get_json_object(line, '$.common.vc'),
       get_json_object(line, '$.page.during_time'),
       get_json_object(line, '$.page.item'),
       get_json_object(line, '$.page.item_type'),
       get_json_object(line, '$.page.last_page_id'),
       get_json_object(line, '$.page.page_id'),
       get_json_object(line, '$.page.source_type'),
       get_json_object(line, '$.ts'),
       get_json_object(display, '$.display_type'),
       get_json_object(display, '$.item'),
       get_json_object(display, '$.item_type'),
       get_json_object(display, '$.order'),
       get_json_object(display, '$.pos_id')
from ods_log lateral view explode_json_array(get_json_object(line, '$.displays')) tmp as display
where dt = '2020-06-14'
  and get_json_object(line, '$.displays') is not null;

-- 错误日志表
DROP TABLE IF EXISTS dwd_error_log;
CREATE EXTERNAL TABLE dwd_error_log
(
    `area_code`       STRING COMMENT '地区编码',
    `brand`           STRING COMMENT '手机品牌',
    `channel`         STRING COMMENT '渠道',
    `is_new`          STRING COMMENT '是否首次启动',
    `model`           STRING COMMENT '手机型号',
    `mid_id`          STRING COMMENT '设备id',
    `os`              STRING COMMENT '操作系统',
    `user_id`         STRING COMMENT '会员id',
    `version_code`    STRING COMMENT 'app版本号',
    `page_item`       STRING COMMENT '目标id ',
    `page_item_type`  STRING COMMENT '目标类型',
    `last_page_id`    STRING COMMENT '上页类型',
    `page_id`         STRING COMMENT '页面ID ',
    `source_type`     STRING COMMENT '来源类型',
    `entry`           STRING COMMENT ' icon手机图标  notice 通知 install 安装后启动',
    `loading_time`    STRING COMMENT '启动加载时间',
    `open_ad_id`      STRING COMMENT '广告页ID ',
    `open_ad_ms`      STRING COMMENT '广告总共播放时间',
    `open_ad_skip_ms` STRING COMMENT '用户跳过广告时点',
    `actions`         STRING COMMENT '动作',
    `displays`        STRING COMMENT '曝光',
    `ts`              STRING COMMENT '时间',
    `error_code`      STRING COMMENT '错误码',
    `msg`             STRING COMMENT '错误信息'
) COMMENT '错误日志表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_error_log'
    TBLPROPERTIES ('parquet.compression' = 'lzo');

insert overwrite table dwd_error_log partition (dt = '2020-06-14')
select get_json_object(line, '$.common.ar'),
       get_json_object(line, '$.common.ba'),
       get_json_object(line, '$.common.ch'),
       get_json_object(line, '$.common.is_new'),
       get_json_object(line, '$.common.md'),
       get_json_object(line, '$.common.mid'),
       get_json_object(line, '$.common.os'),
       get_json_object(line, '$.common.uid'),
       get_json_object(line, '$.common.vc'),
       get_json_object(line, '$.page.item'),
       get_json_object(line, '$.page.item_type'),
       get_json_object(line, '$.page.last_page_id'),
       get_json_object(line, '$.page.page_id'),
       get_json_object(line, '$.page.source_type'),
       get_json_object(line, '$.start.entry'),
       get_json_object(line, '$.start.loading_time'),
       get_json_object(line, '$.start.open_ad_id'),
       get_json_object(line, '$.start.open_ad_ms'),
       get_json_object(line, '$.start.open_ad_skip_ms'),
       get_json_object(line, '$.actions'),
       get_json_object(line, '$.displays'),
       get_json_object(line, '$.ts'),
       get_json_object(line, '$.err.error_code'),
       get_json_object(line, '$.err.msg')
from ods_log
where dt = '2020-06-14'
  and get_json_object(line, '$.err') is not null;


-- 评价事实表
DROP TABLE IF EXISTS dwd_comment_info;
CREATE EXTERNAL TABLE dwd_comment_info
(
    `id`          STRING COMMENT '编号',
    `user_id`     STRING COMMENT '用户ID',
    `sku_id`      STRING COMMENT '商品sku',
    `spu_id`      STRING COMMENT '商品spu',
    `order_id`    STRING COMMENT '订单ID',
    `appraise`    STRING COMMENT '评价(好评、中评、差评、默认评价)',
    `create_time` STRING COMMENT '评价时间'
) COMMENT '评价事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_comment_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");


set hive.exec.dynamic.partition.mode=nonstrict;
set hive.input.format=org.apache.hadoop.hive.ql.io.HiveInputFormat;



insert overwrite table dwd_comment_info partition (dt)
select id,
       user_id,
       sku_id,
       spu_id,
       order_id,
       appraise,
       create_time,
       date_format(create_time, 'yyyy-MM-dd')
from ods_comment_info
where dt = '2020-06-14';

show partitions dwd_comment_info;


insert overwrite table dwd_comment_info partition (dt)
select id,
       user_id,
       sku_id,
       spu_id,
       order_id,
       appraise,
       create_time,
       date_format(create_time, 'yyyy-MM-dd')
from ods_comment_info
where dt = '2020-06-14';

-- 订单明细表
DROP TABLE IF EXISTS dwd_order_detail;
CREATE EXTERNAL TABLE dwd_order_detail
(
    `id`                    STRING COMMENT '订单编号',
    `order_id`              STRING COMMENT '订单号',
    `user_id`               STRING COMMENT '用户id',
    `sku_id`                STRING COMMENT 'sku商品id',
    `province_id`           STRING COMMENT '省份ID',
    `activity_id`           STRING COMMENT '活动ID',
    `activity_rule_id`      STRING COMMENT '活动规则ID',
    `coupon_id`             STRING COMMENT '优惠券ID',
    `create_time`           STRING COMMENT '创建时间',
    `source_type`           STRING COMMENT '来源类型',
    `source_id`             STRING COMMENT '来源编号',
    `sku_num`               BIGINT COMMENT '商品数量',
    `original_amount`       DECIMAL(16, 2) COMMENT '原始价格',
    `split_activity_amount` DECIMAL(16, 2) COMMENT '活动优惠分摊',
    `split_coupon_amount`   DECIMAL(16, 2) COMMENT '优惠券优惠分摊',
    `split_final_amount`    DECIMAL(16, 2) COMMENT '最终价格分摊'
) COMMENT '订单明细事实表表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_order_detail/'
    TBLPROPERTIES ("parquet.compression" = "lzo");


insert overwrite table dwd_order_detail partition (dt)
select od.id,
       od.order_id,
       oi.user_id,
       od.sku_id,
       oi.province_id,
       oda.activity_id,
       oda.activity_rule_id,
       odc.coupon_id,
       od.create_time,
       od.source_type,
       od.source_id,
       od.sku_num,
       od.order_price * od.sku_num,
       od.split_activity_amount,
       od.split_coupon_amount,
       od.split_final_amount,
       date_format(create_time, 'yyyy-MM-dd')
from (select *
      from ods_order_detail
      where dt = '2020-06-14') od
         left join
     (select id,
             user_id,
             province_id
      from ods_order_info
      where dt = '2020-06-14') oi
     on od.order_id = oi.id
         left join
     (select order_detail_id,
             activity_id,
             activity_rule_id
      from ods_order_detail_activity
      where dt = '2020-06-14') oda
     on od.id = oda.order_detail_id
         left join
     (select order_detail_id,
             coupon_id
      from ods_order_detail_coupon
      where dt = '2020-06-14') odc
     on od.id = odc.order_detail_id;


DROP TABLE IF EXISTS dwd_order_refund_info;
CREATE EXTERNAL TABLE dwd_order_refund_info
(
    `id`                 STRING COMMENT '编号',
    `user_id`            STRING COMMENT '用户ID',
    `order_id`           STRING COMMENT '订单ID',
    `sku_id`             STRING COMMENT '商品ID',
    `province_id`        STRING COMMENT '地区ID',
    `refund_type`        STRING COMMENT '退单类型',
    `refund_num`         BIGINT COMMENT '退单件数',
    `refund_amount`      DECIMAL(16, 2) COMMENT '退单金额',
    `refund_reason_type` STRING COMMENT '退单原因类型',
    `create_time`        STRING COMMENT '退单时间'
) COMMENT '退单事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_order_refund_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");


insert overwrite table dwd_order_refund_info partition (dt)
select ri.id,
       ri.user_id,
       ri.order_id,
       ri.sku_id,
       oi.province_id,
       ri.refund_type,
       ri.refund_num,
       ri.refund_amount,
       ri.refund_reason_type,
       ri.create_time,
       date_format(ri.create_time, 'yyyy-MM-dd')
from (select *
      from ods_order_refund_info
      where dt = '2020-06-14') ri
         left join
     (select id, province_id
      from ods_order_info
      where dt = '2020-06-14') oi
     on ri.order_id = oi.id;


DROP TABLE IF EXISTS dwd_cart_info;
CREATE EXTERNAL TABLE dwd_cart_info
(
    `id`           STRING COMMENT '编号',
    `user_id`      STRING COMMENT '用户ID',
    `sku_id`       STRING COMMENT '商品ID',
    `source_type`  STRING COMMENT '来源类型',
    `source_id`    STRING COMMENT '来源编号',
    `cart_price`   DECIMAL(16, 2) COMMENT '加入购物车时的价格',
    `is_ordered`   STRING COMMENT '是否已下单',
    `create_time`  STRING COMMENT '创建时间',
    `operate_time` STRING COMMENT '修改时间',
    `order_time`   STRING COMMENT '下单时间',
    `sku_num`      BIGINT COMMENT '加购数量'
) COMMENT '加购事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_cart_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_cart_info partition (dt = '2020-06-14')
select id,
       user_id,
       sku_id,
       source_type,
       source_id,
       cart_price,
       is_ordered,
       create_time,
       operate_time,
       order_time,
       sku_num
from ods_cart_info
where dt = '2020-06-14';
DROP TABLE IF EXISTS dwd_favor_info;
CREATE EXTERNAL TABLE dwd_favor_info
(
    `id`          STRING COMMENT '编号',
    `user_id`     STRING COMMENT '用户id',
    `sku_id`      STRING COMMENT 'skuid',
    `spu_id`      STRING COMMENT 'spuid',
    `is_cancel`   STRING COMMENT '是否取消',
    `create_time` STRING COMMENT '收藏时间',
    `cancel_time` STRING COMMENT '取消时间'
) COMMENT '收藏事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_favor_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_favor_info partition (dt = '2020-06-14')
select id,
       user_id,
       sku_id,
       spu_id,
       is_cancel,
       create_time,
       cancel_time
from ods_favor_info
where dt = '2020-06-14';
DROP TABLE IF EXISTS dwd_coupon_use;
CREATE EXTERNAL TABLE dwd_coupon_use
(
    `id`            STRING COMMENT '编号',
    `coupon_id`     STRING COMMENT '优惠券ID',
    `user_id`       STRING COMMENT 'userid',
    `order_id`      STRING COMMENT '订单id',
    `coupon_status` STRING COMMENT '优惠券状态',
    `get_time`      STRING COMMENT '领取时间',
    `using_time`    STRING COMMENT '使用时间(下单)',
    `used_time`     STRING COMMENT '使用时间(支付)',
    `expire_time`   STRING COMMENT '过期时间'
) COMMENT '优惠券领用事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_coupon_use/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_coupon_use partition (dt)
select id,
       coupon_id,
       user_id,
       order_id,
       coupon_status,
       get_time,
       using_time,
       used_time,
       expire_time,
       coalesce(date_format(used_time, 'yyyy-MM-dd'), date_format(expire_time, 'yyyy-MM-dd'), '9999-99-99')
from ods_coupon_use
where dt = '2020-06-14';
DROP TABLE IF EXISTS dwd_coupon_use;
CREATE EXTERNAL TABLE dwd_coupon_use
(
    `id`            STRING COMMENT '编号',
    `coupon_id`     STRING COMMENT '优惠券ID',
    `user_id`       STRING COMMENT 'userid',
    `order_id`      STRING COMMENT '订单id',
    `coupon_status` STRING COMMENT '优惠券状态',
    `get_time`      STRING COMMENT '领取时间',
    `using_time`    STRING COMMENT '使用时间(下单)',
    `used_time`     STRING COMMENT '使用时间(支付)',
    `expire_time`   STRING COMMENT '过期时间'
) COMMENT '优惠券领用事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_coupon_use/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_coupon_use partition (dt)
select id,
       coupon_id,
       user_id,
       order_id,
       coupon_status,
       get_time,
       using_time,
       used_time,
       expire_time,
       coalesce(date_format(used_time, 'yyyy-MM-dd'), date_format(expire_time, 'yyyy-MM-dd'), '9999-99-99')
from ods_coupon_use
where dt = '2020-06-14';
DROP TABLE IF EXISTS dwd_payment_info;
CREATE EXTERNAL TABLE dwd_payment_info
(
    `id`             STRING COMMENT '编号',
    `order_id`       STRING COMMENT '订单编号',
    `user_id`        STRING COMMENT '用户编号',
    `province_id`    STRING COMMENT '地区ID',
    `trade_no`       STRING COMMENT '交易编号',
    `out_trade_no`   STRING COMMENT '对外交易编号',
    `payment_type`   STRING COMMENT '支付类型',
    `payment_amount` DECIMAL(16, 2) COMMENT '支付金额',
    `payment_status` STRING COMMENT '支付状态',
    `create_time`    STRING COMMENT '创建时间',--调用第三方支付接口的时间
    `callback_time`  STRING COMMENT '完成时间'--支付完成时间，即支付成功回调时间
) COMMENT '支付事实表表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_payment_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_payment_info partition (dt)
select pi.id,
       pi.order_id,
       pi.user_id,
       oi.province_id,
       pi.trade_no,
       pi.out_trade_no,
       pi.payment_type,
       pi.payment_amount,
       pi.payment_status,
       pi.create_time,
       pi.callback_time,
       nvl(date_format(pi.callback_time, 'yyyy-MM-dd'), '9999-99-99')
from (select *
      from ods_payment_info
      where dt = '2020-06-14') pi
         left join
     (select id, province_id
      from ods_order_info
      where dt = '2020-06-14') oi
     on pi.order_id = oi.id;
DROP TABLE IF EXISTS dwd_refund_payment;
CREATE EXTERNAL TABLE dwd_refund_payment
(
    `id`            STRING COMMENT '编号',
    `user_id`       STRING COMMENT '用户ID',
    `order_id`      STRING COMMENT '订单编号',
    `sku_id`        STRING COMMENT 'SKU编号',
    `province_id`   STRING COMMENT '地区ID',
    `trade_no`      STRING COMMENT '交易编号',
    `out_trade_no`  STRING COMMENT '对外交易编号',
    `payment_type`  STRING COMMENT '支付类型',
    `refund_amount` DECIMAL(16, 2) COMMENT '退款金额',
    `refund_status` STRING COMMENT '退款状态',
    `create_time`   STRING COMMENT '创建时间',--调用第三方支付接口的时间
    `callback_time` STRING COMMENT '回调时间'--支付接口回调时间，即支付成功时间
) COMMENT '退款事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_refund_payment/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_refund_payment partition (dt)
select rp.id,
       user_id,
       order_id,
       sku_id,
       province_id,
       trade_no,
       out_trade_no,
       payment_type,
       refund_amount,
       refund_status,
       create_time,
       callback_time,
       nvl(date_format(callback_time, 'yyyy-MM-dd'), '9999-99-99')
from (select id,
             out_trade_no,
             order_id,
             sku_id,
             payment_type,
             trade_no,
             refund_amount,
             refund_status,
             create_time,
             callback_time
      from ods_refund_payment
      where dt = '2020-06-14') rp
         left join
     (select id,
             user_id,
             province_id
      from ods_order_info
      where dt = '2020-06-14') oi
     on rp.order_id = oi.id;
CREATE EXTERNAL TABLE dwd_order_info
(
    `id`                     STRING COMMENT '编号',
    `order_status`           STRING COMMENT '订单状态',
    `user_id`                STRING COMMENT '用户ID',
    `province_id`            STRING COMMENT '地区ID',
    `payment_way`            STRING COMMENT '支付方式',
    `delivery_address`       STRING COMMENT '邮寄地址',
    `out_trade_no`           STRING COMMENT '对外交易编号',
    `tracking_no`            STRING COMMENT '物流单号',
    `create_time`            STRING COMMENT '创建时间(未支付状态)',
    `payment_time`           STRING COMMENT '支付时间(已支付状态)',
    `cancel_time`            STRING COMMENT '取消时间(已取消状态)',
    `finish_time`            STRING COMMENT '完成时间(已完成状态)',
    `refund_time`            STRING COMMENT '退款时间(退款中状态)',
    `refund_finish_time`     STRING COMMENT '退款完成时间(退款完成状态)',
    `expire_time`            STRING COMMENT '过期时间',
    `feight_fee`             DECIMAL(16, 2) COMMENT '运费',
    `feight_fee_reduce`      DECIMAL(16, 2) COMMENT '运费减免',
    `activity_reduce_amount` DECIMAL(16, 2) COMMENT '活动减免',
    `coupon_reduce_amount`   DECIMAL(16, 2) COMMENT '优惠券减免',
    `original_amount`        DECIMAL(16, 2) COMMENT '订单原始价格',
    `final_amount`           DECIMAL(16, 2) COMMENT '订单最终价格'
) COMMENT '订单事实表'
    PARTITIONED BY (`dt` STRING)
    STORED AS PARQUET
    LOCATION '/warehouse/gmall/dwd/dwd_order_info/'
    TBLPROPERTIES ("parquet.compression" = "lzo");
insert overwrite table dwd_order_info partition (dt)
select oi.id,
       oi.order_status,
       oi.user_id,
       oi.province_id,
       oi.payment_way,
       oi.delivery_address,
       oi.out_trade_no,
       oi.tracking_no,
       oi.create_time,
       times.ts['1002'] payment_time,
       times.ts['1003'] cancel_time,
       times.ts['1004'] finish_time,
       times.ts['1005'] refund_time,
       times.ts['1006'] refund_finish_time,
       oi.expire_time,
       feight_fee,
       feight_fee_reduce,
       activity_reduce_amount,
       coupon_reduce_amount,
       original_amount,
       final_amount,
       case
           when times.ts['1003'] is not null then date_format(times.ts['1003'], 'yyyy-MM-dd')
           when times.ts['1004'] is not null and
                date_add(date_format(times.ts['1004'], 'yyyy-MM-dd'), 7) <= '2020-06-14' and times.ts['1005'] is null
               then date_add(date_format(times.ts['1004'], 'yyyy-MM-dd'), 7)
           when times.ts['1006'] is not null then date_format(times.ts['1006'], 'yyyy-MM-dd')
           when oi.expire_time is not null then date_format(oi.expire_time, 'yyyy-MM-dd')
           else '9999-99-99'
           end
from (select *
      from ods_order_info
      where dt = '2020-06-14') oi
         left join
     (select order_id,
             str_to_map(concat_ws(',', collect_set(concat(order_status, '=', operate_time))), ',', '=') ts
      from ods_order_status_log
      where dt = '2020-06-14'
      group by order_id) times
     on oi.id = times.order_id;
