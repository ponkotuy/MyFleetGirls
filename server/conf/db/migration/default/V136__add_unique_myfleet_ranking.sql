
-- Delete duplicate data
DELETE FROM myfleet_ranking WHERE id NOT IN (SELECT min_id from (SELECT MIN(id) min_id FROM myfleet_ranking GROUP BY ranking_id, rank, yyyymmddhh) tmp);

-- Add unique index
ALTER TABLE myfleet_ranking add unique (yyyymmddhh, ranking_id, rank);
