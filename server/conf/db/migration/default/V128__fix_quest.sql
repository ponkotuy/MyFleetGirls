delete from quest where typ != 1 and created < 1465538400000;
update quest set typ = 4 where created < 1465538400000 and typ = 1;
