--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: gousiosg
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

SELECT pg_catalog.setval('hibernate_sequence', 692, true);


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
    "type" character varying(255) NOT NULL
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
    value character varying(255),
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
-- Name: project_file_join; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_file_join (
    project_file_join_id bigint NOT NULL,
    project_file_id bigint NOT NULL,
    project_version_id bigint NOT NULL
);


ALTER TABLE public.project_file_join OWNER TO alitheia;

--
-- Name: project_file_measurement; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_file_measurement (
    project_file_measurement_id bigint NOT NULL,
    metric_id bigint NOT NULL,
    project_file_id bigint NOT NULL,
    when_run timestamp without time zone,
    result character varying(255)
);


ALTER TABLE public.project_file_measurement OWNER TO alitheia;

--
-- Name: project_version; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_version (
    project_version_id bigint NOT NULL,
    stored_project_id bigint NOT NULL,
    version bigint,
    "timestamp" bigint
);


ALTER TABLE public.project_version OWNER TO alitheia;

--
-- Name: project_version_measurement; Type: TABLE; Schema: public; Owner: alitheia; Tablespace: 
--

CREATE TABLE project_version_measurement (
    project_version_measurement_id bigint NOT NULL,
    metric_id bigint NOT NULL,
    project_version_id bigint NOT NULL,
    when_run timestamp without time zone,
    result character varying(255)
);


ALTER TABLE public.project_version_measurement OWNER TO alitheia;

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
    url character varying(255)
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
1	all	10
2	wp1	10
3	wp3	10
\.


--
-- Data for Name: mailmessage; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY mailmessage (id, sender_id, mlist_id, messageid, send_date, arrival_date, subject) FROM stdin;
\.


--
-- Data for Name: metric; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY metric (metric_id, plugin_id, metric_type, description) FROM stdin;
3	1	2	Line counting metric
\.


--
-- Data for Name: metric_type; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY metric_type (metric_type_id, "type") FROM stdin;
2	SOURCE_CODE
\.


