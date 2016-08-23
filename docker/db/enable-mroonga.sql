INSTALL PLUGIN Mroonga SONAME 'ha_mroonga.so';
CREATE FUNCTION last_insert_grn_id RETURNS INTEGER SONAME 'ha_mroonga.so';
CREATE FUNCTION mroonga_snippet RETURNS STRING SONAME 'ha_mroonga.so';
CREATE FUNCTION mroonga_command RETURNS STRING SONAME 'ha_mroonga.so';
CREATE FUNCTION mroonga_escape RETURNS STRING SONAME 'ha_mroonga.so';
