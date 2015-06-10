
drop table ship_sound;
create table ship_sound(
        ship_id int not null,
        sound_id int not null,
        version int not null,
        sound mediumblob not null,
        primary key(ship_id, sound_id, version)
) engine = ARIA, default charset=utf8;
