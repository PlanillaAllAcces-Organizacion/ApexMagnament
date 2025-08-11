USE ApexMagnamentdb;

INSERT IGNORE INTO rol (Nombre) VALUES ('Administrador') ;
INSERT IGNORE INTO rol (Nombre) VALUES ('Tecnico') ;
INSERT IGNORE INTO rol (Nombre) VALUES ('Usuario');

INSERT IGNORE INTO personal (rol_id, nombre, apellido, email, status, telefono, img_personal, `username`, `password`) VALUES
(
     1, 
    'admin',
    'ALFT',
    'example@gmail.com',
     1 ,
    '1234-5678',
     76565, 
    'SysAdmin',
    '$2a$10$1G6ipIeVxt4VCOjS58FF.eFwXWZPbWf/SMXi4Pl7Zmhs5QHNs1dru'
)

