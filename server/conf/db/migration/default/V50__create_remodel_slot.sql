
create table remodel_slot(
        id int not null,
        slot_id int not null,
        member_id int not null,
        second_ship int,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        develop int not null,
        revamping int not null,
        req_slot_id int not null,
        slot_num int not null,
        created bigint not null,
        index slot_id_index (slot_id)
) engine = ARIA, default charset=utf8mb4;
