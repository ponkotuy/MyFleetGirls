
alter table basic add comment varchar(1024) not null default "";
alter table basic add deck_count smallint not null default 2;
alter table basic add kdock_count smallint not null default 2;
alter table basic add ndock_count smallint not null default 2;
alter table basic add large_dock boolean not null default false;
