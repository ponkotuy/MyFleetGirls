
create table map_route(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        area_id tinyint not null,
        info_no tinyint not null,
        dep tinyint not null,
        dest tinyint not null,
        fleet tinytext not null,
        created bigint not null
) engine = ARIA, default charset=utf8;
