-- Stessa tabella del backend vulnerabile: cambia la difesa, non i dati.
CREATE TABLE IF NOT EXISTS utenti (
    id       INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    ruolo    VARCHAR(20)
);
