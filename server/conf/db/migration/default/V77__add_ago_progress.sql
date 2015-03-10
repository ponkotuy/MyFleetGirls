create table ago_progress(
        member_id bigint not null,
        sortie int not null,
        rank_s int not null,
        reach_boss int not null,
        win_boss int not null,
        primary key(member_id)
) engine = ARIA, default charset=utf8mb4;
