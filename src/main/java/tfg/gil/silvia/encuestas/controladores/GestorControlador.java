package tfg.gil.silvia.encuestas.controladores;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import tfg.gil.silvia.encuestas.excepciones.GestorNoExisteException;
import tfg.gil.silvia.encuestas.excepciones.LecturaExcelException;
import tfg.gil.silvia.encuestas.excepciones.PasswordIncorrectaException;
import tfg.gil.silvia.encuestas.modelo.Gestor;
import tfg.gil.silvia.encuestas.modelo.Peticion;
import tfg.gil.silvia.encuestas.servicios.GestorService;
import tfg.gil.silvia.encuestas.servicios.LeerExcelService;
import tfg.gil.silvia.encuestas.servicios.PeticionService;
import tfg.gil.silvia.encuestas.servicios.ZonaService;

import java.util.List;

@Controller
public class GestorControlador {

    @Autowired
    ZonaService zonaSvc;
    @Autowired
    LeerExcelService excelSvc;
    @Autowired
    GestorService gestorSvc;
    @Autowired
    PeticionService peticionSvc;

    @GetMapping("/admin/subirDatos")
    public String mostrarFormulario(Model model, RedirectAttributes redirectAttributes) {
        // Despues de procesar el formulario, se vuelve aquí para cargar de nuevo la página.
        // Si venía algún error, lo trasladamos al modelo
        String mensajeError = (String) redirectAttributes.getFlashAttributes().get("mensajeError");
        if (mensajeError != null)
            model.addAttribute("mensajeError", mensajeError);
        else if (redirectAttributes.getFlashAttributes().get("ok")!=null)
            model.addAttribute("mensajeOk", true);
        model.addAttribute("zonas", zonaSvc.getZonas());
        return "subirArchivo";
    }

    @PostMapping("/admin/subirDatos")
    public String procesarFormulario(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     @RequestParam("excelFile") MultipartFile excelFile,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
        String mensajeError = null;
        try {
            // Miramos a ver si existe el gestor
            Gestor g = gestorSvc.findGestorByNombreUsuario(username);
            // Comprobamos su password
            if (!g.getPassword().equals(password))
                throw new PasswordIncorrectaException();
            // Leemos del Excel y lo convertimos a peticiones
            List<Peticion> peticiones = excelSvc.procesarExcel(excelFile.getInputStream());
            // Las insertamos en la base de datos
            peticionSvc.guardarPeticiones(baseUrl, g.getZona(), peticiones);
        } catch (GestorNoExisteException e) {
            mensajeError = "El gestor no existe";
        } catch (PasswordIncorrectaException ex) {
            mensajeError = "La contraseña del usuario no es correcta";
        } catch (LecturaExcelException e) {
            mensajeError = e.getMessage();
        } catch (Exception ex) {
            mensajeError = ex.getMessage();
        }
        if (mensajeError != null)
            redirectAttributes.addFlashAttribute("mensajeError", mensajeError);
        else
            redirectAttributes.addFlashAttribute("ok", true);
        return "redirect:/admin/subirDatos";
    }
}
