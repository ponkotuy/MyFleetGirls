
alter table slot_item add created bigint;

create index slot_item_created on slot_item(created);
