
create table user_settings(
        member_id bigint not null primary key,
        yome int not null
) engine = ARIA, default charset=utf8;
