INSERT INTO Zona (nombre) VALUES
	 ('Distrito 1'),
	 ('Distrito 2'),
	 ('Distrito 3');


INSERT INTO Gestor (usuario,password,nombre_completo,zona_id) VALUES
	 ('gestor1','clave1','Silvia Gil',1),
	 ('gestor2','clave2','Andrés Lobato',2);

INSERT INTO Peticion (fecha_peticion, motivo, ubicacion, nombre_peticionario, telefono, zona_id, codigo_encuesta)
VALUES
    ('2023-06-02T11:33:00', 'Aparcado en vado', 'Calle Mora', 'Alberto Perez', '666059838', 1, 'f3df3138'),
    ('2023-06-02T15:04:13', 'Gato subido a un árbol', 'Avenida General Mola 122', 'Rosa Pérez', '6662313', 1, '666095831'),
    ('2023-06-02T17:11:32', 'Giro bloquea paso camión basura', 'Calle Navas esq. c/Alicante', 'Roberto Panadero Díaz', '666343982', 2, '7910002c'),
    ('2023-06-02T19:34:53', 'Pelea entre bandas', 'Calle Oca 2', 'Gilberto Güey', '666098564', 1, 'bcd2e71d'),
    ('2023-06-05T17:30:00', 'Ruido excesivo', 'Calle Mayor', 'Laura Fernandez', '689354712', 2, '15f67dab'),
    ('2023-06-06T10:20:00', 'Contenedor averiado', 'Calle Toledo', 'Manuel Martin', '634780912', 1, '4e6a7fb8'),
    ('2023-06-07T16:40:00', 'Alcantarilla obstruida', 'Calle Atocha', 'Carmen Lopez', '677982314', 3, '38f9a1cd'),
    ('2023-06-08T12:10:00', 'Farola rota', 'Paseo del Prado', 'Antonio Ramirez', '622543791', 2, 'c5b6d9f3'),
    ('2023-06-09T08:55:00', 'Banco roto', 'Calle Mayor', 'Isabel Torres', '655123789', 1, 'ad2e3c9f'),
    ('2023-06-10T13:20:00', 'Coche mal aparcado', 'Calle Gran Via', 'María Torres', '633145678', 1, 'e1f5c84d'),
    ('2023-06-11T16:45:00', 'Basura acumulada', 'Calle Alcalá', 'Javier Rodríguez', '677892345', 3, 'f7b2e8d9'),
    ('2023-06-12T09:10:00', 'Farola intermitente', 'Calle Mayor', 'Sara Gómez', '688123457', 2, '3a9f5e7c'),
    ('2023-06-13T14:30:00', 'Vehículo abandonado', 'Paseo del Prado', 'Carlos Martín', '622987654', 1, 'b6d8c9e1'),
    ('2023-06-14T11:55:00', 'Escalera rota', 'Calle Atocha', 'Luisa Fernández', '666333999', 3, '25789af6');


INSERT INTO Encuesta (codigo_encuesta,fecha_encuesta,respuesta1,respuesta2,respuesta3,respuesta4,respuesta5) VALUES
	 ('7910002c','2023-06-03T10:24:03','7','ANTES','NO','5','6');
