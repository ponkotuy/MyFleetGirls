
create table item_book(
        member_id bigint not null,
        id int not null,
        index_no int not null,
        updated bigint not null,
        primary key(member_id, index_no)
) engine = ARIA, default charset=utf8;