--
-- Data for Name: plugin; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY plugin (plugin_id, name, install_date) FROM stdin;
1	Wc metric plug-in	2008-03-18 16:40:31.893
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
18	/.project	17	ADDED
20	/src/com/edsdev/jconvert/presentation/component/test/MyListCellRenderer.java	19	ADDED
21	/.classpath	19	ADDED
22	/test/com/edsdev/jconvert	19	ADDED
23	/src/com/edsdev/jconvert/presentation/component/NumericalTextField.java	19	ADDED
24	/build	19	ADDED
25	/test/com/edsdev/jconvert/test/ConversionTestCase.java	19	ADDED
26	/build/build.properties	19	ADDED
27	/test/com/edsdev/jconvert/test/TestList.java	19	ADDED
28	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	19	ADDED
29	/build/build.xml	19	ADDED
30	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	19	ADDED
31	/src/com/edsdev/jconvert/domain/Conversion.java	19	ADDED
32	/src/com/edsdev/jconvert/presentation/component/test/TestList.java	19	ADDED
33	/src/com/edsdev/jconvert/presentation/component/test/TestObject.java	19	ADDED
34	/resource	19	ADDED
35	/src/com	19	ADDED
36	/lib/junit-3.8.1.jar	19	ADDED
37	/resource/icon.bmp	19	ADDED
38	/src/com/edsdev/jconvert/presentation/component/ConvertListCellRenderer.java	19	ADDED
39	/src/com/edsdev/jconvert	19	ADDED
40	/resource/convert.dat	19	ADDED
41	/src/com/edsdev/jconvert/domain/ConversionType.java	19	ADDED
42	/src/com/edsdev	19	ADDED
43	/src/com/edsdev/jconvert/presentation/component/BaseTextField.java	19	ADDED
44	/src/com/edsdev/jconvert/persistence	19	ADDED
45	/src/com/edsdev/jconvert/util	19	ADDED
46	/lib	19	ADDED
47	/src/com/edsdev/jconvert/presentation/MainFrame.java	19	ADDED
48	/src/com/edsdev/jconvert/domain/DecimalConversion.java	19	ADDED
49	/resource/icon.jpg	19	ADDED
50	/src/com/edsdev/jconvert/presentation/ConversionTypeData.java	19	ADDED
51	/test/com	19	ADDED
52	/src/com/edsdev/jconvert/presentation/component	19	ADDED
53	/src/com/edsdev/jconvert/domain/Conversion_old.tmp	19	ADDED
54	/src/com/edsdev/jconvert/util/ResourceManager.java	19	ADDED
55	/src/com/edsdev/jconvert/common/exception	19	ADDED
56	/src/com/edsdev/jconvert/domain/FractionalConversion.java	19	ADDED
57	/src/com/edsdev/jconvert/presentation/component/test/MyListModel.java	19	ADDED
58	/src/com/edsdev/jconvert/presentation	19	ADDED
59	/src/com/edsdev/jconvert/common	19	ADDED
60	/src/com/edsdev/jconvert/presentation/component/test	19	ADDED
61	/test/com/edsdev/jconvert/test	19	ADDED
62	/src/com/edsdev/jconvert/persistence/DataLoader.java	19	ADDED
63	/test/com/edsdev	19	ADDED
64	/src	19	ADDED
65	/test	19	ADDED
66	/src/com/edsdev/jconvert/domain	19	ADDED
67	/src/com/edsdev/jconvert/presentation/component/ConvertListModel.java	19	ADDED
68	/src/com/edsdev/jconvert/logic	19	ADDED
70	/resource/convert.dat	69	MODIFIED
72	/src/com/edsdev/jconvert/util/Logger.java	71	ADDED
74	/src/com/edsdev/jconvert/util/Logger.java	73	MODIFIED
76	/src/com/edsdev/jconvert/util/Logger.java	75	MODIFIED
78	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	77	MODIFIED
80	/resource/convert.dat	79	MODIFIED
82	/test/convert_custom.dat	81	ADDED
83	/test/convert.dat	81	ADDED
85	/src/com/edsdev/jconvert/domain/Conversion.java	84	MODIFIED
87	/src/com/edsdev/jconvert/util/ResourceManager.java	86	MODIFIED
89	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	88	MODIFIED
91	/src/com/edsdev/jconvert/persistence/DataLoader.java	90	MODIFIED
93	/test/com/edsdev/jconvert/test/DataLoaderTestCase.java	92	ADDED
95	/src/com/edsdev/jconvert/domain/FractionalConversion.java	94	MODIFIED
97	/src/com/edsdev/jconvert/domain/ConversionType.java	96	MODIFIED
99	/build/build.xml	98	MODIFIED
101	/website/howto.html	100	ADDED
102	/website/style/purple.css	100	ADDED
103	/website	100	ADDED
104	/website/style/green.png	100	ADDED
105	/website/style/site_logo.jpg	100	ADDED
106	/website/style/orange_sbi.png	100	ADDED
107	/website/style/Copy of site_logo.jpg	100	ADDED
108	/website/style/orange.css	100	ADDED
109	/website/style/graphic.jpg	100	ADDED
110	/website/index_orange.html	100	ADDED
111	/website/images	100	ADDED
112	/website/style/blue_menu.png	100	ADDED
113	/website/style/style.css	100	ADDED
114	/website/index_green.html	100	ADDED
115	/website/index_purple.html	100	ADDED
116	/website/style/green_sbi.png	100	ADDED
117	/website/style/orange_menu.png	100	ADDED
118	/website/images/doswindow.jpg	100	ADDED
119	/website/style/green_menu.png	100	ADDED
120	/website/style/purple.png	100	ADDED
121	/website/style/blue.png	100	ADDED
122	/website/style/purple_menu.png	100	ADDED
123	/website/style	100	ADDED
124	/website/style/blue.css	100	ADDED
125	/website/images/download.jpg	100	ADDED
126	/website/style/main.png	100	ADDED
127	/website/index_blue.html	100	ADDED
128	/website/style/blue_sbi.png	100	ADDED
129	/website/style/purple_sbi.png	100	ADDED
130	/website/style/green.css	100	ADDED
131	/website/style/menu.png	100	ADDED
132	/website/style/link.png	100	ADDED
133	/website/index.html	100	ADDED
134	/website/style/orange.png	100	ADDED
135	/website/style/Copy of style.css	100	ADDED
137	/website/howto.html	136	MODIFIED
139	/resource/convert.dat	138	MODIFIED
141	/build/build.xml	140	MODIFIED
142	/build/build.properties	140	MODIFIED
144	/src/com/edsdev/jconvert/domain/FractionalConversion.java	143	MODIFIED
145	/src/com/edsdev/jconvert/domain/DecimalConversion.java	143	MODIFIED
146	/src/com/edsdev/jconvert/domain/Conversion.java	143	MODIFIED
148	/resource/convert.dat	147	MODIFIED
150	/build/build.xml	149	MODIFIED
152	/resource/convert.dat	151	MODIFIED
154	/test/com/edsdev/jconvert/test/DataLoaderTestCase.java	153	MODIFIED
156	/build/build.xml	155	MODIFIED
157	/build/build.properties	155	MODIFIED
159	/src/com/edsdev/jconvert/presentation/component/test	158	DELETED
161	/src/com/edsdev/jconvert/domain/Conversion_old.tmp	160	DELETED
163	/src/com/edsdev/jconvert/domain/Conversion.java	162	MODIFIED
165	/src/com/edsdev/jconvert/presentation/ConversionUnitData.java	164	ADDED
167	/src/com/edsdev/jconvert/presentation/component/ConvertListModel.java	166	MODIFIED
169	/src/com/edsdev/jconvert/presentation/component/ConvertListCellRenderer.java	168	REPLACED
171	/src/com/edsdev/jconvert/presentation/ConversionTypeData.java	170	MODIFIED
173	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	172	MODIFIED
175	/src/com/edsdev/jconvert/presentation/ConversionUnitData.java	174	MODIFIED
177	/website/style/blue.css	176	MODIFIED
178	/website/style/style.css	176	MODIFIED
180	/src/com/edsdev/jconvert/util/Browser.java	179	ADDED
182	/src/com/edsdev/jconvert/presentation/AboutDialog.java	181	ADDED
184	/resource/jconvert.properties	183	ADDED
186	/resource/convert.dat	185	MODIFIED
188	/build/build.xml	187	MODIFIED
190	/src/com/edsdev/jconvert/presentation/MainFrame.java	189	MODIFIED
192	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	191	MODIFIED
194	/build/build.xml	193	MODIFIED
196	/resource/jconvert.properties	195	MODIFIED
198	/src/com/edsdev/jconvert/presentation/ConversionsChangedListener.java	197	ADDED
199	/src/com/edsdev/jconvert/presentation/AddCustomConversionDlg.java	197	ADDED
201	/build/build.xml	200	MODIFIED
202	/resource/jconvert.properties	200	MODIFIED
204	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	203	MODIFIED
206	/src/com/edsdev/jconvert/presentation/AboutDialog.java	205	MODIFIED
208	/src/com/edsdev/jconvert/presentation/MainFrame.java	207	MODIFIED
210	/resource/jconvert.properties	209	MODIFIED
212	/website/howto.html	211	MODIFIED
214	/.classpath	213	MODIFIED
216	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	215	ADDED
217	/src/com/edsdev/jconvert/util/JConvertProperties.java	215	ADDED
219	/src/com/edsdev/jconvert/util/ResourceManager.java	218	MODIFIED
221	/src/com/edsdev/jconvert/presentation/AboutDialog.java	220	MODIFIED
223	/src/com/edsdev/jconvert/presentation/AddCustomConversionDlg.java	222	MODIFIED
225	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	224	MODIFIED
227	/src/com/edsdev/jconvert/presentation/MainFrame.java	226	MODIFIED
229	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	228	MODIFIED
231	/src/com/edsdev/jconvert/util/ResourceManager.java	230	MODIFIED
233	/resource/jconvert.properties	232	MODIFIED
235	/resource/jcMessages.properties	234	ADDED
237	/src/com/edsdev/jconvert/util/Messages.java	236	ADDED
239	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	238	MODIFIED
241	/test/convert.dat	240	MODIFIED
243	/website/style/blue.css	242	MODIFIED
245	/website/howto.html	244	MODIFIED
247	/src/com/edsdev/jconvert/util/Messages.java	246	MODIFIED
249	/src/com/edsdev/jconvert/presentation/component/NumericalTextField.java	248	MODIFIED
251	/src/com/edsdev/jconvert/domain/FractionalConversion.java	250	MODIFIED
253	/build/releaseNotes.txt	252	ADDED
255	/resource/convert.dat	254	MODIFIED
257	/resource/jcMessages.properties	256	MODIFIED
259	/build/releaseNotes.txt	258	MODIFIED
261	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	260	MODIFIED
263	/test/com/edsdev/jconvert/test/ConversionTestCase.java	262	MODIFIED
265	/test/com/edsdev/jconvert/test/DataLoaderTestCase.java	264	MODIFIED
267	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	266	MODIFIED
269	/src/com/edsdev/jconvert/presentation/component/ConvertListCellRenderer.java	268	MODIFIED
271	/src/com/edsdev/jconvert/presentation/AboutDialog.java	270	MODIFIED
273	/src/com/edsdev/jconvert/presentation/AddCustomConversionDlg.java	272	MODIFIED
275	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	274	MODIFIED
277	/src/com/edsdev/jconvert/presentation/ConversionUnitData.java	276	MODIFIED
279	/src/com/edsdev/jconvert/presentation/MainFrame.java	278	MODIFIED
281	/resource/jconvert.properties	280	MODIFIED
283	/resource/convert.dat	282	MODIFIED
285	/resource/jcMessages.properties	284	MODIFIED
287	/src/com/edsdev/jconvert/presentation/MainFrame.java	286	MODIFIED
289	/test/convert.dat	288	MODIFIED
291	/resource/jcMessages_es.properties	290	ADDED
292	/resource/jcMessages_fr.properties	290	ADDED
294	/build/releaseNotes.txt	293	MODIFIED
296	/resource/convert.dat	295	MODIFIED
298	/resource/jcMessages_es.properties	297	MODIFIED
300	/src/com/edsdev/jconvert/presentation/MainFrame.java	299	MODIFIED
302	/build/releaseNotes.txt	301	MODIFIED
304	/resource/convert.dat	303	MODIFIED
306	/resource/jcMessages.properties	305	MODIFIED
308	/resource/jconvert.properties	307	MODIFIED
310	/src/com/edsdev/jconvert/persistence/DataLoader.java	309	MODIFIED
312	/website/howto.html	311	MODIFIED
313	/website/index.html	311	MODIFIED
315	/build/build.xml	314	MODIFIED
317	/resource/jcMessages_ru.properties	316	ADDED
319	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	318	MODIFIED
321	/src/com/edsdev/jconvert/persistence/DataLoader.java	320	MODIFIED
323	/src/com/edsdev/jconvert/presentation/AboutDialog.java	322	MODIFIED
325	/src/com/edsdev/jconvert/presentation/AddCustomConversionDlg.java	324	MODIFIED
327	/src/com/edsdev/jconvert/presentation/ConversionPanel.java	326	MODIFIED
329	/src/com/edsdev/jconvert/presentation/ConversionTypeData.java	328	MODIFIED
331	/src/com/edsdev/jconvert/presentation/SettingsDlg.java	330	ADDED
333	/src/com/edsdev/jconvert/presentation/UpgradeVersionChecker.java	332	ADDED
335	/src/com/edsdev/jconvert/presentation/component/VisibleTabsListCellRenderer.java	334	ADDED
337	/src/com/edsdev/jconvert/util/ByteBuffer.java	336	ADDED
338	/src/com/edsdev/jconvert/util/Http.java	336	ADDED
340	/src/com/edsdev/jconvert/util/JConvertProperties.java	339	MODIFIED
342	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	341	MODIFIED
344	/test/com/edsdev/jconvert/test/DataLoaderTestCase.java	343	MODIFIED
345	/test/com/edsdev/jconvert/test/ConversionTestCase.java	343	MODIFIED
347	/website/index.html	346	MODIFIED
349	/src/com/edsdev/jconvert/util/Messages.java	348	MODIFIED
351	/src/com/edsdev/jconvert/presentation/UpgradeVersionChecker.java	350	MODIFIED
353	/src/com/edsdev/jconvert/presentation/MainFrame.java	352	MODIFIED
355	/resource/jcMessages_ru.properties	354	MODIFIED
356	/resource/jcMessages_es.properties	354	MODIFIED
357	/resource/jcMessages_fr.properties	354	MODIFIED
359	/resource/jcMessages.properties	358	MODIFIED
361	/src/com/edsdev/jconvert/presentation/UpgradeVersionChecker.java	360	MODIFIED
363	/build/build.xml	362	MODIFIED
365	/build/build.xml	364	MODIFIED
367	/website/howto.html	366	MODIFIED
368	/website/index.html	366	MODIFIED
370	/resource/jcMessages.properties	369	MODIFIED
371	/resource/jconvert.properties	369	MODIFIED
372	/resource/jcMessages_ru.properties	369	MODIFIED
373	/resource/jcMessages_es.properties	369	MODIFIED
374	/resource/jcMessages_fr.properties	369	MODIFIED
376	/src/com/edsdev/jconvert/presentation/SettingsDlg.java	375	MODIFIED
378	/build/releaseNotes.txt	377	MODIFIED
380	/resource/jconvert.properties	379	MODIFIED
382	/build/build.xml	381	MODIFIED
383	/build/build.properties	381	MODIFIED
385	/src/com/edsdev/jconvert/domain/FractionalConversion.java	384	MODIFIED
386	/src/com/edsdev/jconvert/domain/DecimalConversion.java	384	MODIFIED
388	/src/com/edsdev/jconvert/persistence/DataLoader.java	387	MODIFIED
390	/src/com/edsdev/jconvert/presentation/ConversionsChangedListener.java	389	MODIFIED
391	/src/com/edsdev/jconvert/presentation/ConversionUnitData.java	389	MODIFIED
392	/src/com/edsdev/jconvert/presentation/MainFrame.java	389	MODIFIED
393	/src/com/edsdev/jconvert/presentation/AboutDialog.java	389	MODIFIED
395	/src/com/edsdev/jconvert/presentation/component/NumericalTextField.java	394	MODIFIED
396	/src/com/edsdev/jconvert/presentation/component/ConvertListCellRenderer.java	394	MODIFIED
397	/src/com/edsdev/jconvert/presentation/component/ConvertListModel.java	394	MODIFIED
398	/src/com/edsdev/jconvert/presentation/component/BaseTextField.java	394	MODIFIED
400	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	399	MODIFIED
402	/src/com/edsdev/jconvert/util/Http.java	401	MODIFIED
403	/src/com/edsdev/jconvert/util/Browser.java	401	MODIFIED
404	/src/com/edsdev/jconvert/util/Messages.java	401	MODIFIED
405	/src/com/edsdev/jconvert/util/ResourceManager.java	401	MODIFIED
406	/src/com/edsdev/jconvert/util/JConvertProperties.java	401	MODIFIED
408	/test/com/edsdev/jconvert/test/TestList.java	407	MODIFIED
409	/test/com/edsdev/jconvert/test/DataLoaderTestCase.java	407	MODIFIED
410	/test/com/edsdev/jconvert/test/ConversionTestCase.java	407	MODIFIED
412	/website/howto.html	411	MODIFIED
414	/build/build.properties	413	MODIFIED
416	/test/com/edsdev/jconvert/test/InternationalizeTestCase.java	415	ADDED
418	/build/build.xml	417	MODIFIED
420	/src/com/edsdev/jconvert/util/JConvertSettingsProperties.java	419	MODIFIED
422	/src/com/edsdev/jconvert/util/Logger.java	421	MODIFIED
424	/src/com/edsdev/jconvert/logic/ConversionGapBuilder.java	423	MODIFIED
426	/website/index.html	425	MODIFIED
\.


