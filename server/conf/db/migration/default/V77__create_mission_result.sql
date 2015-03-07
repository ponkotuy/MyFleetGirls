
create table mission_result(
        id bigint not null auto_increment,
        member_id bigint not null,
        `result` int not null,
        detail varchar(1024) not null,
        get_exp int not null,
        get_ship_exp varchar(256) not null,
        fuel int not null,
        bull int not null,
        steel int not null,
        bauxite int  not null,
        maparea_name varchar(64) not null,
        mission_lv int not null,
        mission_name varchar(64) not null,
        useitem_flag varchar(64),
        primary key(id),
        index (member_id)
) engine = ARIA, default charset=utf8mb4;

create table mission_result_ship(
        result_id bigint not null,
        ship_id int not null,
        primary key(result_id, ship_id)
) engine = ARIA, default charset=utf8mb4;

create table mission_get_item(
        result_id bigint not null,
        item_id int not null,
        `name` varchar(64) not null,
        `count` int not null,
        primary key(result_id)
) engine = ARIA, default charset=utf8mb4;
