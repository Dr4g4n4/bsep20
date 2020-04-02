INSERT INTO authority(name) VALUES ('ROLE_ADMIN');
INSERT INTO admin (userN, password, first_login) VALUES ('admin', '$2a$10$btfhHvv9/VumsTS2ZNnKOudQWzAqNURShHLU1Z3fIsFnpym1dxd.G', true);
INSERT INTO user_authority (admin_id, authority_id) VALUES (1, 1);

INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, uid, extension ) VALUES ('123456789', '2019-12-15', '2022-12-15', true, false, '123456789', 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'draganamihajlovic55@yahoo.com', '16091997', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, uid, extension ) VALUES ('459876238', '2019-08-15', '2029-08-15', true, false, '123456789', 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'smilefox@gmail.com', '1997', 'e');

INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, uid, extension ) VALUES ('879532155', '2019-12-15', '2022-12-15', false, false, '123456789', 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'gaga@yahoo.com', '16091997', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, uid, extension ) VALUES ('684953271', '2019-08-15', '2029-08-15', false, false, '123456789', 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'gagic@gmail.com', '1997', 'e');