-- ----------------------------
-- 二手化妆品/护肤品预约管理系统 业务表
-- 请在 relily 唯一库(relily-vue 等)上执行, 依赖 sys_user/sys_dict/menu 等芮丽预约基础表
-- ----------------------------

-- 商品表: 卖家的商品
drop table if exists cosmetics_product;
create table cosmetics_product (
  id              bigint(20)    not null auto_increment  comment '商品ID',
  version         int           not null default 0       comment '乐观锁版本号',
  goods_name      varchar(100)  not null                 comment '商品名称',
  category        varchar(50)             comment '分类(洁面/面霜/乳液/口红/香水...)',
  brand           varchar(50)             comment '品牌',
  price           decimal(10,2) not null                 comment '单价(元)',
  stock           int           not null default 0       comment '余量/库存',
  condition_level char(1)       default '0'               comment '成色(字典:cosm_condition_level)',
  image           varchar(255)             comment '主图地址',
  description     text                               comment '说明详情/规格/使用情况',
  notice          text                               comment '注意事项(过敏提醒/过期提醒等)',
  status          char(1)       default '0'               comment '状态(0上架 1下架)',
  seller_id       bigint(20)             comment '卖家用户ID',
  seller_name     varchar(30)             comment '卖家用户名',
  del_flag        char(1)       default '0'               comment '删除标志(0存在 1删除)',
  create_by       varchar(64)          comment '创建者',
  create_time     datetime              comment '创建时间',
  update_by       varchar(64)         comment '更新者',
  update_time     datetime            comment '更新时间',
  remark          varchar(500)          comment '备注',
  primary key (id)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='二手化妆品商品表';

-- 预约单表
create table cosmetics_appointment (
  id              bigint(20)    not null auto_increment  comment '预约ID',
  order_no        varchar(32)   not null                 comment '预约单号(唯一)',
  user_id         bigint(20)    not null                 comment '预约用户ID(买家)',
  user_name       varchar(30)             comment '预约用户名(买家)',
  goods_id        bigint(20)    not null                 comment '预约的商品ID',
  goods_name      varchar(100)            comment '商品名称(冗余快照)',
  seller_id       bigint(20)             comment '卖家用户ID(冗余,用于卖家视角查询)',
  unit_price      decimal(10,2)           comment '预约单价(冗余快照)',
  quantity        int           not null default 1       comment '预约数量',
  total_amount    decimal(10,2)           comment '总价(仅预约显示,不支付)',
  appoint_time    datetime              comment '预约到店/自取时间',
  expire_time     datetime              comment '逾期自动取消的截止时间',
  contact_phone   varchar(20)             comment '联系电话',
  receiver_name   varchar(30)             comment '收货人',
  receiver_phone  varchar(20)             comment '收货联系方式',
  receiver_addr   varchar(255)            comment '收货地址(线下发快递用)',
  status          char(1)       default '0'               comment '状态(0待确认 1已确认 2已取消 3已完成 4已发货)',
  cancel_reason   varchar(200)            comment '取消原因(含超时自动取消的标记)',
  cancel_time     datetime              comment '取消时间',
  finish_time     datetime              comment '完成时间(签收自动完结/手动完结)',
  express_company varchar(30)             comment '快递公司(如顺丰/SF)',
  express_no      varchar(50)             comment '快递单号(商家发货后填写)',
  ship_time       datetime              comment '发货时间',
  del_flag        char(1)       default '0'               comment '删除标志(0存在 1删除)',
  create_by       varchar(64)             comment '创建者',
  create_time     datetime              comment '创建时间',
  update_by       varchar(64)             comment '更新者',
  update_time     datetime              comment '更新时间',
  remark          varchar(500)          comment '备注',
  primary key (id),
  unique key uk_order_no (order_no),
  key idx_user_status_time (user_id, status, create_time),
  key idx_status_expire (status, expire_time),
  key idx_seller_goods (seller_id, goods_id)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='预约单表';

-- 物流轨迹表: 实时查询结果的快照(快递100/快递鸟节点)
create table cosmetics_express_track (
  id              bigint(20)    not null auto_increment  comment '轨迹ID',
  appointment_id  bigint(20)    not null                 comment '预约单ID',
  order_no        varchar(32)             comment '预约单号',
  express_company varchar(30)             comment '快递公司',
  express_no      varchar(50)             comment '快递单号',
  track_status    varchar(20)             comment '物流状态(0收件/揽收 1在途中 2派送中 3已签收)',
  is_signed       char(1)       default '0'               comment '是否已签收(0否 1是)',
  track_info      text                             comment '轨迹详情(JSON: [{time,context,status}...])',
  last_query_time datetime              comment '最近一次查询时间',
  create_by       varchar(64)           comment '创建者',
  create_time     datetime            comment '创建时间',
  update_by       varchar(64)         comment '更新者',
  update_time     datetime           comment '更新时间',
  primary key (id),
  key idx_appointment (appointment_id)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='物流轨迹表';

-- ----------------------------
-- 字典配置
-- ----------------------------
insert into sys_dict_type values(30, '预约状态', 'cosm_appoint_status',        '0', 'admin', sysdate(), '', null, '预约单状态');
insert into sys_dict_type values(31, '商品成色', 'cosm_condition_level',       '0', 'admin', sysdate(), '', null, '商品成色');
insert into sys_dict_type values(32, '物流状态', 'cosm_track_status',          '0', 'admin', sysdate(), '', null, '物流轨迹状态');

insert into sys_dict_data values(100, 1, '待确认',   '0', 'cosm_appoint_status', '', 'info',    'Y', '0', 'admin', sysdate(), '', null, '待卖家确认');
insert into sys_dict_data values(101, 2, '已确认',   '1', 'cosm_appoint_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '卖家已确认');
insert into sys_dict_data values(102, 3, '已取消',   '2', 'cosm_appoint_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '已取消');
insert into sys_dict_data values(103, 4, '已完成',   '3', 'cosm_appoint_status', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '已完成');
insert into sys_dict_data values(104, 5, '已发货',   '4', 'cosm_appoint_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '已发货');

insert into sys_dict_data values(105, 1, '仅试色',     '0', 'cosm_condition_level', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '仅试色');
insert into sys_dict_data values(106, 2, '余量80%',    '1', 'cosm_condition_level', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '余量80%');
insert into sys_dict_data values(107, 3, '余量50%',    '2', 'cosm_condition_level', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '余量50%');
insert into sys_dict_data values(108, 4, '全新未拆封', '3', 'cosm_condition_level', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '全新未拆封');

insert into sys_dict_data values(109, 1, '收件/揽收', '0', 'cosm_track_status', '', 'info',    'N', '0', 'admin', sysdate(), '', null, '已揽收');
insert into sys_dict_data values(110, 2, '在途中',    '1', 'cosm_track_status', '', 'primary', 'N', '0', 'admin', sysdate(), '', null, '运输中');
insert into sys_dict_data values(111, 3, '派送中',    '2', 'cosm_track_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '派送中');
insert into sys_dict_data values(112, 4, '已签收',    '3', 'cosm_track_status', '', 'success', 'Y', '0', 'admin', sysdate(), '', null, '已签收');

-- ----------------------------
-- 菜单(目录->菜单->按钮) 权限标识
-- ----------------------------
insert into sys_menu values(2000, '化妆品管理', '0', 5, 'cosmetics', null, '', '', 1, 0, 'M', '0', '0', '', 'skin', 'admin', sysdate(), '', null, '化妆品管理目录');
insert into sys_menu values(2001, '商品管理', '2000', 1, 'product', 'system/cosmeticsProduct/index', '', '', 1, 0, 'C', '0', '0', 'cosmetics:product:list', 'goods', 'admin', sysdate(), '', null, '商品管理菜单');
insert into sys_menu values(2002, '商品新增', '2001', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:product:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2003, '商品修改', '2001', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:product:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2004, '商品删除', '2001', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:product:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2005, '预约管理', '2000', 2, 'appointment', 'system/cosmeticsAppointment/index', '', '', 1, 0, 'C', '0', '0', 'cosmetics:appointment:list', 'edit', 'admin', sysdate(), '', null, '预约管理菜单');
insert into sys_menu values(2006, '预约添加', '2005', 1, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:appointment:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2007, '预约修改', '2005', 2, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:appointment:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2008, '预约删除', '2005', 3, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:appointment:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2009, '取消预约', '2005', 4, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:appointment:cancel', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values(2010, '预约发货', '2005', 5, '', '', '', '', 1, 0, 'F', '0', '0', 'cosmetics:appointment:ship', '#', 'admin', sysdate(), '', null, '');

-- 授权给 admin 角色(role_id=1)全量
insert into sys_role_menu select distinct r.role_id, m.menu_id from sys_role r, (select 2000 as menu_id union select 2001 union select 2002 union select 2003 union select 2004 union select 2005 union select 2006 union select 2007 union select 2008 union select 2009 union select 2010) m where r.role_id = 1;

-- ----------------------------
-- 定时任务: 预约超时自动取消 (每5分钟执行一次)
-- invoke_target 对应 com.relily.system.task.AppointmentTimeoutTask 的 cancelExpired 方法
-- ----------------------------
insert into sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
values ('预约超时自动取消', 'DEFAULT', 'appointmentTimeoutTask.cancelExpired', '0 0/5 * * * ?', '3', '1', '0', 'admin', sysdate(), '扫描待确认超时预约并自动取消');

-- ----------------------------
-- 清理: 删除「系统工具」菜单树(表单构建/代码生成/系统接口)及其角色授权
-- 前端已移除 tool 视图, 避免菜单指向不存在页面
-- ----------------------------
delete from sys_role_menu where menu_id in (
  select menu_id from (
    select menu_id from sys_menu where menu_id = 3 or parent_id = 3 or perms like 'tool:%'
  ) tmp
);
delete from sys_menu where menu_id in (
  select menu_id from (
    select menu_id from sys_menu where menu_id = 3 or parent_id = 3 or perms like 'tool:%'
  ) tmp
);