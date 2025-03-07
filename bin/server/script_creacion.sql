/*NOMBRE DE BBDD telejaca */
CREATE TABLE Localidad (
    id INT AUTO_INCREMENT,
    localidad VARCHAR(50),
    CONSTRAINT PK_Localidad PRIMARY KEY (id)
);

CREATE TABLE Usuarios (
    id INT AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(150),
    genero VARCHAR(1),
    fecha_nacimiento DATE,
    direccion VARCHAR(255),
    datos_importantes TEXT,
    unidad_de_convivencia VARCHAR(255),
    localidad_id INT,
    causa_baja VARCHAR(255),
    fecha_baja DATE,
    CONSTRAINT PK_Usuarios PRIMARY KEY (id),
    CONSTRAINT FK_Usuarios_localidadId FOREIGN KEY (localidad_id) REFERENCES Localidad(id) ON DELETE SET NULL,
    CONSTRAINT CHK_genero CHECK (UPPER(genero) IN ('H', 'M'))
);

CREATE INDEX INDX_nombreUsuario ON Usuarios (nombre);
CREATE INDEX INDX_apellidoUsuario ON Usuarios (apellido);

CREATE TABLE Medicamento (
    id INT AUTO_INCREMENT,
    descripcion VARCHAR(255) NOT NULL,
    usuario_id INT,
    CONSTRAINT PK_Medicamento PRIMARY KEY (id),
    CONSTRAINT FK_Medicamento_usuarioId FOREIGN KEY (usuario_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);

CREATE TABLE Es_Cuidador (
    usuario_receptor_id INT,
    cuidador_id INT,
    CONSTRAINT PK_Es_Cuidador PRIMARY KEY (usuario_receptor_id, cuidador_id),
    CONSTRAINT FK_EsCuidador_usuarioReceptorId FOREIGN KEY (usuario_receptor_id) REFERENCES Usuarios(id) ON DELETE CASCADE,
    CONSTRAINT FK_EsCuidador_cuidadorId FOREIGN KEY (cuidador_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);

CREATE TABLE Telefono (
    id INT AUTO_INCREMENT,
    telefono VARCHAR(13) NOT NULL,
    descripcion VARCHAR(255),
    usuario_id INT,
    CONSTRAINT PK_Telefono PRIMARY KEY (id),
    CONSTRAINT FK_Telefono_uduarioId FOREIGN KEY (usuario_id) REFERENCES Usuarios(id) ON DELETE CASCADE
);

CREATE TABLE Empleados (
    username VARCHAR(50),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(150),
    email VARCHAR(250) UNIQUE,
    password VARCHAR(255) NOT NULL,
    roles VARCHAR(25), /* Un usuario podrá tener varios roles, se separará por comas. */
    CONSTRAINT PK_Empleados PRIMARY KEY (username)
);

CREATE TABLE Tipos_llamadas (
    id INT AUTO_INCREMENT,
    descripcion VARCHAR(255) NOT NULL,
    CONSTRAINT PK_Tipos_llamadas PRIMARY KEY (id)
);

CREATE TABLE Llamada (
    tipo_llamada_id INT,
    usuario_id INT,
    empleado_username VARCHAR(50),
    fecha DATE,
    orden INT,
    descripcion TEXT,
    CONSTRAINT PK_Llamada PRIMARY KEY (tipo_llamada_id, usuario_id, empleado_username, fecha, orden),
    CONSTRAINT FK_Llamada_tipoLlamadaId FOREIGN KEY (tipo_llamada_id) REFERENCES Tipos_llamadas(id) ON DELETE CASCADE,
    CONSTRAINT FK_Llamada_usuarioId FOREIGN KEY (usuario_id) REFERENCES Usuarios(id) ON DELETE CASCADE,
    CONSTRAINT FK_Llamada_empleadoId FOREIGN KEY (empleado_username) REFERENCES Empleados(username) ON DELETE CASCADE
);




