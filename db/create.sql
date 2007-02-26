-- Table of projects that SQO-OSS examines.
--   id is the project id, used to cross-reference tools
--   name and url are the official name and URL of the project (eg. KDE and
--       http://www.kde.org/, so include the protocol in the URL)
--   path is the path in the filesystem RELATIVE to the SQO-OSS data store root
--       (eg. src/KDE, when SQO-OSS root is /usr/local).
--   generation is a monotone-increasing number indicating, roughly, how
--       often the SQO-OSS tools have run, these numbers can be used to
--       do versioning of the results of the tools.

create table projects ( 
	id serial PRIMARY KEY NOT NULL, 
	name varchar(200) NOT NULL,
	url varchar(200) NOT NULL,
	path varchar(500) NOT NULL,
	generation integer DEFAULT 0
) ;


-- Sample entries, from one of the project partners
insert into projects values ( 1,'KDE 3.5','http://www.kde.org/','KDE3' );
insert into projects values ( 2,'KDE 4','http://www.kde.org/','KDE4' );

