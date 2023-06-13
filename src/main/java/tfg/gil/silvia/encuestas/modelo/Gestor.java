package tfg.gil.silvia.encuestas.modelo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = { "usuario"})
public class Gestor {
    private Integer id;
    private String usuario;
    private String password;
    private String nombreCompleto;
    private Zona zona;
}
