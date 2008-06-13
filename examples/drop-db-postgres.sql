-- To avoid the situation that you need to be superuser to drop the
-- database, instead throw away all of the tables.

drop table bug_report_message;
drop table bug;
drop table group_user;
drop table group_privilege;
drop table groups cascade;
drop table mailmessage cascade;
drop table mailinglist cascade;
drop table project_file_measurement cascade;
drop table project_version_measurement cascade;
drop table metric cascade;
drop table invocation_rule;
drop table metric_type;
drop table plugin_configuration;
drop table plugin;
drop table privilege_value cascade;
drop table privilege cascade;
drop table project_file cascade;
drop table tag;
drop table stored_project_measurement;
drop table file_group_measurement;
drop table file_group;
drop table project_version;
drop table service_url cascade;
drop table stored_project cascade;
drop table users;
drop table evaluation_mark;
drop sequence hibernate_sequence;
drop table directory;
drop table pending_user;

-- Now go around again
drop table developer;
drop table group_type;

-- New metric DB tables
drop table code_unit_measurement;
drop table code_construct_type;
drop table productivity_actions;
drop table productivity_developer_actions;
drop table productivity_resource_type;
drop table productivity_resource_weight;
drop table productivity_weight_history;


-- Display remaining tables, just in case
--
\d

