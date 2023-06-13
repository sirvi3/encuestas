package tfg.gil.silvia.encuestas.controladores;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tfg.gil.silvia.encuestas.servicios.EstadisticasService;

import java.io.IOException;

@Controller
public class EstadisticasController {

    private final EstadisticasService estSvc;

    public EstadisticasController(EstadisticasService eSvc) {
        this.estSvc = eSvc;
    }
    @GetMapping("/estadisticas")
    public String cargarEstadisticas(Model m) {
        m.addAttribute("estadisticas", estSvc.getEstadisticasPorSemana());
        return "estadisticas";
    }

    @GetMapping("/estadisticas/descargar/{semana}")
    public ResponseEntity<byte[]> generarExcelSemana(Model m, @PathVariable("semana") String semana) {
        byte[] fileData;
        String fileName;
        try {
            // Obtener los datos de las estadísticas en formato CSV o XLSX
            fileData = estSvc.descargarDatosExcel(semana);
            fileName = "estadisticas"+semana+".xlsx";
        } catch (IOException e) {
            // Manejar el error de forma adecuada según tu lógica de negocio
            return ResponseEntity.status(500).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData(fileName, fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(fileData);

    }
}
