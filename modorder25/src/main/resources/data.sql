INSERT INTO roles (name, description)
SELECT  'ROLE_USER' , 'Utente Base'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');

INSERT INTO roles (name, description)
SELECT  'ROLE_ADMIN' , 'Utente Admin'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

INSERT INTO roles (name, description)
SELECT  'ROLE_MANAGER' , 'Utente Manager'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_MANAGER');

INSERT INTO roles (name, description)
SELECT  'ROLE_DEVELOP' , 'Utente Developer'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_DEVELOP');

INSERT INTO users(active, created_at, email, password, updated_at, username, permessi)
SELECT true	, '2024-12-10 16:42:11', 'mario@mario.com', '$2a$10$4twk9mJlipxPFxQP7f/Vf.21tSVGr/DhcuP7OOWwYPZ1HvyGc/Nha', '2024-12-10 16:42:11','err',31
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'err');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id  FROM users u, roles r 
WHERE u.username = 'mario' AND r.name = 'ROLE_DEVELOP'  AND 
NOT EXISTS ( SELECT 1  FROM user_roles ur  WHERE ur.user_id = u.id  );

INSERT INTO Tipi_permessi (codice, descrizione, valore)
SELECT 'GRP001', 'Gruppo 1', 1
WHERE NOT EXISTS (SELECT 1 FROM Tipi_permessi WHERE codice = 'GRP001');
INSERT INTO Tipi_permessi (codice, descrizione, valore)
SELECT 'GRP002', 'Gruppo 2', 2
WHERE NOT EXISTS (SELECT 1 FROM Tipi_permessi WHERE codice = 'GRP002');
INSERT INTO Tipi_permessi (codice, descrizione, valore)
SELECT 'GRP003', 'Gruppo 3', 4
WHERE NOT EXISTS (SELECT 1 FROM Tipi_permessi WHERE codice = 'GRP003');
INSERT INTO Tipi_permessi (codice, descrizione, valore)
SELECT 'GRP004', 'Gruppo 4', 8
WHERE NOT EXISTS (SELECT 1 FROM Tipi_permessi WHERE codice = 'GRP004');
INSERT INTO Tipi_permessi (codice, descrizione, valore)
SELECT 'GRP005', 'Gruppo 5', 16
WHERE NOT EXISTS (SELECT 1 FROM Tipi_permessi WHERE codice = 'GRP005');

