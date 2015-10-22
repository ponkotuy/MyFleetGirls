
-- ShipHistory
alter table ship_history add primary key (member_id, ship_id, created);
alter table ship_history drop index member_id;

-- DeckShip
alter table deck_ship add primary key (member_id, deck_id, num);
alter table deck_ship drop index member_id;

-- ShipBattleResult
drop table ship_battle_result;

-- RemodelSlot
alter table remodel_slot add column `_id` bigint not null auto_increment primary key;

-- SnapshotText
alter table snapshot_text engine = ARIA;
