
create table deck_snapshot(
        id bigint not null primary key auto_increment,
        member_id bigint not null,
        `name` tinytext not null,
        title text not null,
        comment text not null,
        created bigint not null,
        index(member_id)
) engine = ARIA, default charset=utf8mb4;

create table deck_ship_snapshot(
        id bigint not null primary key auto_increment,
        member_id bigint not null,
        deck_id bigint not null,
        num smallint not null,
        ship_id int not null,
        lv smallint not null,
        exp int not null,
        nowhp smallint not null,
        slot tinytext not null,
        fuel int not null,
        bull int not null,
        dock_time bigint not null,
        cond smallint not null,
        karyoku smallint not null,
        raisou smallint not null,
        taiku smallint not null,
        soukou smallint not null,
        kaihi smallint not null,
        taisen smallint not null,
        sakuteki smallint not null,
        lucky smallint not null,
        locked boolean not null,
        created bigint not null,
        maxhp smallint not null,
        unique(deck_id, num),
        index(member_id)
) engine = ARIA, charset=utf8mb4;
