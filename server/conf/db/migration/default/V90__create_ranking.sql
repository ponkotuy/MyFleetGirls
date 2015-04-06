
create table ranking(
        id bigint not null auto_increment,
        member_id bigint not null,
        `no` int not null,
        rate int not null,
        created bigint not null,
        primary key(id),
        unique key(member_id, created)
) engine = ARIA, default charset=utf8mb4;
