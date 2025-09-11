package com.sipcommb.envases.service;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.ExtractosDTO;
import com.sipcommb.envases.dto.FileResponse;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.dto.JarTypeDTO;
import com.sipcommb.envases.dto.QuimicosDTO;

@Service
public class FileService {

    @Autowired
    JarTypeService jarTypeService;

    @Autowired
    CapService capService;

    @Autowired
    JarService jarService;

    @Autowired
    QuimicosService quimicosService;

    @Autowired
    ExtractosService extractosService;


    public List<FileResponse> readFile(MultipartFile file, String token) {
        token = token.replace("Bearer ", "");
        List<FileResponse> fileResponses = new ArrayList<>();
        try{
            InputStream inputStream = file.getInputStream();

            Workbook workbook = WorkbookFactory.create(inputStream);
            fileResponses.addAll(readJarType(workbook.getSheetAt(0)));
            fileResponses.addAll(readCap(workbook.getSheetAt(1), token));
            fileResponses.addAll(readJar(workbook.getSheetAt(2), token));
            fileResponses.addAll(readQuimicos(workbook.getSheetAt(3), token));
            fileResponses.addAll(readExtractos(workbook.getSheetAt(4), token));
            return fileResponses;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    public List<FileResponse> readJarType(Sheet sheet) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            Cell diameterCell = row.getCell(0);
            Cell nameCell = row.getCell(1);
            Cell descriptionCell = row.getCell(2);

            try{

                jarTypeService.addJarTypes( new JarTypeDTO(
                    nameCell.getStringCellValue(),
                    descriptionCell.getStringCellValue(),
                    getCellAsString(diameterCell)
                ));

                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Tipo de envase agregado correctamente"));

            }catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
            
        }
        return fileResponses;
    }

    public List<FileResponse> readCap(Sheet sheet, String token) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            Cell nameCell = row.getCell(0);
            Cell descriptionCell = row.getCell(1);
            Cell diameterCell = row.getCell(2);
            Cell unidadCell = row.getCell(3);
            Cell docenaCell = row.getCell(4);
            Cell cienCell = row.getCell(5);
            Cell pacaCell = row.getCell(6);
            Cell unitsInPacaCell = row.getCell(7);
            Cell colorCell = row.getCell(8);
            Cell quantityCell = row.getCell(9);

