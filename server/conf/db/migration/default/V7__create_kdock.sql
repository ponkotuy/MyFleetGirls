
create table k_dock(
        id tinyint not null,
        member_id bigint not null,
        ship_id int not null,
        `state` tinyint not null,
        complete_time bigint not null,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        created bigint not null,
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;
