
create table master_ship(
        id int not null unique,
        `name` tinytext not null,
        yomi tinytext not null
) default charset=utf8;
