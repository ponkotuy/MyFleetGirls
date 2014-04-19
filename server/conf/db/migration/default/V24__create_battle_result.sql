
create table battle_result(
        id bigint not null auto_increment primary key,
        member_id bigint not null,
        area_id tinyint not null,
        info_no tinyint not null,
        cell tinyint not null,
        enemies tinytext not null,
        win_rank char(1) not null,
        quest_name tinytext not null,
        quest_level tinyint not null,
        enemy_deck tinytext not null,
        first_clear boolean not null,
        get_ship_id int,
        get_ship_type tinytext,
        get_ship_name tinytext,
        created bigint not null
) engine = ARIA, default charset=utf8;

create table ship_battle_result(
        battle_id bigint not null,
        member_id bigint not null,
        id tinyint not null,
        exp int not null,
        lost_flag boolean not null
) engine = ARIA, default charset=utf8;
