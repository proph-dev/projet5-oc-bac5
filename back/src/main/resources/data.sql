INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Hélène', 'THIERCELIN');


INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES  ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq'),
        ('titi', 'toto', false, 'toto@gmail.com', '$2a$10$.o4wxVbCxwF2dcSsynQR0O6I.5H2CgdJ6I7NduJ3.3M3.MKkUlZc2'),
        ('tyty', 'tsts', false, 'tyty@gmail.com', '$2a$10$kH0s2azQ2roxXMD4mBNQ6eK/lr3snacu0fAjrRHuTGZOBuOsN1Mhe');

INSERT INTO SESSIONS (name, description, teacher_id, date)
VALUES  ('Session pour les nouveaux', 'Session pour les nouveaux', 1, '2023-12-01 01:00:00'),
        ('Session junior', 'Session junior', 2, '2023-12-01 01:00:00'),
        ('Session pro', 'session pour les pros', 2, '2023-12-01 01:00:00'),
        ('Session libre', 'session libre', 2, '2023-12-01 01:00:00');