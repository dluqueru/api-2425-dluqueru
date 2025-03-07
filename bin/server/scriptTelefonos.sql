DELIMITER $$

CREATE PROCEDURE AsignarTelefonosAleatorios()
BEGIN
    DECLARE usuario_id INT;
    DECLARE done INT DEFAULT FALSE;
    DECLARE cur CURSOR FOR SELECT id FROM Usuarios;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    OPEN cur;
    
    read_loop: LOOP
        FETCH cur INTO usuario_id;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- Generar un teléfono aleatorio con 9 cifras y comenzar con '9'
        INSERT INTO Telefono (telefono, descripcion, usuario_id)
        VALUES (CONCAT('9', LPAD(FLOOR(RAND() * 100000000), 8, '0')), 'Teléfono principal', usuario_id);
        
    END LOOP;
    
    CLOSE cur;
END $$

DELIMITER ;


CALL AsignarTelefonosAleatorios();

DROP PROCEDURE IF EXISTS AsignarTelefonosAleatorios;