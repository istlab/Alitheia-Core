-- This is a script to populate a sample database.
-- The database must already exist and the tables
-- and schemata must exist.
--
-- We use a few place-markers of the form @@NAME@@
-- to indicate where the script varies between 
-- Derby and Postgres, since they are not 100%
-- compatible. These markers are:
--
--
-- @@SCHEMA@@
--	Derby requires ALITHEIA. schema specified,
--	Postgres requires this to be absent.
-- @@HOUR@@
--	Derby's timestamp format is more rigid than
--	Postgres, but incompatible. Includes the '-'
--	that separates the day from the hour.
--
--


-- Projects stored in the system. Project 1, yoyo, is bogus.
-- It amalgamates SQO-OSS resources and a CodeYard project
-- called YoYo which is just a playground.

insert into @@SCHEMA@@STORED_PROJECT values ( 
	1, 
	'yoyo', 
	'http://www.yoyo.com',
	'groot@kde.org', 
	'file:/dev/null',
	'http://cvs.codeyard.net/svn/yoyo/',
	'maildir:../examples/mail/SQO-OSS'
	) ;

insert into @@SCHEMA@@STORED_PROJECT values (
        2,
	'VLC',   
	'http://www.videolan.org',
	'blah@vlc.org',                 
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/vlc',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @@SCHEMA@@STORED_PROJECT values (
        3,
	'NMAP',
        'http://www.nmap.org',
        'blah@nmap.org',
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/nmap',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @@SCHEMA@@STORED_PROJECT values (
        4,
	'SVN',
        'http://www.subversion.org',
        'blah@svn.org',
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/subversion',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @@SCHEMA@@STORED_PROJECT values (
        5,
	'GCC',
        'http://www.gcc.org',
        'blah@gcc.org',
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/gcc',
        'maildir:../examples/mail/SQO-OSS'
) ;

-- These are mailing lists for project 1, which is the
-- yoyo / SQO-OSS amalgam.

insert into @@SCHEMA@@MAILINGLIST values (1,'all',1);
insert into @@SCHEMA@@MAILINGLIST values (2,'wp1',1);
insert into @@SCHEMA@@MAILINGLIST values (3,'wp3',1);


-- This is the user database.

insert into @@SCHEMA@@USERS values (
	1,
	'Fluffy Bunny',
	0,
	'1970-1-1@@HOUR@@',
	'2008-1-14@@HOUR@@',
	'carrot',
	'bunny@kde.org'
	) ;

