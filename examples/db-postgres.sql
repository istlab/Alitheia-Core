--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: alitheia
--

SELECT pg_catalog.setval('hibernate_sequence', 1, true);


--
-- Data for Name: bug; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: bug_report_message; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: bug_reporter; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: commits; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: committer; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: feature; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: file_metadata; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: group_privilege; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: group_user; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: groups; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: mailinglist; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO mailinglist VALUES (1, 'all', 1);
INSERT INTO mailinglist VALUES (2, 'wp1', 1);
INSERT INTO mailinglist VALUES (3, 'wp3', 1);


--
-- Data for Name: mailmessage; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: measurement; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO measurement VALUES (1, 1, 1, '2004-10-19 10:23:54', 'SomeWord');


--
-- Data for Name: metric; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO metric VALUES (1, 1, 1, 'Profanity Count');


--
-- Data for Name: metric_type; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO metric_type VALUES (1, 'Counting Words');


--
-- Data for Name: plugin; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO plugin VALUES (1, 'Wc metric plug-in', '2008-02-13 12:33:50.713');


--
-- Data for Name: privilege; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: privilege_value; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: project_file; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: project_version; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO project_version VALUES (1, 1, 1);


--
-- Data for Name: sender; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: service_url; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: stored_project; Type: TABLE DATA; Schema: public; Owner: alitheia
--

INSERT INTO stored_project VALUES (1, 'SQO-OSS', 'http://www.sqo-oss.eu/', 'mailto:gousiosg@dmst.aueb.gr', 'file:/dev/null', 'http://cvs.codeyard.net/svn/yoyo/', 'maildir:../examples/mail/SQO-OSS');


--
-- Data for Name: tag; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: alitheia
--



--
-- PostgreSQL database dump complete
--

