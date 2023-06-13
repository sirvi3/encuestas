package tfg.gil.silvia.encuestas.modelo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = { "codigo"})
public class Encuesta {
    String codigo;
    LocalDateTime fecha;
    private String respuesta1;
    private String respuesta2;
    private String respuesta3;
    private String respuesta4;
    private String respuesta5;
}
