
create table myfleet_auth(
        id bigint not null primary key,
        hash binary(32) not null,
        salt binary(32) not null,
        created bigint not null
) engine = ARIA, default charset=utf8;
