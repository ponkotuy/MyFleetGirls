
delete from master_ship;
alter table master_ship add filename char(13) not null;
create unique index ms_fname_index on master_ship(filename);

alter table ship_image add filename char(13);
alter table ship_image add member_id bigint not null default 0;
create unique index si_fname_index on ship_image(filename);
