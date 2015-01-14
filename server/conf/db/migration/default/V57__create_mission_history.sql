
create table mission_history(
        id bigint primary key auto_increment,
        member_id bigint not null,
        deck_id int not null,
        page int not null,
        `number` int not null,
        complete_time bigint not null,
        created bigint not null,
        index(member_id, created),
        unique(member_id, complete_time)
) engine = ARIA, default charset=utf8;
