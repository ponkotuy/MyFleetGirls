
alter table battle_result add index br_member_area_info_cell(member_id, area_id, info_no, cell);
alter table map_route add index mr_member_area_info(member_id, area_id, info_no);
