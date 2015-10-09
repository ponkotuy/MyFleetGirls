alter table ship_history drop id;
alter table ship_history partition by hash(member_id) partitions 16;
