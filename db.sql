create table message_commodity_order
(
    order_id           bigint        not null
        primary key,
    commodity_id       bigint        not null,
    stock_decrement    int           not null,
    stock_decrement_at timestamp     null,
    user_id            bigint        not null,
    coin_decrement     decimal(8, 2) not null,
    coin_decrement_at  timestamp     null,
    status             tinyint       not null,
    created_at         timestamp     not null
)
    comment 'commodity-order 服务的消息表';
create table recharge
(
	id bigint auto_increment
		primary key,
	user_id bigint not null,
	coin decimal(8,2) not null,
	created_at datetime null,
	status tinyint default 0 not null comment '-1：已取消
0：未支付
1：已支付
',
	pay_at datetime null,
	trade_no char(28) null comment '如果是支付宝则为支付宝订单号，如果为充值卡则为充值卡号',
	way tinyint null comment '1：支付宝
2：充值卡'
);
create table commodity_order
(
	id bigint auto_increment
		primary key,
	user_id bigint not null comment '消费者 id',
	commodity_id bigint not null,
	created_at timestamp null,
	status tinyint null comment '1.待发货
2.用户催单
3.所有者已发送奖品
4.完成订单
5.订单异常',
	price decimal(8,2) default 0.00 null,
	constraint spike_order_pk
		unique (user_id, commodity_id)
);

create index commodity_order_commodity_id_index
	on commodity_order (commodity_id);

create index commodity_order_user_id_index
	on commodity_order (user_id);

create table message_commodity
(
    order_id           bigint    not null
        primary key,
    commodity_id       bigint    not null,
    stock_decrement    int       not null,
    stock_decrement_at timestamp null,
    created_at         timestamp not null
)
    comment 'commodity 服务的消息表';
create table user
(
    id         bigint auto_increment
        primary key,
    username   varchar(60)                not null,
    password   char(32)                   not null comment '两次 md5 第一次在客户端（防劫持），第二次在服务器（防数据泄露后被彩虹表破解）',
    nickname   varchar(60)                null,
    created_at timestamp                  null comment '创建时间',
    updated_at timestamp                  null comment '上次修改时间',
    email      varchar(64)                null,
    phone      char(11)                   null,
    coin       decimal(8, 2) default 0.00 null,
    constraint username
        unique (username)
);
create table message_user
(
    order_id          bigint        not null
        primary key,
    user_id           bigint        not null,
    coin_decrement    decimal(8, 2) not null,
    coin_decrement_at timestamp     null,
    created_at        timestamp     not null
);
create table commodity
(
    id         bigint auto_increment
        primary key,
    name       varchar(32)                not null,
    user_id    bigint                     null comment '创建者',
    detail     longtext                   null,
    start_at   timestamp                  not null comment '起始时间',
    end_at     timestamp                  not null comment '结束时间',
    stock      int(12)       default 0    not null,
    created_at timestamp                  not null,
    updated_at timestamp                  not null,
    price      decimal(8, 2) default 0.00 null
);