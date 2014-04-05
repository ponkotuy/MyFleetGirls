
create table master_stype(
        id int not null primary key,
        sortno int not null,
        `name` tinytext not null,
        scnt int not null,
        kcnt int not null
) engine = ARIA, default charset=utf8;
