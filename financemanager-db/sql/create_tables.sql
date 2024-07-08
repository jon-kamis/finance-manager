SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE SCHEMA FMDB;
SET search_path TO FMDB;

--
-- Name: users; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE TABLE users (
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

ALTER TABLE users ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


CREATE TABLE loans (
    id integer NOT NULL,
    userid integer NOT NULL,
    account_name character varying(255) NOT NULL,
    principal NUMERIC(10,2) NOT NULL,
    origination_dt timestamp,
    balance NUMERIC(10,2) NOT NULL,
    principal NUMERIC(10,2) NOT NULL,
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

ALTER TABLE loans ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME loan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

COPY users (id, username, first_name, last_name, email, password, create_dt, last_update_dt) FROM stdin;
1	admin	admin	istrator	admin@fm.com	$2a$10$S9nLk.BzkZuSPXvdn6JXoO0VX/tf8QNebc0ct8J39n.mU8Gzz.pPS	2023-11-13 00:00:00	2023-11-13 00:00:00
\.

SELECT pg_catalog.setval('users_id_seq', 2, true);
