
create table mission_history_ship(
        id int not null,
        ship_id int not null,
        member_id bigint not null,
        mission_id bigint not null,
        lv smallint not null,
        exp int not null,
        nowhp smallint not null,
        slot tinytext not null,
        fuel int not null,
        bull int not null,
        created bigint not null,
        primary key(mission_id, id)
) engine = ARIA, default charset=utf8mb4;
