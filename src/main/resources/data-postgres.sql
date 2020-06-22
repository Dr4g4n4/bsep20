INSERT INTO privilege(name) VALUES ('READ_CERTS');
INSERT INTO privilege(name) VALUES ('EDIT_CERTS');
INSERT INTO privilege(name) VALUES ('DOWNLOAD');
INSERT INTO privilege(name) VALUES ('WRITE_CERTS');

INSERT INTO authority(name) VALUES ('ROLE_ADMIN');

INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 1);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 2);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 3);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 4);

INSERT INTO admin (userN, password, first_login, enabled) VALUES ('admin', '$2a$10$btfhHvv9/VumsTS2ZNnKOudQWzAqNURShHLU1Z3fIsFnpym1dxd.G', true, true);
INSERT INTO user_authority (admin_id, authority_id) VALUES (1, 1);

