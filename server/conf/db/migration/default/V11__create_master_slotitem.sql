
create table master_slot_item(
        id int not null primary key,
        `name` tinytext not null,
        typ tinytext not null,
        power int not null,
        torpedo int not null,
        bomb int not null,
        antiAir int not null,
        antiSub int not null,
        `search` int not null,
        hit int not null,
        `length` int not null,
        rare int not null,
        info text not null
) engine = ARIA, default charset=utf8;
