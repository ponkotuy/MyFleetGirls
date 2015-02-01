
create table remodel(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        flag boolean not null,
        before_item_id int not null,
        after_item_id int not null,
        vocie_id int not null,
        use_slot_ids varchar(128) not null,
        certain boolean not null,
        index (member_id)
) engine = ARIA, default charset=utf8mb4;

create table remodel_after_slot(
        remodel_id bigint not null primary key,
        id int not null,
        slotitem_id int not null,
        locked boolean not null,
        `level` int not null
) engine = ARIA, default charset=utf8mb4;


alter table remodel_slot change member_id member_id bigint not null;
