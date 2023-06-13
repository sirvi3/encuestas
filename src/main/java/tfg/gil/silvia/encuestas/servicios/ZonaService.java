package tfg.gil.silvia.encuestas.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import tfg.gil.silvia.encuestas.modelo.Zona;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class ZonaService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ZonaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Zona> getZonas() {
        String sql = "SELECT id, nombre FROM zona";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Zona zona = new Zona();
            zona.setId(rs.getLong("id"));
            zona.setNombre(rs.getString("nombre"));
            return zona;
        });
    }

    public Zona getZona(Long id) {
        String sql = "SELECT id, nombre FROM zona WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new Zona(rs.getLong("id"), rs.getString("nombre")), id);
    }

}
