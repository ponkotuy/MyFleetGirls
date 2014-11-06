
create table favorite(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        url text(2000) not null,
        hash_url bigint not null,
        `first` varchar(32) not null,
        `second` varchar(32) not null,
        created bigint not null,
        index(member_id),
        index(`first`, `second`),
        unique(member_id, hash_url)
) engine = ARIA, default charset=utf8mb4;
