
create table calc_score(
        member_id bigint not null,
        monthly_exp int not null,
        yearly_exp int not null,
        eo int not null,
        last_eo int not null,
        yyyymmddhh int not null,
        created bigint not null,
        primary key(member_id, yyyymmddhh)
) engine = ARIA, default charset=utf8mb4;
