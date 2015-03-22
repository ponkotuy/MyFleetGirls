
create table fleet_group(
        id bigint not null auto_increment,
        `name` varchar(128) not null,
        primary key (id)
) engine = ARIA, default charset=utf8mb4;

create table fleet_group_member(
        group_id bigint not null,
        ship_id bigint not null,
        primary key (group_id, ship_id)
) engine = ARIA, default charset=utf8mb4;
