package tfg.gil.silvia.encuestas.servicios;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfg.gil.silvia.encuestas.excepciones.GestorNoExisteException;
import tfg.gil.silvia.encuestas.modelo.Gestor;
import tfg.gil.silvia.encuestas.modelo.Zona;

@Service
public class GestorService {

    private final JdbcTemplate jdbcTemplate;

    public GestorService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Transactional(readOnly = true)
    public Gestor findGestorByNombreUsuario(String username) throws GestorNoExisteException {
        String sql = """
                SELECT distinct g.id, g.nombre_completo, g.usuario, g.password,
                        z.id as zona_id, z.nombre as zona 
                FROM Gestor g, Zona z 
                WHERE g.zona_id=z.id and g.usuario=?""";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Gestor g = new Gestor();
                g.setId(rs.getInt("id"));
                g.setNombreCompleto(rs.getString("nombre_completo"));
                g.setUsuario(rs.getString("usuario"));
                g.setPassword(rs.getString("password"));
                g.setZona(new Zona(rs.getLong("zona_id"), rs.getString("zona")));
                return g;
            }, username);
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new GestorNoExisteException();
        }
    }
}
