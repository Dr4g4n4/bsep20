INSERT INTO authority(name) VALUES ('ROLE_ADMIN');
INSERT INTO admin (userN, password, first_login, enabled) VALUES ('admin', '$2a$10$btfhHvv9/VumsTS2ZNnKOudQWzAqNURShHLU1Z3fIsFnpym1dxd.G', true, true);
INSERT INTO user_authority (admin_id, authority_id) VALUES (1, 1);

