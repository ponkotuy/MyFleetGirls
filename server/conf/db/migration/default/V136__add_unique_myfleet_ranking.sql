
-- CREATE NEW TABLE
CREATE TABLE `myfleet_ranking_tmp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ranking_id` int(11) NOT NULL,
  `rank` int(11) NOT NULL,
  `yyyymmddhh` int(11) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `target_name` tinytext NOT NULL,
  `data` text NOT NULL,
  `url` text,
  `num` bigint(20) NOT NULL,
  `created` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `yyyymmddhh` (`yyyymmddhh`, `ranking_id`, `rank`),
  KEY `myfleet_ranking_target_id` (`target_id`, `rank`)
) ENGINE=Aria DEFAULT CHARSET=utf8mb4;

-- INSERT NEW TABLE
lock tables myfleet_ranking write, myfleet_ranking_tmp write;
insert ignore into myfleet_ranking_tmp select * from myfleet_ranking;

-- RENAME
drop table myfleet_ranking;
unlock tables;
rename table myfleet_ranking_tmp to myfleet_ranking;
