
-- alphabet: Wikiに付けられているアルファベット
drop table if exists cell_info;
create table cell_info(
    area_id int not null,
    info_no int not null,
    cell int not null,
    alphabet char(2) not null,
    `start` boolean not null,
    boss boolean not null,
    primary key(area_id, info_no, cell)
) engine = ARIA, default charset=utf8;

-- 暫定的に1-1, 2-1, 2-2, 2-3の対照表
insert into cell_info values
    (1, 1, 1, 'A', true, false),
    (1, 1, 2, 'B', false, false),
    (1, 1, 3, 'C', false, true),
    (2, 1, 1, 'A', true, false),
    (2, 1, 2, 'B', false, false),
    (2, 1, 3, 'D', false, false),
    (2, 1, 4, 'C', false, false),
    (2, 1, 5, 'F', false, false),
    (2, 1, 6, 'E', false, true),
    (2, 2, 1, 'A', true, false),
    (2, 2, 2, 'E', true, false),
    (2, 2, 3, 'B', false, false),
    (2, 2, 4, 'G', false, false),
    (2, 2, 5, 'C', false, false),
    (2, 2, 6, 'D', false, false),
    (2, 2, 7, 'F', false, true),
    (2, 2, 8, 'E', false, false),
    (2, 3, 1, 'C', true, false),
    (2, 3, 2, 'A', true, false),
    (2, 3, 3, 'H', false, false),
    (2, 3, 4, 'D', false, false),
    (2, 3, 5, 'B', false, false),
    (2, 3, 6, 'E', false, false),
    (2, 3, 7, 'I', false, false),
    (2, 3, 8, 'J', false, false),
    (2, 3, 9, 'K', false, false),
    (2, 3, 10, 'F', false, false),
    (2, 3, 11, 'G', false, true),
    (2, 3, 12, 'E', false, false);
