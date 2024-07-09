--
-- Name: users; Type: TABLE; Schema: FMDB; Owner: -
--

DROP SCHEMA public;
CREATE SCHEMA FMDB;

CREATE TABLE FMDB.users (
    id integer NOT NULL,
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


CREATE TABLE FMDB.loans (
    id integer NOT NULL,
    userid integer NOT NULL,
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

COPY FMDB.users (id, username, first_name, last_name, email, password, create_dt, last_update_dt, last_update_by) FROM stdin;
1	admin	admin	istrator	admin@fm.com	$2a$10$S9nLk.BzkZuSPXvdn6JXoO0VX/tf8QNebc0ct8J39n.mU8Gzz.pPS	2023-11-13 00:00:00	2023-11-13 00:00:00 admin
\.

SELECT pg_catalog.setval('FMDB.users_id_seq', 2, true);
