
drop table ship_image;
create table ship_image(
        id int not null,
        image mediumblob not null,
        filename char(13) not null,
        member_id bigint not null,
        swf_id smallint not null,
        primary key(id, swf_id),
        unique(filename, swf_id)
) engine = ARIA, default charset=utf8mb4;
