
create table admiral(
        id bigint not null primary key,
        nickname_id bigint not null,
        nickname tinytext not null,
        created bigint not null
) engine = ARIA, default charset=utf8;
