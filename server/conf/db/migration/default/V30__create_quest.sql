
create table quest(
        member_id bigint not null,
        id int not null,
        category int not null,
        typ int not null,
        `state` int not null,
        title tinytext not null,
        detail text not null,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        bonus boolean not null,
        progress_flag int not null,
        created bigint not null,
        primary key(member_id, id)
) engine = ARIA default charset=utf8;
