INSERT INTO authority(name) VALUES ('ROLE_ADMIN');
INSERT INTO admin (userN, password, first_login) VALUES ('admin', '$2a$10$btfhHvv9/VumsTS2ZNnKOudQWzAqNURShHLU1Z3fIsFnpym1dxd.G', true);
INSERT INTO user_authority (admin_id, authority_id) VALUES (1, 1);

INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('123456789', '2019-12-15', '2022-12-15', true, false, '123456789', 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'draganamihajlovic55@yahoo.com', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('459876238', '2019-08-15', '2029-08-15', true, false, '123456789', 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'smilefox@gmail.com', 'e');

INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('879532155', '2019-12-15', '2022-12-15', false, false, '123456789', 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'gaga@yahoo.com', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('684953271', '2019-08-15', '2029-08-15', false, false, '123456789', 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'gagic@gmail.com', 'e');

INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('158726789', '2019-12-15', '2022-12-15', true, false, '879532155', 'Potpisivanje sertifikata', 'Foca', 'Milica', 'Skipina', 'email1@gmail.com', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('358989126', '2019-08-15', '2029-08-15', true, false, '459876238', 'Digitalni potpis', 'Novi Sad', 'Ime', 'Prezime', 'drugimail@gmail.com', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('258941632', '2019-12-15', '2022-12-15', false, false, '684953271', 'Potpisivanje sertifikata', 'Futog', 'Ivana', 'Kovacevic', 'ivana@gmail.com', 'e');
INSERT INTO certificate (serial_number_subject, start_date, end_date, ca, revoked, serial_number_issuer, purpose, city, name, surname, email, extension ) VALUES ('165879562', '2019-08-15', '2029-08-15', false, false, '879532155', 'Digitalni potpis', 'Futog', 'Djordje', 'Petrovic', 'djordje@gmail.com', 'e');