package tfg.gil.silvia.encuestas.servicios;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import tfg.gil.silvia.encuestas.dto.EstadisticasSemana;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Service
public class EstadisticasService {
    static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JdbcTemplate jdbcTemplate;

    public EstadisticasService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EstadisticasSemana> getEstadisticasPorSemana() {
        String sql = """
                select date(p.fecha_peticion, '-' || strftime('%w', p.fecha_peticion) || ' days', 'weekday 1') as Semana,
                       count(p.id) as Total,
                       count (e.codigo_encuesta) as Rellenadas,
                       count(p.id)-count (e.codigo_encuesta) as Pendientes
                from Peticion p
                		left join Encuesta e on e.codigo_encuesta=p.codigo_encuesta
                group by 1
                order by 1
                """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EstadisticasSemana e = new EstadisticasSemana();
            e.setInicioSemana(LocalDate.parse(rs.getString("Semana"), dateFormatter));
            e.setTotal(rs.getInt("Total"));
            e.setPendientes(rs.getInt("Pendientes"));
            e.setRellenadas(rs.getInt("Rellenadas"));
            return e;
        });
    }

    public byte[] descargarDatosExcel(String semana) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Estadisticas");
        // Crear un estilo para la cabecera
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();

        // Aplicar formato a la fuente
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14); // Tamaño de fuente más grande

        // Aplicar formato al estilo de celda
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER); // Alinear al centro
        headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // Alinear verticalmente al centro

        Row headerRow = sheet.createRow(0);
        AtomicInteger col = new AtomicInteger();
        Arrays.asList("ID","Fecha peticion", "Motivo", "Ubicacion", "Zona", "Peticionario", "Telefono", "Fecha encuesta",
                "Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4", "Respuesta 5").forEach(nombreCol -> {
            Cell c = headerRow.createCell(col.getAndIncrement());
            c.setCellStyle(headerCellStyle);
            c.setCellValue(nombreCol);
        });
        // Resto de filas
        String sql = """
                select p.id, p.fecha_peticion, p.motivo, p.ubicacion , z.nombre as zona, p.nombre_peticionario , p.telefono,
                                 e.fecha_encuesta, e.respuesta1 ,e.respuesta2 ,e.respuesta3 ,e.respuesta4 , e.respuesta5
                          from Peticion p
                                inner join Zona z on
                                    p.zona_id=z.id
                                left join Encuesta e on
                                    p.codigo_encuesta=e.codigo_encuesta
                          WHERE date(p.fecha_peticion, '-' || strftime('%w', p.fecha_peticion) || ' days', 'weekday 1')=?
                """;
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            Row row = sheet.createRow(rowNum + 1);
            int colx = 0;
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            row.createCell(colx).setCellValue(rs.getString(1 + colx++));
            // Autoajustar el ancho de las columnas
            for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                sheet.autoSizeColumn(i);
            }
            return null;
        }, semana);
        // Ponemos el autofiltro
        sheet.setAutoFilter(CellRangeAddress.valueOf("A1:K1"));
        // Escribir el libro en un flujo de salida (ByteArrayOutputStream)
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            workbook.write(outputStream);
            workbook.close();
            // Obtener los datos del flujo de salida
            return outputStream.toByteArray();
        }
    }
}
