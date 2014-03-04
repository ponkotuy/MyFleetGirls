
create table deck_port(
        id tinyint not null,
        member_id bigint not null,
        mission_id bigint not null,
        `name` tinytext not null,
        created bigint not null
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;

create table mission(
        id bigint not null auto_increment,
        page int not null,
        `number` int not null,
        complete_time bigint not null,
        created bigint not null
) engine = ARIA, default charset=utf8;

create table deck_ship(
        id bigint not null auto_increment,
        member_id bigint not null,
        ship_id int not null
) engine = ARIA, default charset=utf8;
