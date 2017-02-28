-- 37-1 ボスは 12(B) ではなく 11(K)
update cell_info set boss = true where area_id = 37 and info_no = 1 and cell = 11;
update cell_info set boss = false where area_id = 37 and info_no = 1 and cell = 12;

