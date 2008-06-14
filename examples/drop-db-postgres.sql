-- To avoid the situation that you need to be superuser to drop the
-- database, instead throw away all of the tables.
--
-- You can either sed s/ACTION/drop table/ or sed s/ACTION/delete from/
-- to drop or clear the database.

ACTION bug_report_message;
ACTION bug;
ACTION group_user;
ACTION group_privilege;
ACTION groups;
ACTION mailmessage;
ACTION mailinglist;
ACTION project_file_measurement;
ACTION project_version_measurement;
ACTION metric;
ACTION invocation_rule;
ACTION metric_type;
ACTION plugin_configuration;
ACTION plugin;
ACTION privilege_value;
ACTION privilege;
ACTION project_file;
ACTION tag;
ACTION stored_project_measurement;
ACTION file_group_measurement;
ACTION file_group;
ACTION project_version;
ACTION service_url;
ACTION developer;
ACTION stored_project;
ACTION users;
ACTION evaluation_mark;
ACTION directory;
ACTION pending_user;

-- Now go around again
ACTION group_type;

-- New metric DB tables
ACTION code_unit_measurement;
ACTION code_construct_type;
ACTION productivity_actions;
ACTION productivity_developer_actions;
ACTION productivity_resource_type;
ACTION productivity_resource_weight;
ACTION productivity_weight_history;


-- Hibernate administration
drop sequence hibernate_sequence;

-- Display remaining tables, just in case
--
\d

