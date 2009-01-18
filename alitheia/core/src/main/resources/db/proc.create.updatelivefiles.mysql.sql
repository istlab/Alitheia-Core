DELIMITER // 

DROP PROCEDURE IF EXISTS updatelivefiles;
//

CREATE PROCEDURE updatelivefiles (IN oldpv BIGINT, IN newpv BIGINT, 
    IN deletedstatusid INT)
BEGIN

declare projectid BIGINT;
declare fileid BIGINT;
declare done INT default 0;

declare cur1 CURSOR FOR 
   select PROJECT_FILE_ID 
   from PROJECT_VERSION pv1, PROJECT_FILE pf, 
   PROJECT_VERSION pv2, PROJECT_VERSION pv3, PROJECT_VERSION pv4, 
   DIRECTORY dir 
   where
    pf.VALID_FROM_ID = pv2.PROJECT_VERSION_ID 
    and pf.VALID_TO_ID = pv3.PROJECT_VERSION_ID 
    and pf.PROJECT_VERSION_ID = pv4.PROJECT_VERSION_ID 
    and pf.DIRECTORY_ID = dir.DIRECTORY_ID 
    and pv2.VERSION_SEQUENCE <= pv1.VERSION_SEQUENCE 
    and pv3.VERSION_SEQUENCE >= pv1.VERSION_SEQUENCE 
    and pf.STATE_ID <> deletedstatusid 
    and pv4.STORED_PROJECT_ID = projectid
    and pv1.STORED_PROJECT_ID = projectid
    and pv1.PROJECT_VERSION_ID = oldpv 
    and concat(dir.PATH,'/',pf.FILE_NAME) not in (
        select concat(dir2.PATH,'/',pf2.FILE_NAME) 
        from PROJECT_FILE pf2, DIRECTORY dir2
        where pf2.PROJECT_VERSION_ID = newpv
        and pf2.DIRECTORY_ID=dir2.DIRECTORY_ID
		and dir.directory_id = dir2.directory_id
	);

declare continue handler for not found set done = 1;

create temporary table if not exists PROJECT_FILES_UPDATE (
    PROJECT_FILE_ID BIGINT,
    index using hash (PROJECT_FILE_ID)
) ENGINE=MEMORY;

select STORED_PROJECT_ID into projectid
from PROJECT_VERSION
where PROJECT_VERSION_ID = newpv;

open cur1;

repeat
    fetch cur1 into fileid;
    insert into PROJECT_FILES_UPDATE values (fileid);
until done end repeat;

update PROJECT_FILE, PROJECT_FILES_UPDATE
set  PROJECT_FILE.VALID_TO_ID=newpv
where  PROJECT_FILE.PROJECT_FILE_ID = PROJECT_FILES_UPDATE.PROJECT_FILE_ID;

delete from PROJECT_FILES_UPDATE;

END;
//

DELIMITER ;

