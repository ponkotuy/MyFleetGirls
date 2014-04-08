
create table ship_sound(
        ship_id int not null,
        sound_id int not null,
        sound mediumblob not null,
        primary key(ship_id, sound_id)
) engine = ARIA, default charset=utf8;
