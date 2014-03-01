
create table basic(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        lv smallint not null,
        experience int not null,
        rank smallint not null,
        max_chara smallint not null,
        f_coin int not null,
        st_win int not null,
        st_lose int not null,
        ms_count int not null,
        ms_success int not null,
        pt_win int not null,
        pt_lose int not null,
        created bigint not null
) engine = ARIA, default charset=utf8;

create table admiral(
        id bigint not null unique,
        nickname_id bigint not null,
        nickname tinytext not null,
        created bigint not null
) engine = ARIA, default charset=utf8;

create table material(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        instant smallint not null,
        bucket smallint not null,
        develop smallint not null,
        created bigint not null
) engine = ARIA, default charset=utf8;
