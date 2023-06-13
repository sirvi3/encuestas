package tfg.gil.silvia.encuestas.controladores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tfg.gil.silvia.encuestas.modelo.Peticion;
import tfg.gil.silvia.encuestas.servicios.PeticionService;

import java.util.Optional;

@Controller
public class EncuestasControlador {
    private final PeticionService peticionService;
    @Autowired
    public EncuestasControlador(PeticionService petSvc) {
        this.peticionService = petSvc;
    }

    @GetMapping("/encuesta/{codigo_encuesta}")
    public String mostrarEncuesta(@PathVariable("codigo_encuesta") String codigoEncuesta, Model model) {
        Optional<Peticion> pet = peticionService.obtenerPeticionPorCodigo(codigoEncuesta);
        String mensaje = null;
        // Si la peticion existe
        if (pet.isPresent()) {
            Peticion p = pet.get();
            // Si no ha rellenado la encuesta, se le ofrece
            if (p.getEncuesta()==null) {
                model.addAttribute("peticion", p);
                return "encuesta";
            } else {
                mensaje = "Encuesta ya rellenada";
            }
        } else {
            mensaje = "Encuesta no encontrada";
        }
        model.addAttribute("mensaje", mensaje);
        return "paginaError";
    }

    @PostMapping("/encuesta/{codigo_encuesta}")
    public String enviarRespuestas(@PathVariable("codigo_encuesta") String codigoEncuesta, @ModelAttribute EncuestaFormBean respuestasEncuesta) {
        // Guardamos el código de la encuesta en el bean (viene dado, el usuario no lo rellena)
        respuestasEncuesta.setCodigo(codigoEncuesta);
        // Guardamos en BD
        peticionService.guardarEncuesta(respuestasEncuesta);
        // Redirigir a la página de agradecimiento
        return "redirect:/agradecimiento";
    }

    @GetMapping("/agradecimiento")
    public String mostrarAgradecimiento() {
        return "agradecimiento";
    }
}
