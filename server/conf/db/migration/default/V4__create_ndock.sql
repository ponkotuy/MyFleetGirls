
create table n_dock(
        id int not null,
        member_id bigint not null,
        ship_id int not null,
        complete_time bigint not null,
        created bigint not null,
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;
