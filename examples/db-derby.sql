-- This is a script to populate a sample database.
-- The database must already exist and the tables
-- and schemata must exist.
--
--


-- Projects stored in the system. Project 1, yoyo, is bogus.
-- It amalgamates SQO-OSS resources and a CodeYard project
-- called YoYo which is just a playground.

insert into @SCHEMA@STORED_PROJECT values ( 
	1, 
	'yoyo', 
	'http://www.yoyo.com',
	'groot@kde.org', 
	'file:/dev/null',
	'http://cvs.codeyard.net/svn/yoyo/',
	'maildir:../examples/mail/SQO-OSS'
	) ;

insert into @SCHEMA@STORED_PROJECT values (
        2,
	'VLC',   
	'http://www.videolan.org',
	'blah@vlc.org',                 
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/vlc',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @SCHEMA@STORED_PROJECT values (
        3,
	'NMAP',
        'http://www.nmap.org',
        'blah@nmap.org',
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/nmap',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @SCHEMA@STORED_PROJECT values (
        4,
	'SVN',
        'http://www.subversion.org',
        'blah@svn.org',
        'file:/dev/null',
        'file:///home/markos/REPOSITORIES/subversion',
        'maildir:../examples/mail/SQO-OSS'
) ;

insert into @SCHEMA@STORED_PROJECT values (
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

insert into @SCHEMA@MAILINGLIST values (1,'all',1);
insert into @SCHEMA@MAILINGLIST values (2,'wp1',1);
insert into @SCHEMA@MAILINGLIST values (3,'wp3',1);


-- This is the user database.

insert into @SCHEMA@USERS values (
	10,
	'Fluffy Bunny',
	'@TS=1970-1-1@',
	'@TS=2008-1-14@',
	'carrot',
	'bunny@kde.org'
	) ;

insert into @SCHEMA@USERS values (
	11,
	'Adriaan Bunny',
	'@TS=2008-1-1@',
	'@TS=2008-2-14@',
	'celery',
	'bunny@example.com'
	) ;


-- This is ade, for the yoyo project
insert into @SCHEMA@DEVELOPER values (
	163840,
	'Adriaan de Groot',
	'adriaan@codeyard.net',
	'adridg',1);

-- These are some early commits for the yoyo project
insert into @SCHEMA@PROJECT_VERSION values (196608, 1, 1, 1173867744, 163840, 'Create project', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196609, 1, 2, 1173868573, 163840, 'Dit project is /opnieuw/ aangemaakt' , NULL );
insert into @SCHEMA@PROJECT_VERSION values (196610, 1, 3, 1173871775, 163840, 'Instructies voor oude yoyo-ers' , NULL );
insert into @SCHEMA@PROJECT_VERSION values (196611, 1, 4, 1173872211, 163840, 'meer instructies ivm projectreset', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196612, 1, 5, 1173872288, 163840, 'html-bestendig', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196613, 1, 6, 1173874416, 163840, 'mijn eerste test op pleincollege bisschop bekkers', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196614, 1, 7, 1173885348, 163840, '', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196615, 1, 8, 1173885469, 163840, 'test toegevoegd', NULL );
insert into @SCHEMA@PROJECT_VERSION values (196616, 1, 9, 1173885485, 163840, '', NULL );

