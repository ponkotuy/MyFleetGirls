
create table ship_history(
        id bigint not null auto_increment,
        ship_id int not null,
        member_id bigint not null,
        lv smallint not null,
        exp int not null,
        created bigint not null,
        primary key(id),
        unique key(member_id, ship_id, created)
) engine = ARIA, default charset=utf8mb4;