            try{

                if(quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad de la tapa y nombre es obligatorio");
                }

                if(quantityCell == null) {
                    throw new RuntimeException("la cantidad de la tapa es obligatoria");
                }

                if(nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre de la tapa es obligatorio");
                }

                capService.addCaps(new CapRequest(
                    nameCell.getStringCellValue(),
                    getCellAsNullableString(descriptionCell),
                    colorCell.getStringCellValue(),
                    getCellAsString(diameterCell),
                    (int) quantityCell.getNumericCellValue(),
                    unidadCell.getNumericCellValue(),
                    getCellAsNullableDouble(docenaCell),
                    getCellAsNullableDouble(cienCell),
                    getCellAsNullableDouble(pacaCell),
                    getCellAsNullableDouble(unitsInPacaCell).intValue()
                ), token);

                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Tipo de tapa agregado correctamente"));

            }catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
            
        }
        return fileResponses;
    }

    private List<FileResponse> readJar(Sheet sheet, String token) {
        boolean firstRow = true;

        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }



            Cell nameCell = row.getCell(0);
            Cell descriptionCell = row.getCell(1);
            Cell diameterCell = row.getCell(2);
            Cell unidadCell = row.getCell(3);
            Cell docenaCell = row.getCell(4);
            Cell cienCell = row.getCell(5);
            Cell pacaCell = row.getCell(6);
            Cell unitsInPacaCell = row.getCell(7);
            Cell quantityCell = row.getCell(8);
           


            try {

                if(quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del frasco y nombre es obligatorio");
                }

                if(quantityCell == null) {
                    throw new RuntimeException("la cantidad del frasco es obligatoria");
                }

                if(nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del frasco es obligatorio");
                }

                jarService.addJar(new JarRequestDTO(
                    nameCell.getStringCellValue(), 
                    getCellAsNullableString(descriptionCell), 
                    getCellAsString(diameterCell), 
                    (int) quantityCell.getNumericCellValue(), 
                    getCellAsNullableDouble(unidadCell), 
                    getCellAsNullableDouble(docenaCell), 
                    getCellAsNullableDouble(cienCell), 
                    getCellAsNullableDouble(pacaCell), 
                    getCellAsNullableDouble(unitsInPacaCell).intValue(), 
                    getCaps(row.getCell(10)), 
                    getCaps(row.getCell(9))), token);

                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Tipo de tapa agregado correctamente"));

            } catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
        }
        return fileResponses;
    }

    private List<FileResponse> readQuimicos(Sheet sheet, String token) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            Cell nameCell = row.getCell(0);
            Cell descriptionCell = row.getCell(1);
            Cell unidadCell = row.getCell(2);
            Cell quantityCell = row.getCell(3);

            try {

                if(quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del químico y nombre es obligatorio");
                }

                if(quantityCell == null) {
                    throw new RuntimeException("la cantidad del químico es obligatoria");
                }

                if(nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del químico es obligatorio");
                }

                quimicosService.addQuimico(new QuimicosDTO(
                    nameCell.getStringCellValue(),
                    getCellAsNullableString(descriptionCell),
                    (int) quantityCell.getNumericCellValue(),
                    unidadCell.getNumericCellValue()
                ), token);
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Químico agregado correctamente"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
        }
        return fileResponses;
    }

    private List<FileResponse> readExtractos(Sheet sheet, String token) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            Cell nameCell = row.getCell(0);
            Cell descriptionCell = row.getCell(1);
            Cell ml1000cell = row.getCell(2);
            Cell ml500cell = row.getCell(3);
            Cell ml250cell = row.getCell(4);
            Cell ml125cell = row.getCell(5);
            Cell ml60cell = row.getCell(6);
            Cell ml22cell = row.getCell(7);
            Cell quantityCell = row.getCell(8);

            try {

                if(quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del extracto y nombre es obligatorio");
                }

                if(quantityCell == null) {
                    throw new RuntimeException("la cantidad del extracto es obligatoria");
                }

                if(nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del extracto es obligatorio");
                }

                extractosService.addExtracto(new ExtractosDTO(
                    nameCell.getStringCellValue(),
                    getCellAsNullableString(descriptionCell),
                    Integer.valueOf((int) quantityCell.getNumericCellValue()),
                    getCellAsNullableDouble(ml1000cell),
                    getCellAsNullableDouble(ml500cell),
                    getCellAsNullableDouble(ml250cell),
                    getCellAsNullableDouble(ml125cell),
                    getCellAsNullableDouble(ml60cell),
                    getCellAsNullableDouble(ml22cell)
                ), token);
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Extracto agregado correctamente"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
        }
        return fileResponses;
    }


    private String getCellAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // Si quieres quitar los decimales si es entero:
                double d = cell.getNumericCellValue();
                    if (d == Math.floor(d)) {
                        return String.valueOf((int) d);
                    } else {
                        return String.valueOf(d);
                    }
            default:
                return "";
        }
    }

    private String getCellAsNullableString(Cell cell) {
        if (cell == null ) {
            return null;
        }
        return getCellAsString(cell);
    }

    private Double getCellAsNullableDouble(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getNumericCellValue();
    }

    private String[] getCaps(Cell cellComp) {
        if (cellComp == null) {
            return null;
        }
        String[] caps = cellComp.getStringCellValue().split(",");
        for (int i = 0; i < caps.length; i++) {
            caps[i] = caps[i].trim();
        }

        return caps;
    }
}
