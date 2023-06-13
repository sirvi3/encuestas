package tfg.gil.silvia.encuestas.modelo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Peticion {
    private Long id;
    private LocalDateTime fechaPeticion;
    private String motivo;
    private String ubicacion;
    private String peticionario;
    private String telefono;
    private String codigoEncuesta;
    private Zona zona;
    private Encuesta encuesta;

    public String getFechaPeticionLegible() {
        return fechaPeticion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'a las' HH':'mm"));
    }
}
