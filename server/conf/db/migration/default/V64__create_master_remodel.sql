
create table master_remodel(
        slotitem_id int not null,
        slotitem_level int not null,
        develop int not null,
        remodel int not null,
        certain_develop int not null,
        certain_remodel int not null,
        use_slotitem_id int not null,
        use_slotitem_num int not null,
        change_flag boolean not null,
        primary key(slotitem_id, slotitem_level)
) engine = ARIA, default charset = utf8mb4;
