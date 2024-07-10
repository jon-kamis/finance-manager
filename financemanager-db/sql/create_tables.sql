--
-- Name: users; Type: TABLE; Schema: FMDB; Owner: -
--

DROP SCHEMA public;
CREATE SCHEMA FMDB;

CREATE TABLE FMDB.users (
    id integer unique NOT NULL,
    username character varying(255) NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    email character varying(255),
    password character varying(255),
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: FMDB; Owner: -
--

ALTER TABLE FMDB.users ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: roles; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE TABLE FMDB.roles (
    id integer unique NOT NULL,
    role_name character varying(255) unique NOT NULL,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);


--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: FMDB; Owner: -
--

ALTER TABLE FMDB.roles ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

--
-- Name: user_roles; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE TABLE FMDB.user_roles (
    id integer NOT NULL,
    user_id integer references FMDB.users(id),
    role_id integer references FMDB.roles(id),
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);


--
-- Name: user_roles_id_seq; Type: SEQUENCE; Schema: FMDB; Owner: -
--

ALTER TABLE FMDB.user_roles ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME user_roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


CREATE TABLE FMDB.loans (
    id integer NOT NULL,
    user_id integer NOT NULL,
    account_name character varying(255) NOT NULL,
    principal NUMERIC(10,2) NOT NULL,
    origination_dt timestamp,
    balance NUMERIC(10,2) NOT NULL,
    interest NUMERIC(10,2),
    payment NUMERIC(10,2),
    rate NUMERIC(10,5),
    term integer not null,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

--
-- Name: loan_id_seq; Type: SEQUENCE; Schema: FMDB; Owner: -
--

ALTER TABLE FMDB.loans ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME loan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

COPY FMDB.users (id, username, first_name, last_name, email, password, create_dt, last_update_dt, last_update_by) FROM stdin with delimiter as ',';
1,admin,admin,admin,admin@fm.com,$2a$10$S9nLk.BzkZuSPXvdn6JXoO0VX/tf8QNebc0ct8J39n.mU8Gzz.pPS,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
\.

COPY FMDB.roles (id, role_name, create_dt, last_update_dt, last_update_by) FROM stdin with delimiter as ',';
1,admin,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
2,user,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
\.

COPY FMDB.user_roles (id, user_id, role_id, create_dt, last_update_dt, last_update_by) FROM stdin with delimiter as ',';
1,1,1,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
2,1,2,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
\.

SELECT pg_catalog.setval('FMDB.users_id_seq', 2, true);
SELECT pg_catalog.setval('FMDB.roles_id_seq', 3, true);
SELECT pg_catalog.setval('FMDB.user_roles_id_seq', 3, true);