--
-- Data for Name: project_file_join; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_file_join (project_file_join_id, project_file_id, project_version_id) FROM stdin;
\.


--
-- Data for Name: project_file_measurement; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_file_measurement (project_file_measurement_id, metric_id, project_file_id, when_run, result) FROM stdin;
427	3	18	2008-03-18 16:43:38.153	17
428	3	20	2008-03-18 16:43:38.213	65
429	3	53	2008-03-18 16:43:38.264	324
430	3	59	2008-03-18 16:43:38.289	0
431	3	80	2008-03-18 16:43:38.466	4311
432	3	97	2008-03-18 16:43:38.506	55
433	3	113	2008-03-18 16:43:38.536	267
434	3	127	2008-03-18 16:43:38.562	158
435	3	132	2008-03-18 16:43:38.594	8
436	3	146	2008-03-18 16:43:38.626	390
437	3	163	2008-03-18 16:43:38.651	400
438	3	188	2008-03-18 16:43:38.668	108
439	3	201	2008-03-18 16:43:38.695	120
440	3	202	2008-03-18 16:43:38.724	6
441	3	217	2008-03-18 16:43:38.762	62
442	3	235	2008-03-18 16:43:38.786	317
443	3	253	2008-03-18 16:43:38.805	24
444	3	271	2008-03-18 16:43:38.836	117
445	3	283	2008-03-18 16:43:38.969	4824
446	3	296	2008-03-18 16:43:39.094	4824
447	3	323	2008-03-18 16:43:39.127	113
448	3	344	2008-03-18 16:43:39.151	124
449	3	359	2008-03-18 16:43:39.174	344
450	3	371	2008-03-18 16:43:39.201	6
451	3	380	2008-03-18 16:43:39.239	6
452	3	392	2008-03-18 16:43:39.281	365
453	3	403	2008-03-18 16:43:39.316	49
454	3	418	2008-03-18 16:43:39.326	151
455	3	426	2008-03-18 16:43:39.36	135
456	3	424	2008-03-18 16:43:39.356	132
457	3	422	2008-03-18 16:43:39.403	148
458	3	420	2008-03-18 16:43:39.407	223
459	3	416	2008-03-18 16:43:39.435	52
460	3	414	2008-03-18 16:43:39.456	27
461	3	412	2008-03-18 16:43:39.464	170
462	3	410	2008-03-18 16:43:39.49	129
463	3	409	2008-03-18 16:43:39.51	127
464	3	408	2008-03-18 16:43:39.528	46
465	3	406	2008-03-18 16:43:39.538	66
466	3	405	2008-03-18 16:43:39.57	160
467	3	404	2008-03-18 16:43:39.572	90
468	3	402	2008-03-18 16:43:39.605	296
469	3	400	2008-03-18 16:43:39.621	199
470	3	398	2008-03-18 16:43:39.645	172
471	3	397	2008-03-18 16:43:39.656	53
472	3	396	2008-03-18 16:43:39.685	59
473	3	395	2008-03-18 16:43:39.71	83
474	3	393	2008-03-18 16:43:39.716	113
475	3	391	2008-03-18 16:43:39.74	106
476	3	390	2008-03-18 16:43:39.763	8
477	3	388	2008-03-18 16:43:39.787	422
478	3	386	2008-03-18 16:43:39.802	125
479	3	385	2008-03-18 16:43:39.822	190
480	3	383	2008-03-18 16:43:39.843	26
481	3	382	2008-03-18 16:43:39.862	150
482	3	378	2008-03-18 16:43:39.873	45
483	3	374	2008-03-18 16:43:39.905	349
484	3	376	2008-03-18 16:43:39.919	266
485	3	373	2008-03-18 16:43:39.941	349
486	3	372	2008-03-18 16:43:39.956	349
487	3	370	2008-03-18 16:43:39.985	358
488	3	368	2008-03-18 16:43:40.006	125
489	3	367	2008-03-18 16:43:40.014	152
490	3	363	2008-03-18 16:43:40.042	150
491	3	365	2008-03-18 16:43:40.043	149
492	3	361	2008-03-18 16:43:40.07	159
493	3	357	2008-03-18 16:43:40.088	324
494	3	356	2008-03-18 16:43:40.104	324
495	3	355	2008-03-18 16:43:40.129	324
496	3	353	2008-03-18 16:43:40.145	361
497	3	351	2008-03-18 16:43:40.174	156
498	3	349	2008-03-18 16:43:40.277	91
499	3	347	2008-03-18 16:43:40.298	125
500	3	345	2008-03-18 16:43:40.324	126
501	3	342	2008-03-18 16:43:40.338	180
502	3	340	2008-03-18 16:43:40.363	66
503	3	338	2008-03-18 16:43:40.377	294
504	3	337	2008-03-18 16:43:40.407	57
505	3	335	2008-03-18 16:43:40.426	88
506	3	333	2008-03-18 16:43:40.444	130
507	3	331	2008-03-18 16:43:40.461	266
508	3	329	2008-03-18 16:43:40.492	97
509	3	327	2008-03-18 16:43:40.511	292
510	3	325	2008-03-18 16:43:40.533	310
511	3	321	2008-03-18 16:43:40.559	422
512	3	319	2008-03-18 16:43:40.568	135
514	3	315	2008-03-18 16:43:40.606	147
513	3	317	2008-03-18 16:43:40.604	315
515	3	312	2008-03-18 16:43:40.642	152
516	3	313	2008-03-18 16:43:40.652	125
517	3	308	2008-03-18 16:43:40.683	6
518	3	310	2008-03-18 16:43:40.685	421
519	3	306	2008-03-18 16:43:40.715	338
520	3	302	2008-03-18 16:43:40.847	39
521	3	304	2008-03-18 16:43:40.857	4824
522	3	300	2008-03-18 16:43:40.887	319
523	3	298	2008-03-18 16:43:40.889	314
524	3	294	2008-03-18 16:43:40.92	36
525	3	292	2008-03-18 16:43:40.932	314
526	3	291	2008-03-18 16:43:40.968	314
527	3	289	2008-03-18 16:43:41.009	4403
528	3	287	2008-03-18 16:43:41.02	314
529	3	285	2008-03-18 16:43:41.046	326
530	3	281	2008-03-18 16:43:41.059	6
531	3	279	2008-03-18 16:43:41.08	315
532	3	277	2008-03-18 16:43:41.094	106
533	3	275	2008-03-18 16:43:41.127	276
534	3	273	2008-03-18 16:43:41.142	315
535	3	269	2008-03-18 16:43:41.16	54
536	3	267	2008-03-18 16:43:41.188	170
537	3	265	2008-03-18 16:43:41.201	125
538	3	263	2008-03-18 16:43:41.227	128
539	3	261	2008-03-18 16:43:41.242	177
540	3	259	2008-03-18 16:43:41.27	25
541	3	257	2008-03-18 16:43:41.299	326
542	3	255	2008-03-18 16:43:41.416	4369
543	3	251	2008-03-18 16:43:41.437	180
544	3	249	2008-03-18 16:43:41.462	80
545	3	247	2008-03-18 16:43:41.476	87
546	3	245	2008-03-18 16:43:41.506	144
547	3	243	2008-03-18 16:43:41.51	68
548	3	241	2008-03-18 16:43:41.535	252
549	3	239	2008-03-18 16:43:41.543	139
550	3	233	2008-03-18 16:43:41.577	6
551	3	237	2008-03-18 16:43:41.587	75
552	3	231	2008-03-18 16:43:41.62	158
553	3	229	2008-03-18 16:43:41.631	109
554	3	225	2008-03-18 16:43:41.675	228
555	3	227	2008-03-18 16:43:41.675	217
556	3	223	2008-03-18 16:43:41.708	313
557	3	221	2008-03-18 16:43:41.731	103
558	3	219	2008-03-18 16:43:41.753	160
559	3	216	2008-03-18 16:43:41.775	109
560	3	214	2008-03-18 16:43:41.789	9
561	3	212	2008-03-18 16:43:41.817	131
562	3	210	2008-03-18 16:43:41.837	6
563	3	208	2008-03-18 16:43:41.857	160
564	3	206	2008-03-18 16:43:41.881	114
565	3	204	2008-03-18 16:43:41.90	177
566	3	199	2008-03-18 16:43:41.934	298
567	3	198	2008-03-18 16:43:41.944	8
568	3	196	2008-03-18 16:43:41.977	6
569	3	194	2008-03-18 16:43:41.982	120
570	3	192	2008-03-18 16:43:42.013	206
571	3	190	2008-03-18 16:43:42.022	130
572	3	184	2008-03-18 16:43:42.065	5
573	3	186	2008-03-18 16:43:42.069	4369
574	3	182	2008-03-18 16:43:42.101	108
575	3	180	2008-03-18 16:43:42.108	49
576	3	178	2008-03-18 16:43:42.138	267
577	3	177	2008-03-18 16:43:42.141	68
578	3	175	2008-03-18 16:43:42.176	80
579	3	173	2008-03-18 16:43:42.196	203
580	3	171	2008-03-18 16:43:42.221	87
581	3	169	2008-03-18 16:43:42.246	58
582	3	167	2008-03-18 16:43:42.262	50
583	3	165	2008-03-18 16:43:42.283	79
584	3	157	2008-03-18 16:43:42.31	13
585	3	156	2008-03-18 16:43:42.408	106
586	3	154	2008-03-18 16:43:42.441	121
587	3	152	2008-03-18 16:43:42.469	4357
588	3	150	2008-03-18 16:43:42.588	105
589	3	148	2008-03-18 16:43:42.623	4357
590	3	145	2008-03-18 16:43:42.644	112
591	3	144	2008-03-18 16:43:42.66	180
592	3	142	2008-03-18 16:43:42.694	12
593	3	141	2008-03-18 16:43:42.704	103
594	3	137	2008-03-18 16:43:42.753	125
595	3	139	2008-03-18 16:43:42.766	4339
596	3	135	2008-03-18 16:43:42.787	258
597	3	134	2008-03-18 16:43:42.797	9
598	3	133	2008-03-18 16:43:42.818	117
599	3	131	2008-03-18 16:43:42.839	6
600	3	130	2008-03-18 16:43:42.85	68
601	3	128	2008-03-18 16:43:42.875	8
602	3	129	2008-03-18 16:43:42.897	8
603	3	126	2008-03-18 16:43:42.923	10
604	3	125	2008-03-18 16:43:42.934	167
605	3	123	2008-03-18 16:43:42.962	0
606	3	124	2008-03-18 16:43:42.974	68
607	3	121	2008-03-18 16:43:43.004	9
608	3	122	2008-03-18 16:43:43.004	8
609	3	120	2008-03-18 16:43:43.036	9
610	3	119	2008-03-18 16:43:43.055	6
611	3	118	2008-03-18 16:43:43.072	142
612	3	117	2008-03-18 16:43:43.094	6
613	3	116	2008-03-18 16:43:43.117	9
614	3	115	2008-03-18 16:43:43.128	158
615	3	114	2008-03-18 16:43:43.161	158
616	3	112	2008-03-18 16:43:43.164	9
617	3	111	2008-03-18 16:43:43.20	0
618	3	110	2008-03-18 16:43:43.204	158
619	3	109	2008-03-18 16:43:43.237	24
620	3	108	2008-03-18 16:43:43.252	68
621	3	107	2008-03-18 16:43:43.279	137
622	3	106	2008-03-18 16:43:43.284	4
623	3	105	2008-03-18 16:43:43.314	120
624	3	104	2008-03-18 16:43:43.318	9
625	3	103	2008-03-18 16:43:43.348	0
626	3	102	2008-03-18 16:43:43.365	68
627	3	101	2008-03-18 16:43:43.396	125
628	3	99	2008-03-18 16:43:43.407	96
629	3	95	2008-03-18 16:43:43.53	180
630	3	93	2008-03-18 16:43:43.543	120
631	3	91	2008-03-18 16:43:43.585	277
632	3	89	2008-03-18 16:43:43.599	173
633	3	87	2008-03-18 16:43:43.627	93
634	3	85	2008-03-18 16:43:43.633	390
635	3	83	2008-03-18 16:43:43.66	234
636	3	82	2008-03-18 16:43:43.673	12
637	3	78	2008-03-18 16:43:43.705	177
638	3	76	2008-03-18 16:43:43.742	126
639	3	74	2008-03-18 16:43:43.774	131
640	3	72	2008-03-18 16:43:43.776	126
641	3	68	2008-03-18 16:43:43.828	0
642	3	70	2008-03-18 16:43:43.84	4311
643	3	66	2008-03-18 16:43:43.881	0
644	3	67	2008-03-18 16:43:43.886	46
645	3	65	2008-03-18 16:43:43.917	0
646	3	64	2008-03-18 16:43:43.922	0
647	3	63	2008-03-18 16:43:43.958	0
648	3	62	2008-03-18 16:43:43.975	193
649	3	61	2008-03-18 16:43:43.995	0
650	3	60	2008-03-18 16:43:44.014	0
651	3	58	2008-03-18 16:43:44.037	0
652	3	57	2008-03-18 16:43:44.059	46
653	3	56	2008-03-18 16:43:44.083	117
654	3	55	2008-03-18 16:43:44.105	0
655	3	54	2008-03-18 16:43:44.129	93
656	3	52	2008-03-18 16:43:44.137	0
658	3	50	2008-03-18 16:43:44.176	86
657	3	51	2008-03-18 16:43:44.175	0
659	3	49	2008-03-18 16:43:44.218	10
660	3	48	2008-03-18 16:43:44.232	112
661	3	46	2008-03-18 16:43:44.264	0
662	3	47	2008-03-18 16:43:44.264	84
663	3	45	2008-03-18 16:43:44.295	0
664	3	44	2008-03-18 16:43:44.316	0
665	3	43	2008-03-18 16:43:44.344	173
666	3	42	2008-03-18 16:43:44.372	0
667	3	41	2008-03-18 16:43:44.375	43
668	3	39	2008-03-18 16:43:44.409	0
669	3	38	2008-03-18 16:43:44.429	66
670	3	37	2008-03-18 16:43:44.442	7
671	3	35	2008-03-18 16:43:44.473	0
672	3	36	2008-03-18 16:43:44.477	822
673	3	34	2008-03-18 16:43:44.515	0
674	3	32	2008-03-18 16:43:44.543	65
675	3	33	2008-03-18 16:43:44.548	23
676	3	31	2008-03-18 16:43:44.589	376
677	3	30	2008-03-18 16:43:44.60	178
678	3	29	2008-03-18 16:43:44.622	95
679	3	28	2008-03-18 16:43:44.643	141
680	3	27	2008-03-18 16:43:44.662	43
681	3	26	2008-03-18 16:43:44.685	11
682	3	25	2008-03-18 16:43:44.712	125
683	3	24	2008-03-18 16:43:44.72	0
685	3	22	2008-03-18 16:43:44.848	0
684	3	23	2008-03-18 16:43:44.754	54
686	3	21	2008-03-18 16:43:44.875	9
\.


