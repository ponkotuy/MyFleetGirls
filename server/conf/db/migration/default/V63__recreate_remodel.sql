
drop table remodel;
create table remodel(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        flag boolean not null,
        before_item_id int not null,
        after_item_id int not null,
        vocie_id int not null,
        use_slot_ids varchar(128) not null,
        certain boolean not null,
        before_item_level int not null,
        first_ship_id int not null,
        second_ship_id int,
        created bigint not null,
        index (member_id)
) engine = ARIA, default charset=utf8mb4;

drop table remodel_after_slot;
create table remodel_after_slot(
        remodel_id bigint not null primary key,
        id int not null,
        slotitem_id int not null,
        locked boolean not null,
        `level` int not null,
        created bigint not null
) engine = ARIA, default charset=utf8mb4;
