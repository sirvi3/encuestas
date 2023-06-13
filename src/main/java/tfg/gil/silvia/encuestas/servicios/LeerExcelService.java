package tfg.gil.silvia.encuestas.servicios;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tfg.gil.silvia.encuestas.excepciones.LecturaExcelException;
import tfg.gil.silvia.encuestas.modelo.Peticion;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class LeerExcelService {

    public List<Peticion> procesarExcel(InputStream fileInputStream) throws LecturaExcelException {
        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {
            Sheet sheet = workbook.getSheetAt(0); // Suponiendo que el archivo Excel tiene solo una hoja
            // Obtener las cabeceras
            Row headerRow = sheet.getRow(0);
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }
            // Validar las columnas requeridas
            comprobarColumnas(headers);
            // Leer los datos
            try {
                List<Peticion> excelDataList = new ArrayList<>();
                int numFila = 1;
                Row row;
                do {
                    row = sheet.getRow(numFila++);
                    if (row!=null) {
                        Peticion excelData = new Peticion();
                        excelData.setFechaPeticion(getValorCeldaComoFecha(row.getCell(0)));
                        excelData.setMotivo(getValorCeldaComoString(row.getCell(1)));
                        excelData.setUbicacion(getValorCeldaComoString(row.getCell(2)));
                        excelData.setPeticionario(getValorCeldaComoString(row.getCell(3)));
                        excelData.setTelefono(getValorCeldaComoString(row.getCell(4)));
                        excelDataList.add(excelData);
                    }
                } while (row!=null);
                workbook.close();
                return excelDataList;
            } catch (Exception e) {
                throw new LecturaExcelException("Error leyendo datos del Excel: " + e.getMessage());
            }
        } catch (IOException e) {
            throw new LecturaExcelException("Error de entrada/salida leyendo archivo Excel: " + e.getMessage());
        }
    }

    private void comprobarColumnas(List<String> headers) throws LecturaExcelException {
        List<String> requiredColumns = List.of("FECHA_PETICION", "MOTIVO", "UBICACION", "NOMBRE_PETICIONARIO", "TELEFONO");
        if (new HashSet<>(headers).containsAll(requiredColumns)) {
            return;
        }
        throw new LecturaExcelException("Las columnas de cabecera no son correctas");
    }

    private LocalDateTime getValorCeldaComoFecha(Cell cell) throws ParseException {
        return LocalDateTime.parse(cell.getStringCellValue(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String getValorCeldaComoString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return "";
        }
    }

}
