
create table honor(
        id bigint not null auto_increment,
        member_id bigint not null,
        category int not null,
        `name` varchar(128) not null,
        set_badge boolean not null default false,
        primary key(id),
        unique(member_id, `name`),
        key(member_id, set_badge)
) engine = ARIA, default charset=utf8mb4;
