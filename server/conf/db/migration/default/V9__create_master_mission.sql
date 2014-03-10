
create table master_mission(
        id int not null primary key,
        map_area tinyint not null,
        `name` tinytext not null,
        `time` int not null,
        fuel double not null,
        ammo double not null,
        index map_area_index (map_area)
) engine = ARIA, default charset=utf8;
