
create table Basic(
        id bigint not null auto_increment unique,
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
) default charset=utf8;

create table Auth(
        id bigint not null unique,
        nickname tinytext not null,
        created bigint not null
) default charset=utf8;

create table Material(
        id bigint not null auto_increment unique,
        fuel int not null,
        ammo int not null,
        steel int not null,
        bauxite int not null,
        instant int not null,
        bucket int not null,
        develop int not null,
        created bigint not null
) default charset=utf8;
