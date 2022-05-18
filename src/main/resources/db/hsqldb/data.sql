-- One admin user, named admin1 with password 4dm1n and authority admin
-- INSERT INTO users(username,password,enabled) VALUES ('admin','admin',TRUE);
-- INSERT INTO authorities(id,username,authority) VALUES (1,'admin','admin');

INSERT INTO roles(id, name) values (1, 'ROLE_ADMIN');
INSERT INTO roles(id, name) values (2, 'ROLE_USER');
INSERT INTO roles(id, name) values (3, 'ROLE_BANNED');
-- La contraseña es Admin1234

insert into clients(id, name, email, password, phone, surname) values (1, 'Admin', 'admin@admin.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666998877, 'admin');
insert into clients(id, name, email, password, phone, surname) values (2, 'User1', 'user1@admin.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666998871, 'user');
insert into clients(id, name, email, password, phone, surname) values (3, 'User2', 'user2@admin.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666998872, 'user');
insert into clients(id, name, email, password, phone, surname) values (4, 'User3', 'user3@admin.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666998873, 'user');
insert into clients(id, name, email, password, phone, surname) values (5, 'Paco', 'paco@gmail.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666777888, 'Fernández');
insert into clients(id, name, email, password, phone, surname) values (6, 'Juan', 'juan@gmail.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666777889, 'García');


insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (1,1);  
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (1,2);  
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (2,2);  
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (3,2);  
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (4,2);  
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (5,2); 
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (6,2); 
---
insert into comision(id, porcentaje) values (6,0.1);
--Plazas y reservas
insert into plazas(id, direccion, precio_hora, fianza, ancho, largo, latitud, longitud, esta_disponible, es_Aire_Libre, descripcion, user_id, tramos) values 
(1,'Calle Bami,4,Sevilla,Sevilla,41003',5,15.0,3,5,37.360814,-5.979376, true,true,'',3, false);

insert into plazas(id, direccion, precio_hora, fianza, ancho, largo, latitud, longitud, esta_disponible, es_Aire_Libre, descripcion, user_id, tramos) values 
(4,'Calle Tejares,3,Sevilla,Sevilla,41005',2.5,45.0,2.5,4.5,37.382226,-6.012347, true,true,'',5,false);


insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(1,1101,2,0,200,TO_TIMESTAMP('2030-07-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-07-03 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),
TO_TIMESTAMP('2022-05-17 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),'',50,'Calle Bami 4',1,6,3);

insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(2,1102,2,0,200,TO_TIMESTAMP('2030-07-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-07-03 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),
TO_TIMESTAMP('2022-05-17 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),'',50,'Calle Bami 4',1,6,3);


insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(5,1000,5,0,200,TO_TIMESTAMP('2030-07-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-07-03 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),
TO_TIMESTAMP('2022-05-17 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),'',50,'Calle Tejares 3',4,6,6);

insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(7,1001,5,0,200,TO_TIMESTAMP('2030-08-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-08-03 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),
TO_TIMESTAMP('2022-05-17 06:14:00.742000000', 'YYYY-MM-DD HH24:MI:SS.FF'),'',50,'Calle Tejares 3',4,6,6);

insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(8,1002,5,1,200,TO_TIMESTAMP('2030-09-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-09-03 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),
TO_TIMESTAMP('2022-05-17 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),'',50,'Calle Tejares 3',4,6,6);

insert into reservas(id, paypal_order_id,propietario_id,estado, precio_total, fecha_inicio, fecha_fin, fecha_solicitud, comentarios, fianza, direccion,plaza_id,comision,user_id) values 
(9,1003,5,1,200,TO_TIMESTAMP('2030-10-02 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),TO_TIMESTAMP('2030-10-03 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),
TO_TIMESTAMP('2022-05-17 06:14:00', 'YYYY-MM-DD HH24:MI:SS'),'',50,'Calle Tejares 3',4,6,6);


insert into incidencias(id, estado, fecha, titulo, descripcion, reserva_id, user_id) values
(1, 0, '2022-05-17 00:00:00', 'Todo mal', 'La plaza ha desaparecido', 5, 2);

insert into incidencias(id, estado, fecha, titulo, descripcion, reserva_id, user_id) values
(2, 0, '2022-05-17 00:00:00', 'Todo mal', 'La plaza ha desaparecido', 7, 3);

insert into incidencias(id, estado, fecha, titulo, descripcion, reserva_id, user_id) values
(3, 0, '2022-05-17 00:00:00', 'Todo mal', 'La plaza ha desaparecido',8, 4);
