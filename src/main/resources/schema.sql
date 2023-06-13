-- Zona definition
drop table  if exists Zona;

CREATE TABLE Zona (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT
);


-- Gestor definition

drop table if exists Gestor;

CREATE TABLE Gestor (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    usuario VARCHAR(255),
    password VARCHAR(255),
    nombre_completo VARCHAR(255),
    zona_id BIGINT,
    FOREIGN KEY (zona_id) REFERENCES Zona(id)
);


-- Peticion definition

drop table  if exists Peticion;

CREATE TABLE Peticion (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_peticion DATE,
    motivo TEXT,
    ubicacion TEXT,
    nombre_peticionario TEXT,
    telefono TEXT,
    zona_id INTEGER,
    codigo_encuesta TEXT UNIQUE,
    FOREIGN KEY (zona_id) REFERENCES Zona(id)
);


-- Encuesta definition
drop table  if exists Encuesta;

CREATE TABLE Encuesta (
    codigo_encuesta TEXT PRIMARY KEY,
    fecha_encuesta DATE,
    respuesta1 TEXT,
    respuesta2 TEXT,
    respuesta3 TEXT,
    respuesta4 TEXT,
    respuesta5 TEXT,
    FOREIGN KEY (codigo_encuesta) REFERENCES Peticion(codigo_encuesta)
);