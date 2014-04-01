
alter table ship drop slot;

create table ship_slot_item(
        member_id bigint not null,
        ship_id int not null,
        id tinyint not null, -- 1 to 4
        slotitem_id int not null,
        primary key(member_id, ship_id, id)
) engine = ARIA, default charset=utf8;
