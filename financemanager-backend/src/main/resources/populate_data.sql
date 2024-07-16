COPY FMDB.users (id, username, first_name, last_name, email, password, create_dt, last_update_dt, last_update_by) FROM stdin with delimiter as ',';
1,admin,admin,admin,admin@fm.com,$2a$10$QpsNZZqtHP6mnvDTjb6iWeAwD/oTVZCLwoss.s7siB6dtn9/gMJOa,2023-11-13 00:00:00,2023-11-13 00:00:00,admin
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