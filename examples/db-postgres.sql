--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: bug; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE bug (
    bug_id bigint NOT NULL,
    commit_id bigint,
    description character varying(255),
    properties bytea
);


ALTER TABLE public.bug OWNER TO alitheia;

--
-- Name: bug_report_message; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE bug_report_message (
    bug_report_message_id bigint NOT NULL,
    bug_id bigint NOT NULL,
    bug_reporter_id bigint NOT NULL,
    description character varying(255),
    "action" character varying(255)
);


ALTER TABLE public.bug_report_message OWNER TO alitheia;

--
-- Name: bug_reporter; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE bug_reporter (
    bug_reporter_id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.bug_reporter OWNER TO alitheia;

--
-- Name: commits; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE commits (
    commit_id bigint NOT NULL,
    commit_time time without time zone,
    committer bigint,
    project_version bigint,
    properties character varying(255)
);


ALTER TABLE public.commits OWNER TO alitheia;

--
-- Name: committer; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE committer (
    committer_id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE public.committer OWNER TO alitheia;

--
-- Name: feature; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE feature (
    feature_id bigint NOT NULL,
    feature_name character varying(255)
);


ALTER TABLE public.feature OWNER TO alitheia;

--
-- Name: file_metadata; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE file_metadata (
    metadata_id bigint NOT NULL,
    protection_mode character varying(255),
    number_of_links integer,
    user_id bigint,
    group_id bigint,
    access_time time without time zone,
    modification_time time without time zone,
    status_change character varying(255),
    file_size integer,
    file_blocks integer,
    project_file bigint
);


ALTER TABLE public.file_metadata OWNER TO alitheia;

--
-- Name: group_privilege; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE group_privilege (
    service_url_id bigint NOT NULL,
    group_id bigint NOT NULL,
    privilege_value_id bigint NOT NULL
);


ALTER TABLE public.group_privilege OWNER TO alitheia;

--
-- Name: group_user; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE group_user (
    user_id bigint NOT NULL,
    group_id bigint NOT NULL
);


ALTER TABLE public.group_user OWNER TO alitheia;

--
-- Name: groups; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE groups (
    group_id bigint NOT NULL,
    description character varying(255)
);


ALTER TABLE public.groups OWNER TO alitheia;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: alitheia
--

CREATE SEQUENCE hibernate_sequence
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO alitheia;

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: alitheia
--

SELECT pg_catalog.setval('hibernate_sequence', 1, true);


--
-- Name: mailinglist; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE mailinglist (
    mlist_id bigint NOT NULL,
    mlist_listid character varying(255) NOT NULL,
    project_id bigint
);


ALTER TABLE public.mailinglist OWNER TO alitheia;

--
-- Name: mailmessage; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE mailmessage (
    id bigint NOT NULL,
    sender_id bigint NOT NULL,
    mlist_id bigint NOT NULL,
    messageid character varying(255) NOT NULL,
    send_date timestamp without time zone,
    arrival_date timestamp without time zone,
    subject character varying(255)
);


ALTER TABLE public.mailmessage OWNER TO alitheia;

--
-- Name: measurement; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE measurement (
    measurement_id bigint NOT NULL,
    metric bigint NOT NULL,
    project_version_id bigint NOT NULL,
    when_run timestamp without time zone,
    result character varying(255)
);


ALTER TABLE public.measurement OWNER TO alitheia;

--
-- Name: metric; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE metric (
    metric_id bigint NOT NULL,
    plugin_id bigint NOT NULL,
    metric_type bigint,
    description character varying(255) NOT NULL
);


ALTER TABLE public.metric OWNER TO alitheia;

--
-- Name: metric_type; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE metric_type (
    metric_type_id bigint NOT NULL,
    "type" bytea NOT NULL
);


ALTER TABLE public.metric_type OWNER TO alitheia;

--
-- Name: plugin; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE plugin (
    plugin_id bigint NOT NULL,
    name character varying(255),
    install_date timestamp without time zone
);


ALTER TABLE public.plugin OWNER TO alitheia;

--
-- Name: privilege; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE privilege (
    privilege_id bigint NOT NULL,
    description character varying(255)
);


ALTER TABLE public.privilege OWNER TO alitheia;

--
-- Name: privilege_value; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE privilege_value (
    privilege_value_id bigint NOT NULL,
    value integer,
    privilege_id bigint NOT NULL
);


ALTER TABLE public.privilege_value OWNER TO alitheia;

--
-- Name: project_file; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_file (
    project_file_id bigint NOT NULL,
    file_name character varying(255),
    project_version_id bigint NOT NULL,
    file_status character varying(255)
);


ALTER TABLE public.project_file OWNER TO alitheia;

--
-- Name: project_version; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_version (
    project_version_id bigint NOT NULL,
    stored_project_id bigint NOT NULL,
    version bigint,
    timestamp bigint
);


ALTER TABLE public.project_version OWNER TO alitheia;

--
-- Name: sender; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE sender (
    sender_id bigint NOT NULL,
    sender_email character varying(255)
);


ALTER TABLE public.sender OWNER TO alitheia;

--
-- Name: service_url; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE service_url (
    service_url_id bigint NOT NULL,
    url bytea
);


ALTER TABLE public.service_url OWNER TO alitheia;

--
-- Name: stored_project; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE stored_project (
    project_id bigint NOT NULL,
    project_name character varying(255),
    website_url character varying(255),
    contact_url character varying(255),
    bts_url character varying(255),
    scm_url character varying(255),
    mail_url character varying(255)
);


ALTER TABLE public.stored_project OWNER TO alitheia;

--
-- Name: tag; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE tag (
    tag_id bigint NOT NULL,
    tag_name character varying(255)
);


ALTER TABLE public.tag OWNER TO alitheia;

--
-- Name: users; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE users (
    user_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    "level" integer,
    registered timestamp without time zone,
    lastlogin timestamp without time zone,
    "password" character varying(255) NOT NULL,
    email character varying(255) NOT NULL
);


ALTER TABLE public.users OWNER TO alitheia;

--
-- Data for Name: bug; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY bug (bug_id, commit_id, description, properties) FROM stdin;
\.


--
-- Data for Name: bug_report_message; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY bug_report_message (bug_report_message_id, bug_id, bug_reporter_id, description, "action") FROM stdin;
\.


--
-- Data for Name: bug_reporter; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY bug_reporter (bug_reporter_id, name) FROM stdin;
\.


--
-- Data for Name: commits; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY commits (commit_id, commit_time, committer, project_version, properties) FROM stdin;
\.


--
-- Data for Name: committer; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY committer (committer_id, name) FROM stdin;
\.


--
-- Data for Name: feature; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY feature (feature_id, feature_name) FROM stdin;
\.


--
-- Data for Name: file_metadata; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY file_metadata (metadata_id, protection_mode, number_of_links, user_id, group_id, access_time, modification_time, status_change, file_size, file_blocks, project_file) FROM stdin;
\.


--
-- Data for Name: group_privilege; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY group_privilege (service_url_id, group_id, privilege_value_id) FROM stdin;
\.


--
-- Data for Name: group_user; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY group_user (user_id, group_id) FROM stdin;
\.


--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY groups (group_id, description) FROM stdin;
\.


--
-- Data for Name: mailinglist; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY mailinglist (mlist_id, mlist_listid, project_id) FROM stdin;
1	all	1
2	wp1	1
3	wp3	1
\.


--
-- Data for Name: mailmessage; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY mailmessage (id, sender_id, mlist_id, messageid, send_date, arrival_date, subject) FROM stdin;
\.


--
-- Data for Name: measurement; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY measurement (measurement_id, metric, project_version_id, when_run, result) FROM stdin;
1	1	1	2004-10-19 10:23:54	SomeWord
\.


--
-- Data for Name: metric; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY metric (metric_id, plugin_id, metric_type, description) FROM stdin;
1	1	1	Profanity Count
\.


--
-- Data for Name: metric_type; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY metric_type (metric_type_id, "type") FROM stdin;
1	Counting Words
\.


--
-- Data for Name: plugin; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY plugin (plugin_id, name, install_date) FROM stdin;
1	Wc metric plug-in	2008-02-13 12:33:50.713
\.


--
-- Data for Name: privilege; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY privilege (privilege_id, description) FROM stdin;
\.


--
-- Data for Name: privilege_value; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY privilege_value (privilege_value_id, value, privilege_id) FROM stdin;
\.


--
-- Data for Name: project_file; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_file (project_file_id, file_name, project_version_id, file_status) FROM stdin;
\.


--
-- Data for Name: project_version; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_version (project_version_id, stored_project_id, version, timestamp) FROM stdin;
1	1	1	1111111111111
\.


--
-- Data for Name: sender; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY sender (sender_id, sender_email) FROM stdin;
\.


--
-- Data for Name: service_url; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY service_url (service_url_id, url) FROM stdin;
\.


--
-- Data for Name: stored_project; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY stored_project (project_id, project_name, website_url, contact_url, bts_url, scm_url, mail_url) FROM stdin;
1	SQO-OSS	http://www.sqo-oss.eu/	mailto:gousiosg@dmst.aueb.gr	file:/dev/null	http://cvs.codeyard.net/svn/yoyo/	maildir:../examples/mail/SQO-OSS
\.


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY tag (tag_id, tag_name) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY users (user_id, name, "level", registered, lastlogin, "password", email) FROM stdin;
\.


--
-- Name: bug_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY bug
    ADD CONSTRAINT bug_pkey PRIMARY KEY (bug_id);


--
-- Name: bug_report_message_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY bug_report_message
    ADD CONSTRAINT bug_report_message_pkey PRIMARY KEY (bug_report_message_id);


--
-- Name: bug_reporter_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY bug_reporter
    ADD CONSTRAINT bug_reporter_pkey PRIMARY KEY (bug_reporter_id);


--
-- Name: commits_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY commits
    ADD CONSTRAINT commits_pkey PRIMARY KEY (commit_id);


--
-- Name: committer_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY committer
    ADD CONSTRAINT committer_pkey PRIMARY KEY (committer_id);


--
-- Name: feature_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY feature
    ADD CONSTRAINT feature_pkey PRIMARY KEY (feature_id);


--
-- Name: file_metadata_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY file_metadata
    ADD CONSTRAINT file_metadata_pkey PRIMARY KEY (metadata_id);


--
-- Name: group_privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT group_privilege_pkey PRIMARY KEY (service_url_id, group_id, privilege_value_id);


--
-- Name: group_user_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY group_user
    ADD CONSTRAINT group_user_pkey PRIMARY KEY (user_id, group_id);


--
-- Name: groups_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY groups
    ADD CONSTRAINT groups_pkey PRIMARY KEY (group_id);


--
-- Name: mailinglist_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY mailinglist
    ADD CONSTRAINT mailinglist_pkey PRIMARY KEY (mlist_id);


--
-- Name: mailmessage_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY mailmessage
    ADD CONSTRAINT mailmessage_pkey PRIMARY KEY (id);


--
-- Name: measurement_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY measurement
    ADD CONSTRAINT measurement_pkey PRIMARY KEY (measurement_id);


--
-- Name: metric_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY metric
    ADD CONSTRAINT metric_pkey PRIMARY KEY (metric_id);


--
-- Name: metric_type_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY metric_type
    ADD CONSTRAINT metric_type_pkey PRIMARY KEY (metric_type_id);


--
-- Name: plugin_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY plugin
    ADD CONSTRAINT plugin_pkey PRIMARY KEY (plugin_id);


--
-- Name: privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY privilege
    ADD CONSTRAINT privilege_pkey PRIMARY KEY (privilege_id);


--
-- Name: privilege_value_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY privilege_value
    ADD CONSTRAINT privilege_value_pkey PRIMARY KEY (privilege_value_id);


--
-- Name: project_file_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_file
    ADD CONSTRAINT project_file_pkey PRIMARY KEY (project_file_id);


--
-- Name: project_version_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_version
    ADD CONSTRAINT project_version_pkey PRIMARY KEY (project_version_id);


--
-- Name: sender_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY sender
    ADD CONSTRAINT sender_pkey PRIMARY KEY (sender_id);


--
-- Name: service_url_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY service_url
    ADD CONSTRAINT service_url_pkey PRIMARY KEY (service_url_id);


--
-- Name: stored_project_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY stored_project
    ADD CONSTRAINT stored_project_pkey PRIMARY KEY (project_id);


--
-- Name: tag_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY tag
    ADD CONSTRAINT tag_pkey PRIMARY KEY (tag_id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- Name: fk10254d40f631f; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY bug
    ADD CONSTRAINT fk10254d40f631f FOREIGN KEY (commit_id) REFERENCES commits(commit_id);


--
-- Name: fk14466f9cf6aebe0c; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY measurement
    ADD CONSTRAINT fk14466f9cf6aebe0c FOREIGN KEY (project_version_id) REFERENCES project_version(project_version_id);


--
-- Name: fk14466f9cfd3d7c25; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY measurement
    ADD CONSTRAINT fk14466f9cfd3d7c25 FOREIGN KEY (metric) REFERENCES metric(metric_id);


--
-- Name: fk30970127897a1a95; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY bug_report_message
    ADD CONSTRAINT fk30970127897a1a95 FOREIGN KEY (bug_id) REFERENCES bug(bug_id);


--
-- Name: fk30970127da059be; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY bug_report_message
    ADD CONSTRAINT fk30970127da059be FOREIGN KEY (bug_reporter_id) REFERENCES bug_reporter(bug_reporter_id);


--
-- Name: fk51830250499cda5f; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY mailmessage
    ADD CONSTRAINT fk51830250499cda5f FOREIGN KEY (sender_id) REFERENCES sender(sender_id);


--
-- Name: fk51830250c4e33bb3; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY mailmessage
    ADD CONSTRAINT fk51830250c4e33bb3 FOREIGN KEY (mlist_id) REFERENCES mailinglist(mlist_id);


--
-- Name: fk6b1ec1ab8b362ff5; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY group_user
    ADD CONSTRAINT fk6b1ec1ab8b362ff5 FOREIGN KEY (group_id) REFERENCES groups(group_id);


--
-- Name: fk6b1ec1ab8f6ad7df; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY group_user
    ADD CONSTRAINT fk6b1ec1ab8f6ad7df FOREIGN KEY (user_id) REFERENCES users(user_id);


--
-- Name: fk8758e9b0a94ea7b8; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY metric
    ADD CONSTRAINT fk8758e9b0a94ea7b8 FOREIGN KEY (metric_type) REFERENCES metric_type(metric_type_id);


--
-- Name: fk8758e9b0d6dfd19f; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY metric
    ADD CONSTRAINT fk8758e9b0d6dfd19f FOREIGN KEY (plugin_id) REFERENCES plugin(plugin_id);


--
-- Name: fk8c9989c96a3f8d92; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY mailinglist
    ADD CONSTRAINT fk8c9989c96a3f8d92 FOREIGN KEY (project_id) REFERENCES stored_project(project_id);


--
-- Name: fkb095122390642535; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY privilege_value
    ADD CONSTRAINT fkb095122390642535 FOREIGN KEY (privilege_id) REFERENCES privilege(privilege_id);


--
-- Name: fkc3dc4302f6aebe0c; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_file
    ADD CONSTRAINT fkc3dc4302f6aebe0c FOREIGN KEY (project_version_id) REFERENCES project_version(project_version_id);


--
-- Name: fkc5061d72c3822bae; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_version
    ADD CONSTRAINT fkc5061d72c3822bae FOREIGN KEY (stored_project_id) REFERENCES stored_project(project_id);


--
-- Name: fke41bd4b16149e774; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT fke41bd4b16149e774 FOREIGN KEY (service_url_id) REFERENCES service_url(service_url_id);


--
-- Name: fke41bd4b17b45fd7c; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT fke41bd4b17b45fd7c FOREIGN KEY (privilege_value_id) REFERENCES privilege_value(privilege_value_id);


--
-- Name: fke41bd4b18b362ff5; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY group_privilege
    ADD CONSTRAINT fke41bd4b18b362ff5 FOREIGN KEY (group_id) REFERENCES groups(group_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

