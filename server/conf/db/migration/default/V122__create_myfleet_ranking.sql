
create table myfleet_ranking(
        id bigint not null primary key auto_increment,
        ranking_id int not null,
        rank int not null,
        yyyymmddhh int not null,
        target_id bigint not null, -- member_idまたはship_id
        target_name tinytext not null,
        `data` text not null,      -- JSON形式
        url text,
        num bigint not null,
        created bigint not null,
        key(yyyymmddhh)
) engine = ARIA, default charset=utf8mb4;
