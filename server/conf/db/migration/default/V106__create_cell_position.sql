
create table cell_position(
        area_id int not null,
        info_no int not null,
        cell int not null,
        pos_x int not null,
        pos_y int not null,
        primary key(area_id, info_no, cell)
) engine = ARIA, default charset=utf8mb4;

create table map_image(
        area_id int not null,
        info_no int not null,
        image mediumblob not null,
        version smallint not null,
        primary key (area_id, info_no, version)
) engine = ARIA, default charset=utf8mb4;
