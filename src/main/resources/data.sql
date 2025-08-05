USE ApexMagnamentdb;

INSERT IGNORE INTO rol (Nombre) VALUES ('Administrador') ;
INSERT IGNORE INTO rol (Nombre) VALUES ('Técnico') ;
INSERT IGNORE INTO rol (Nombre) VALUES ('Usuario');

INSERT IGNORE INTO personal (rol_id, nombre, apellido, email, telefono, img_personal, `username`, `password`) VALUES
(
     1, 
    'Juan',
    'Perez',
    'jefg@gmail.ocom',
    '12345678',
     76565, 
    'juan.perez',
    'password_hashed_o_plano'
)

