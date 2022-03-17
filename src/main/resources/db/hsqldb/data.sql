-- One admin user, named admin1 with password 4dm1n and authority admin
-- INSERT INTO users(username,password,enabled) VALUES ('admin','admin',TRUE);
-- INSERT INTO authorities(id,username,authority) VALUES (1,'admin','admin');

INSERT INTO clients VALUES (1, 'George', 'Franklin');
INSERT INTO clients VALUES (2, 'Barack', 'Obama');
INSERT INTO clients VALUES (3, 'Vladimir', 'Putin');
INSERT INTO clients VALUES (4, 'Donald', 'Trump');
INSERT INTO clients VALUES (5, 'Kim', 'Jong-Un');

-- Para probar el filtro
INSERT INTO plazas (id, precio_hora, direccion) VALUES (1, 2, 'Madrid centro');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (2, 1.2, 'Dos Hermanas, Sevilla');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (3, 0.5, 'Alcalá de Henares, Madrid');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (4, 5, 'Alcalá de Guadaira, Sevilla');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (5, 2, 'Sevilla centro');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (6, 3, 'C. Entre Arroyos, 28030 Madrid');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (7, 1.5, 'López de Gómara, 41010 Sevilla');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (8, 2, 'C. Carmen Amaya, 11, 28030 Madrid');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (9, 0.8, 'C. Juan Carvallo, 41006 Sevilla');
INSERT INTO plazas (id, precio_hora, direccion) VALUES (10, 0.5, 'Av. del Dr. García Tapia, 41, 28030 Madrid');

INSERT INTO horarios (id, fecha_inicio, fecha_fin, plaza_id) VALUES (1, '2022-04-01 04:00:00', '2022-04-01 05:00:00', 2);
INSERT INTO horarios (id, fecha_inicio, fecha_fin, plaza_id) VALUES (2, '2022-04-01 03:00:00', '2022-04-01 05:00:00', 1);
INSERT INTO horarios (id, fecha_inicio, fecha_fin, plaza_id) VALUES (3, '2022-04-01 04:00:00', '2022-04-01 06:00:00', 1);
---