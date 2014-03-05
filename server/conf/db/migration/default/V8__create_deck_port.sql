
drop table if exists deck_port, mission, deck_ship;

create table deck_port(
        id tinyint not null,
        member_id bigint not null,
        `name` tinytext not null,
        created bigint not null,
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;

create table mission(
        member_id bigint not null,
        deck_id int not null,
        page int not null,
        `number` int not null,
        complete_time bigint not null,
        created bigint not null,
        primary key(member_id, deck_id)
) engine = ARIA, default charset=utf8;

create table deck_ship(
        deck_id tinyint not null,
        num tinyint not null,
        member_id bigint not null,
        ship_id int not null,
        unique(member_id, deck_id, num)
) engine = ARIA, default charset=utf8;
