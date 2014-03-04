
create table create_ship(
        member_id bigint not null,
        result_ship int not null,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        develop int not null,
        k_dock int not null,
        highspeed boolean not null,
        large_flag boolean not null,
        complete_time bigint not null,
        created bigint not null,
        primary key(member_id, k_dock, complete_time)
) engine = ARIA, default charset=utf8;
