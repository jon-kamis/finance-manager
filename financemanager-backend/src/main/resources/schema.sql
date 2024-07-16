--
-- Name: users; Type: TABLE; Schema: FMDB; Owner: -
--

DROP SCHEMA public;
CREATE SCHEMA FMDB;

CREATE SEQUENCE IF NOT EXISTS FMDB.users_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.users (
    id integer unique NOT NULL default nextval('FMDB.users_id_seq'),
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
-- Name: roles; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS FMDB.roles_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.roles (
    id integer unique NOT NULL default nextval('FMDB.roles_id_seq'),
    role_name character varying(255) unique NOT NULL,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

--
-- Name: user_roles; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS FMDB.user_roles_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.user_roles (
    id integer unique NOT NULL default nextval('FMDB.user_roles_id_seq'),
    user_id integer references FMDB.users(id),
    role_id integer references FMDB.roles(id),
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

CREATE SEQUENCE IF NOT EXISTS FMDB.loans_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.loans (
    id integer unique NOT NULL default nextval('FMDB.loans_id_seq'),
    user_id integer NOT NULL references FMDB.users(id),
    account_name character varying(255) NOT NULL,
    principal NUMERIC(10,2) NOT NULL,
    balance NUMERIC(10,2),
    first_payment_dt timestamp,
    frequency character varying(255) NOT NULL,
    interest NUMERIC(10,2),
    payment NUMERIC(10,2),
    rate NUMERIC(10,5),
    term integer not null,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

CREATE SEQUENCE IF NOT EXISTS FMDB.loan_payments_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.loan_payments (
    id integer unique NOT NULL default nextval('FMDB.loan_payments_id_seq'),
    loan_id integer NOT NULL references FMDB.loans(id),
    payment_number integer NOT NULL,
    payment_dt timestamp NOT NULL,
    principal NUMERIC(10,2) NOT NULL,
    principal_to_date NUMERIC(10,2) NOT NULL,
    interest NUMERIC(10,2) NOT NULL,
    interest_to_date NUMERIC(10,2) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    balance NUMERIC(10,2) NOT NULL,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);
