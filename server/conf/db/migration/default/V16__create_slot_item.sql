
create table slot_item(
        member_id bigint not null,
        id int not null,
        slotitem_id int not null,
        `name` tinytext not null,
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;
