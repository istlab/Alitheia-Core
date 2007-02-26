-- Table of projects that SQO-OSS examines.
--   id is the project id, used to cross-reference tools
--   name and website are the official name and URL of the project (eg. KDE and
--       http://www.kde.org/, so include the protocol in the URL)
--   contactPoint is ...
--   srcPath is the path in the filesystem RELATIVE to the SQO-OSS data store 
--       (eg. src/KDE, when SQO-OSS root is /usr/local).
--   mailPath is ...

create table Project ( 
	id serial UNIQUE PRIMARY KEY NOT NULL, 
	name varchar(200) NOT NULL,
	website varchar(200) NOT NULL,
	contactPoint varchar(200) NOT NULL,
	srcPath varchar(500) NOT NULL,
	mailPath varchar(500) NOT NULL
) ;


-- Sample entries, from one of the project partners
insert into Project values ( 1,'KDE','http://www.kde.org/','','src/KDE/','' );


-- Version numbers of existing projects
create table ProjectVersion (
	id serial UNIQUE NOT NULL,
	projectId integer UNIQUE PRIMARY KEY NOT NULL,
	version varchar(50) NOT NULL,
	FOREIGN KEY (projectId) REFERENCES Project (id)
) ;

-- Sample entries
insert into ProjectVersion values ( 1,1,'3.5.5' );
insert into ProjectVersion values ( 2,1,'4.0pre2' );
insert into ProjectVersion values ( 3,1,'3.5.6' );



-- Types of metrics
-- 
-- A type of metric is ...
create table MetricType (
	id serial UNIQUE NOT NULL,
	type varchar(200)
) ;

insert into MetricType values ( 1, 'Defect Count' );
insert into MetricType values ( 2, 'Steaminess (percent)' );

-- The metrics themselves.
create table Metric (
	id serial UNIQUE PRIMARY KEY NOT NULL,
	metricTypeId integer NOT NULL,
	description varchar(1000),
	FOREIGN KEY (metricTypeId) REFERENCES MetricType (id)
) ;

insert into Metric values ( 1, 1, 'Presence of bad str* functions.' );
insert into Metric values ( 2, 1, 'SLOC' );
insert into Metric values ( 3, 2, 'ln(Cyclomatic Complexity)' );
insert into Metric values ( 4, 1, 'File count' );

-- Plugins are executables
create table Plugin (
	id serial UNIQUE NOT NULL,
	metricId integer NOT NULL,
	path varchar(500) NOT NULL,
	cmd varchar(200) NOT NULL,
	parameters varchar(1000),
	FOREIGN KEY (metricId) REFERENCES Metric (id)
) ;

insert into Plugin values ( 1, 2, '/usr/bin', 'wc', '-l %file' );

-- Measurements are from a single metric applied to a particular version
create table Measurement (
	id serial UNIQUE NOT NULL,
	projectVersionId integer NOT NULL,
	metricId integer NOT NULL,
	result integer NOT NULL DEFAULT 0,
	whenRun timestamp NOT NULL DEFAULT now,
	FOREIGN KEY (projectVersionId) REFERENCES ProjectVersion (id),
	FOREIGN KEY (metricId) REFERENCES Metric (id)
) ;

insert into Measurement values ( 1, 2, 4, 1688, 1172533405 );

