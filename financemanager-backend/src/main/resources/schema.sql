--
-- Name: users; Type: TABLE; Schema: FMDB; Owner: -
--

DROP SCHEMA public;
CREATE SCHEMA FMDB;

CREATE SEQUENCE IF NOT EXISTS FMDB.users_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.users (
    id integer unique NOT NULL default nextval('FMDB.users_id_seq'),
    username character varying(255) unique NOT NULL,
    first_name character varying(255),
    last_name character varying(255),
    email character varying(255),
    password character varying(255),
    state character varying(255) NOT NULL,
    local_tax_rate NUMERIC(10,4) NOT NULL default 0,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

--
-- Name: refresh_tokens; Type: TABLE; Schema: FMDB; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS FMDB.refresh_tokens_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.refresh_tokens (
    id integer unique NOT NULL default nextval('FMDB.refresh_tokens_id_seq'),
    token uuid unique NOT NULL,
    username character varying(255) unique NOT NULL,
    expiration_dt timestamp without time zone
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
    first_payment_dt timestamp NOT NULL,
    frequency character varying(255) NOT NULL,
    interest NUMERIC(10,2),
    payment NUMERIC(10,2),
    rate NUMERIC(10,5) NOT NULL,
    term integer NOT NULL,
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

CREATE SEQUENCE IF NOT EXISTS FMDB.transactions_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.transactions (
    id integer unique NOT NULL default nextval('FMDB.transactions_id_seq'),
    user_id integer NOT NULL references FMDB.users(id),
    transaction_name character varying(255) NOT NULL,
    transaction_type character varying(255) NOT NULL,
    category character varying(255) NOT NULL,
    frequency character varying(255) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    effective_dt timestamp without time zone,
    expiration_dt timestamp without time zone,
    parent_table_name character varying(255),
    parent_id integer,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

CREATE SEQUENCE IF NOT EXISTS FMDB.transaction_days_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.transaction_days (
    id integer unique NOT NULL default nextval('FMDB.transaction_days_id_seq'),
    transaction_id integer NOT NULL references FMDB.transactions(id),
    day integer,
    weekday character varying(255),
    start_date timestamp without time zone,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

CREATE SEQUENCE IF NOT EXISTS FMDB.incomes_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.incomes (
    id integer unique NOT NULL default nextval('FMDB.incomes_id_seq'),
    user_id integer NOT NULL references FMDB.users(id),
    income_name character varying(255) NOT NULL,
    withheld_tax NUMERIC(10,2) NOT NULL default 0,
    tax_credits integer NOT NULL default 0,
    frequency character varying(255) NOT NULL,
    category character varying(255) NOT NULL,
    filing_type character varying(255) NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    taxable boolean NOT NULL default 'true',
    effective_dt timestamp without time zone NOT NULL,
    expiration_dt timestamp without time zone,
    create_dt timestamp without time zone,
    last_update_dt timestamp without time zone,
    last_update_by character varying(255)
);

CREATE SEQUENCE IF NOT EXISTS FMDB.standard_withholdings_id_seq;
CREATE TABLE IF NOT EXISTS FMDB.standard_withholdings (
    id integer unique NOT NULL default nextval('FMDB.standard_withholdings_id_seq'),
    filing_type character varying(255) NOT NULL,
    min NUMERIC(10,2) NOT NULL,
    max NUMERIC(10,2),
    amount NUMERIC(10,2) NOT NULL,
    percentage NUMERIC(10,2) NOT NULL
);