-- 32-3 ボスは 10(J) ではなく 11(K)
update cell_info set boss = false where area_id = 32 and info_no = 3 and cell = 10;
update cell_info set boss = true where area_id = 32 and info_no = 3 and cell = 11;

-- 32-5-2(B) は start ではない
update cell_info set `start` = false where area_id = 32 and info_no = 5 and cell = 2;
