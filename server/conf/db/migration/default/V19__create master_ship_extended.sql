
create table master_ship_specs(
        id int not null primary key,
        hp int not null,
        souko_min int not null,
        souko_max int not null,
        tousai int not null,
        karyoku_min int not null,
        karyoku_max int not null,
        raisou_min int not null,
        raisou_max int not null,
        taiku_min int not null,
        taiku_max int not null,
        taisen_min int not null,
        taisen_max int not null,
        kaihi_min int not null,
        kaihi_max int not null,
        sakuteki_min int not null,
        sakuteki_max int not null,
        lucky_min int not null,
        lucky_max int not null,
        sokuh int not null,
        soku int not null,
        `length` int not null
) engine = ARIA, default charset=utf8;

create table master_ship_after(
        id int not null primary key,
        afterlv int not null,
        aftershipid int not null,
        afterfuel int not null,
        afterbull int not null
) engine = ARIA, default charset=utf8;

create table master_ship_other(
        id int not null primary key,
        defeq tinytext not null,
        buildtime int not null,
        broken_fuel int not null,
        broken_ammo int not null,
        broken_steel int not null,
        broken_bauxite int not null,
        powup_fuel int not null,
        powup_ammo int not null,
        powup_steel int not null,
        powup_bauxite int not null,
        backs int not null,
        fuel_max int not null,
        bull_max int not null,
        slot_num int not null
) engine = ARIA, default charset=utf8;
