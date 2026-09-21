-- Utenti di esempio. Svuoto prima per evitare duplicati ai riavvii con devtools.
DELETE FROM utenti;
INSERT INTO utenti (username, password, ruolo) VALUES ('admin',    'S3gr3t0!',  'ADMIN');
INSERT INTO utenti (username, password, ruolo) VALUES ('gabriele', 'ciao123',   'USER');
INSERT INTO utenti (username, password, ruolo) VALUES ('mario',    'password',  'USER');
