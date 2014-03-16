
create table create_item(
        member_id bigint not null,
        id bigint not null auto_increment primary key,
        item_id int,
        slotitem_id int,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        create_flag boolean not null,
        shizai_flag boolean not null,
        flagship int not null,
        created bigint not null,
        unique(member_id, item_id)
) engine = ARIA, default charset=utf8;
