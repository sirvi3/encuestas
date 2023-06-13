package tfg.gil.silvia.encuestas.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EstadisticasSemana implements Comparable<EstadisticasSemana> {
    private LocalDate inicioSemana;
    private Integer total, pendientes, rellenadas;

    @Override
    public int compareTo(EstadisticasSemana o) {
        return this.inicioSemana.compareTo(o.inicioSemana);
    }
}