--
-- Data for Name: project_version; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_version (project_version_id, stored_project_id, version, "timestamp") FROM stdin;
17	10	1	1184808175
19	10	2	1184808391
69	10	3	1184896508
71	10	4	1185846431
73	10	5	1185846677
75	10	6	1185847966
77	10	7	1185848876
79	10	8	1186094655
81	10	9	1186099467
84	10	10	1186099690
86	10	11	1186099749
88	10	12	1186099850
90	10	13	1186100112
92	10	14	1186100665
94	10	15	1186100717
96	10	16	1186100754
98	10	17	1186100792
100	10	18	1186359884
136	10	19	1186456532
138	10	20	1186533534
140	10	21	1186534227
143	10	22	1186534451
147	10	23	1186539311
149	10	24	1186539363
151	10	25	1186539734
153	10	26	1186539766
155	10	27	1186541493
158	10	28	1187132084
160	10	29	1187132114
162	10	30	1187312221
164	10	31	1187312265
166	10	32	1187312330
168	10	33	1187314249
170	10	34	1187314462
172	10	35	1187314507
174	10	36	1187317190
176	10	37	1187318289
179	10	38	1189559459
181	10	39	1189559512
183	10	40	1189559564
185	10	41	1189559982
187	10	42	1189560017
189	10	43	1189560079
191	10	44	1189560131
193	10	45	1190072009
195	10	46	1190072044
197	10	47	1190072120
200	10	48	1190075661
203	10	49	1190076131
205	10	50	1190076190
207	10	51	1190076332
209	10	52	1190078474
211	10	53	1190414559
213	10	54	1190414581
215	10	55	1190416870
218	10	56	1190417036
220	10	57	1190417082
222	10	58	1190417179
224	10	59	1190417229
226	10	60	1190417273
228	10	61	1190418800
230	10	62	1190418827
232	10	63	1190425775
234	10	64	1190943025
236	10	65	1190943083
238	10	66	1190943510
240	10	67	1191540219
242	10	68	1191540300
244	10	69	1191540352
246	10	70	1191540408
248	10	71	1191540508
250	10	72	1191540547
252	10	73	1191540573
254	10	74	1191541180
256	10	75	1191541270
258	10	76	1191635764
260	10	77	1191635868
262	10	78	1191635917
264	10	79	1191635939
266	10	80	1191636005
268	10	81	1191636092
270	10	82	1191636167
272	10	83	1191636218
274	10	84	1191636290
276	10	85	1191636327
278	10	86	1191636423
280	10	87	1192237781
282	10	88	1192238274
284	10	89	1192238353
286	10	90	1192238414
288	10	91	1192238442
290	10	92	1192238469
293	10	93	1192238494
295	10	94	1192752013
297	10	95	1192752113
299	10	96	1192752205
301	10	97	1193356827
303	10	98	1193357165
305	10	99	1193357202
307	10	100	1193357332
309	10	101	1193357483
311	10	102	1193357846
314	10	103	1194050464
316	10	104	1194051211
318	10	105	1194051355
320	10	106	1194051425
322	10	107	1194051466
324	10	108	1194051607
326	10	109	1194051697
328	10	110	1194051838
330	10	111	1194052205
332	10	112	1194052234
334	10	113	1194052274
336	10	114	1194052326
339	10	115	1194052356
341	10	116	1194052388
343	10	117	1194052430
346	10	118	1194052451
348	10	119	1194126336
350	10	120	1194126454
352	10	121	1194127288
354	10	122	1194130602
358	10	123	1194131618
360	10	124	1194131644
362	10	125	1194304793
364	10	126	1194305327
366	10	127	1194309082
369	10	128	1194309798
375	10	129	1194309826
377	10	130	1194309886
379	10	131	1194311367
381	10	132	1194482213
384	10	133	1194482312
387	10	134	1194482357
389	10	135	1194482416
394	10	136	1194482478
399	10	137	1194482577
401	10	138	1194482635
407	10	139	1194482672
411	10	140	1194482760
413	10	141	1194915549
415	10	142	1194915583
417	10	143	1194915616
419	10	144	1194916469
421	10	145	1194916815
423	10	146	1194916856
425	10	147	1196483247
\.


