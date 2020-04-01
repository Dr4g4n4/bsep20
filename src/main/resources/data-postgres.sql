INSERT INTO authority(name) VALUES ('ROLE_ADMIN');
INSERT INTO admin (userN, password, first_login) VALUES ('admin', '$2a$10$lQF1FjXTI68iTJDo86eusO59W04bhd9LAKF8oFy.i2MjBPvVnLHV6', true);
INSERT INTO user_authority (admin_id, authority_id) VALUES (1, 1);

INSERT INTO certificate (id_subject, start_date, end_date, ca, revoked, id_issuer, purpose, city, name, surname, email, uid ) VALUES (0, '2019-12-15', '2022-12-15', true, false, 111, 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'draganamihajlovic55@yahoo.com', '16091997');
INSERT INTO certificate (id_subject, start_date, end_date, ca, revoked, id_issuer, purpose, city, name, surname, email, uid ) VALUES (1, '2019-08-15', '2029-08-15', true, false, 111, 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'smilefox@gmail.com', '1997');

INSERT INTO certificate (id_subject, start_date, end_date, ca, revoked, id_issuer, purpose, city, name, surname, email, uid ) VALUES (2, '2019-12-15', '2022-12-15', false, false, 111, 'Potpisivanje sertifikata', 'Foca', 'Dragana', 'Mihajlovic', 'gaga@yahoo.com', '16091997');
INSERT INTO certificate (id_subject, start_date, end_date, ca, revoked, id_issuer, purpose, city, name, surname, email, uid ) VALUES (3, '2019-08-15', '2029-08-15', false, false, 111, 'Digitalni potpis', 'Orahovo', 'Smile', 'Fox', 'gagic@gmail.com', '1997');