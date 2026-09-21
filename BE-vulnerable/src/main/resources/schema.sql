-- Tabella utenti per la demo di SQL Injection sul login.
-- NB: le password sono in chiaro solo per rendere leggibile la demo; in produzione vanno sempre cifrate (bcrypt).
CREATE TABLE IF NOT EXISTS utenti (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    ruolo    VARCHAR(20)
);