--
-- Data for Name: project_version_measurement; Type: TABLE DATA; Schema: public; Owner: alitheia
--

COPY project_version_measurement (project_version_measurement_id, metric_id, project_version_id, when_run, result) FROM stdin;
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
10	JConvert	http://sourceforge.net/projects/jconvert	http://sourceforge.net/projects/jconvert	bts:/Volumes/Files/Desktop/JConvert/bugs	file:///Volumes/Files/Desktop/JConvert/svn	maildir:/Volumes/Files/Desktop/JConvert/mail
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
1	'Fluffy Bunny'	0	1970-01-01 00:00:00	2008-01-14 00:00:00	carrot	bunny@kde.org
2	'Adriaan Bunny'	0	2008-01-01 00:00:00	2008-02-14 00:00:00	celery	bynny@example.com
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
-- Name: project_file_join_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_file_join
    ADD CONSTRAINT project_file_join_pkey PRIMARY KEY (project_file_join_id);


--
-- Name: project_file_measurement_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_file_measurement
    ADD CONSTRAINT project_file_measurement_pkey PRIMARY KEY (project_file_measurement_id);


--
-- Name: project_file_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_file
    ADD CONSTRAINT project_file_pkey PRIMARY KEY (project_file_id);


--
-- Name: project_version_measurement_pkey; Type: CONSTRAINT; Schema: public; Owner: alitheia; Tablespace: 
--

