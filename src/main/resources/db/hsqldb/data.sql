-- One admin user, named admin1 with password 4dm1n and authority admin
-- INSERT INTO users(username,password,enabled) VALUES ('admin','admin',TRUE);
-- INSERT INTO authorities(id,username,authority) VALUES (1,'admin','admin');

INSERT INTO roles(id, name) values (1, 'ROLE_ADMIN');
INSERT INTO roles(id, name) values (2, 'ROLE_USER');
-- La contraseña es Admin1234
insert into clients(id, name, email, password, phone, surname) values (1, 'Admin', 'admin@admin.com', '$2a$10$lrBydvUI6cvLw12966m7cecxVjzfpGUN941ln5kzZGJIZZpyOMFta', 666998877, 'admin');
insert into CLIENT_ROLES(CLIENT_ID, ROLE_ID) values (1,1);  
---