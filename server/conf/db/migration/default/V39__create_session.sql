
create table `session`(
        uuid_most bigint not null,
        uuid_least bigint not null,
        member_id bigint not null,
        created bigint not null,
        primary key(uuid_most, uuid_least),
        unique(member_id)
) engine = ARIA, default charset=utf8mb4;
