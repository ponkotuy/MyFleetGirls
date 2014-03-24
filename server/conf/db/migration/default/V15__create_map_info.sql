
create table map_info (
        member_id bigint not null,
        id int not null,
        cleared boolean not null,
        exboss_flag boolean not null,
        primary key(member_id, id)
) engine = ARIA, default charset=utf8;
