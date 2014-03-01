
create table master_ship(
        id int not null primary key,
        `name` tinytext not null,
        yomi tinytext not null
) engine = ARIA, default charset=utf8;
