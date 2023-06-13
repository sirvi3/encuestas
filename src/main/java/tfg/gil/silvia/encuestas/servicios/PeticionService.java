package tfg.gil.silvia.encuestas.servicios;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.gil.silvia.encuestas.controladores.EncuestaFormBean;
import tfg.gil.silvia.encuestas.modelo.Encuesta;
import tfg.gil.silvia.encuestas.modelo.Peticion;
import tfg.gil.silvia.encuestas.modelo.Zona;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class PeticionService {
    static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final JdbcTemplate jdbcTemplate;
    private final ZonaService zonaSvc;
    private final EnvioService envioSvc;
    @Autowired
    public PeticionService(JdbcTemplate jdbcTemplate, ZonaService zonaSvc, EnvioService envioSvc) {
        this.jdbcTemplate = jdbcTemplate;
        this.zonaSvc = zonaSvc;
        this.envioSvc = envioSvc;
    }

    public List<Peticion> obtenerTodasLasPeticiones() {
        String sql = "SELECT * FROM peticion";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapearPeticion(rs));
    }

    public Optional<Peticion> obtenerPeticionPorCodigo(String codigo) {
        String sql = """
            SELECT p.id, p.fecha_peticion, p.motivo, p.ubicacion, p.nombre_peticionario, p.telefono, p.zona_id,
                   p.codigo_encuesta, e.*
            FROM Peticion p LEFT JOIN Encuesta e on e.codigo_encuesta=p.codigo_encuesta
            WHERE p.codigo_encuesta = ?""";
        List<Peticion> lst = jdbcTemplate.query(sql, (rs, rowNum) -> mapearPeticion(rs), codigo);
        return lst.stream().findFirst();
    }

    @Transactional
    public void guardarPeticiones(String baseUrl, Zona zona, Collection<Peticion> peticion) {
        peticion.forEach(p -> guardarPeticion(baseUrl, zona, p));
    }

    @Transactional
    public void guardarPeticion(String baseUrl, Zona z, Peticion peticion) {
        // Las peticiones solo deben traer FECHA_PETICION, MOTIVO, UBICACION, NOMBRE_PETICIONARIO y TELEFONO
        String sql = "INSERT INTO peticion (fecha_peticion, motivo, ubicacion, nombre_peticionario, telefono, zona_id, codigo_encuesta) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Generamos un codigo de encuesta
        peticion.setCodigoEncuesta(UUID.randomUUID().toString().substring(0, 8));
        // Le asignamos la zona
        peticion.setZona(z);
        // Guardamos en BD
        jdbcTemplate.update(sql,
                peticion.getFechaPeticion(),
                peticion.getMotivo(),
                peticion.getUbicacion(),
                peticion.getPeticionario(),
                peticion.getTelefono(),
                peticion.getZona().getId(),
                peticion.getCodigoEncuesta());
        // Tras guardarla en BD se la enviamos al usuario via mail o sms o lo que se quiera. Si falla algo,
        // lo dejamos en la BD igualmente (mejora: Reintentar el envío)
        try {
            envioSvc.enviarEncuesta(baseUrl, peticion);
        } catch (Exception e) {
            log.warn("Error enviando notificación al usuario "+peticion.getPeticionario()+" (encuesta "+ peticion.getCodigoEncuesta()+")");
        }
    }

    @Transactional
    public void guardarEncuesta(EncuestaFormBean encuesta) {
        String sql = "INSERT INTO Encuesta (codigo_encuesta, fecha_encuesta, respuesta1, respuesta2, respuesta3, respuesta4, respuesta5) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                encuesta.getCodigo(),
                Timestamp.from(Instant.now()),
                encuesta.getRespuesta1(),
                encuesta.getRespuesta2(),
                encuesta.getRespuesta3(),
                encuesta.getRespuesta4(),
                encuesta.getRespuesta5());
    }

    private Peticion mapearPeticion(ResultSet rs) throws SQLException {
        Peticion peticion = new Peticion();
        peticion.setId(rs.getLong("id"));
        peticion.setFechaPeticion(LocalDateTime.parse(rs.getString("fecha_peticion"), formatter));
        peticion.setMotivo(rs.getString("motivo"));
        peticion.setUbicacion(rs.getString("ubicacion"));
        peticion.setPeticionario(rs.getString("nombre_peticionario"));
        peticion.setTelefono(rs.getString("telefono"));
        peticion.setZona(zonaSvc.getZona(rs.getLong("zona_id")));
        peticion.setCodigoEncuesta(rs.getString("codigo_encuesta"));
        // Si tiene encuesta asociada, ponemos sus datos
        String sFechaEnc = rs.getString("fecha_encuesta");
        if (sFechaEnc!=null) {
            Encuesta e = new Encuesta();
            e.setCodigo(peticion.getCodigoEncuesta());
            e.setFecha(LocalDateTime.parse(sFechaEnc, formatter));
            e.setRespuesta1(rs.getString("respuesta1"));
            e.setRespuesta1(rs.getString("respuesta2"));
            e.setRespuesta1(rs.getString("respuesta3"));
            e.setRespuesta1(rs.getString("respuesta4"));
            e.setRespuesta1(rs.getString("respuesta5"));
            peticion.setEncuesta(e);
        }
        return peticion;
    }
}
