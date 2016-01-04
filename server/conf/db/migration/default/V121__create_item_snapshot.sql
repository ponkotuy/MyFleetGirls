
create table item_snapshot(
        id bigint auto_increment not null,
        member_id bigint not null,
        ship_snapshot_id bigint not null,
        `position` tinyint not null, -- 1 to 4
        slotitem_id int not null,
        `level` int not null,
        alv int, -- 1 to 7
        created bigint not null,
        primary key (id),
        unique key (member_id, ship_snapshot_id, `position`)
) engine = ARIA, default charset=utf8mb4;
