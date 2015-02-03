
create table yome_ship(
        member_id bigint not null,
        sort_no smallint not null,
        ship_id int not null,
        primary key (member_id, sort_no)
) engine = ARIA, default charset = utf8mb4;

insert into yome_ship (member_id, ship_id, sort_no) select member_id, yome, 1 from user_settings where yome is not null;

alter table user_settings drop yome;