ALTER TABLE ONLY project_version_measurement
    ADD CONSTRAINT project_version_measurement_pkey PRIMARY KEY (project_version_measurement_id);


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
-- Name: fk694e504f1b42e47f; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_version_measurement
    ADD CONSTRAINT fk694e504f1b42e47f FOREIGN KEY (metric_id) REFERENCES metric(metric_id);


--
-- Name: fk694e504ff6aebe0c; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_version_measurement
    ADD CONSTRAINT fk694e504ff6aebe0c FOREIGN KEY (project_version_id) REFERENCES project_version(project_version_id);


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
-- Name: fka31cdc4779452628; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_file_join
    ADD CONSTRAINT fka31cdc4779452628 FOREIGN KEY (project_file_id) REFERENCES project_file(project_file_id);


--
-- Name: fka31cdc47f6aebe0c; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_file_join
    ADD CONSTRAINT fka31cdc47f6aebe0c FOREIGN KEY (project_version_id) REFERENCES project_version(project_version_id);


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
-- Name: fkf0c89ddf1b42e47f; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_file_measurement
    ADD CONSTRAINT fkf0c89ddf1b42e47f FOREIGN KEY (metric_id) REFERENCES metric(metric_id);


--
-- Name: fkf0c89ddf79452628; Type: FK CONSTRAINT; Schema: public; Owner: alitheia
--

ALTER TABLE ONLY project_file_measurement
    ADD CONSTRAINT fkf0c89ddf79452628 FOREIGN KEY (project_file_id) REFERENCES project_file(project_file_id);


--
-- Name: public; Type: ACL; Schema: -; Owner: gousiosg
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM gousiosg;
GRANT ALL ON SCHEMA public TO gousiosg;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

