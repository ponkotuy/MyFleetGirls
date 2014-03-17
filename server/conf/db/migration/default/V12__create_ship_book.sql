
create table ship_book(
        member_id bigint not null,
        id int not null,
        index_no int not null,
        is_dameged boolean not null,
        `name` tinytext not null,
        updated bigint not null,
        primary key(member_id, index_no)
) engine = ARIA, default charset=utf8;
