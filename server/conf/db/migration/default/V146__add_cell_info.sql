-- 37-1 ボスは 12(B) ではなく 11(K)
update cell_info set boss = true where area_id = 39 and info_no = 6 and cell = 8;

insert into cell_info values
    (39, 6, 12, 'G', false, false),
    (39, 6, 14, 'J', false, false),
    (39, 6, 15, 'K', false, false),
    (39, 6, 16, 'L', false, false),
    (39, 6, 17, 'M', false, false),
    (39, 6, 18, 'N', false, false),
    (39, 6, 19, 'O', false, false),
    (39, 6, 20, 'P', false, false),
    (39, 6, 21, 'Q', false, false),
    (39, 6, 22, 'R', false, false),
    (39, 6, 23, 'S', false, true),
    (39, 6, 24, 'K', true, false),
    (39, 6, 25, 'O', false, false),
    (39, 6, 26, 'P', false, false),
    (39, 6, 27, 'P', false, false);
