
create table snapshot_text(
        id bigint not null,
        content text(65535) not null,
        primary key(id),
        fulltext index(content)
) ENGINE = InnoDB DEFAULT CHARSET utf8mb4;
