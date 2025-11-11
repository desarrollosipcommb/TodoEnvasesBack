package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.BodegaDTO;
import com.sipcommb.envases.dto.CapColorRequest;
import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.ExtractoRequest;
import com.sipcommb.envases.dto.FileResponse;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.dto.JarTypeDTO;
import com.sipcommb.envases.dto.QuimicoRequestDTO;
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

    @Autowired
    BodegaService bodegaService;

    public List<FileResponse> readFile(MultipartFile file, String token) {
        token = token.replace("Bearer ", "");
        List<FileResponse> fileResponses = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();

            Workbook workbook = WorkbookFactory.create(inputStream);
            fileResponses.addAll(readJarType(workbook.getSheetAt(0)));
            fileResponses.addAll(readBodegas(workbook.getSheetAt(1)));
            fileResponses.addAll(readCap(workbook.getSheetAt(2), token));
            fileResponses.addAll(readJar(workbook.getSheetAt(3), token));
            fileResponses.addAll(readQuimicos(workbook.getSheetAt(4), token));
            fileResponses.addAll(readExtractos(workbook.getSheetAt(5), token));
            return fileResponses;
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    public List<FileResponse> readFileInventory(MultipartFile file, String token) {
        token = token.replace("Bearer ", "");
        List<FileResponse> fileResponses = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = WorkbookFactory.create(inputStream);
            fileResponses.addAll(CapInventory(workbook.getSheetAt(2), token));
            fileResponses.addAll(JarInventory(workbook.getSheetAt(3), token));
            fileResponses.addAll(QuimicoInventory(workbook.getSheetAt(4), token));
            fileResponses.addAll(extractosInventory(workbook.getSheetAt(5), token));
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

            try {

                jarTypeService.addJarTypes(new JarTypeDTO(
                        nameCell.getStringCellValue(),
                        descriptionCell.getStringCellValue(),
                        getCellAsString(diameterCell), true));

                fileResponses
                        .add(new FileResponse(nameCell.getStringCellValue(), "Tipo de envase agregado correctamente"));

            } catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }

        }
        return fileResponses;
    }

    public List<FileResponse> readBodegas(Sheet sheet) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            Cell nameCell = row.getCell(0);
            Cell priorityCell = row.getCell(1);

            try {

                bodegaService.addBodega(nameCell.getStringCellValue(), (long) priorityCell.getNumericCellValue());

                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Bodega agregada correctamente"));

            } catch (Exception e) {
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
            Cell bodegaCell = row.getCell(10);

            try {

                if (quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad de la tapa y nombre es obligatorio");
                }

                if (quantityCell == null) {
                    throw new RuntimeException("la cantidad de la tapa es obligatoria");
                }

                if (getCellAsString(descriptionCell).equals("")) {
                    throw new RuntimeException("la descripción de la tapa es obligatoria");
                }

                if (nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre de la tapa es obligatorio");
                }

                if (bodegaCell == null) {
                    throw new RuntimeException("Al menos una bodega para la tapa es obligatoria");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                if (capService.existsByNameAndDiameter(nameCell.getStringCellValue(), getCellAsString(diameterCell))) {
                    capService.addCapColor(new CapColorRequest(
                            nameCell.getStringCellValue(),
                            getCellAsString(diameterCell),
                            colorCell.getStringCellValue(),
                            unidadCell.getNumericCellValue(),
                            getCellAsNullableDouble(docenaCell),
                            getCellAsNullableDouble(cienCell),
                            getCellAsNullableDouble(pacaCell),
                            getCellAsNullableDouble(unitsInPacaCell).intValue(),
                            bodegaDTOs), token);
                } else {
                    capService.addCaps(new CapRequest(
                            nameCell.getStringCellValue(),
                            getCellAsNullableString(descriptionCell),
                            getCellAsString(diameterCell)), token);
                    capService.addCapColor(new CapColorRequest(
                            nameCell.getStringCellValue(),
                            getCellAsString(diameterCell),
                            colorCell.getStringCellValue(),
                            unidadCell.getNumericCellValue(),
                            getCellAsNullableDouble(docenaCell),
                            getCellAsNullableDouble(cienCell),
                            getCellAsNullableDouble(pacaCell),
                            getCellAsNullableDouble(unitsInPacaCell).intValue(),
                            bodegaDTOs), token);
                }

                fileResponses.add(new FileResponse(nameCell.getStringCellValue() + " " + colorCell.getStringCellValue(),
                        "Tipo de tapa agregado correctamente"));

            } catch (Exception e) {
                fileResponses.add(new FileResponse(getCellAsString(nameCell) + " " + getCellAsString(colorCell),
                        "Error, " + e.getMessage()));
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
            Cell bodegaCell = row.getCell(9);

            try {

                if (quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del frasco y nombre es obligatorio");
                }

                if (quantityCell == null) {
                    throw new RuntimeException("la cantidad del frasco es obligatoria");
                }

                if (nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del frasco es obligatorio");
                }

                bodegaService.getBodegaByName(bodegaCell.getStringCellValue());

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                jarService.addJar(new JarRequestDTO(
                        nameCell.getStringCellValue(),
                        getCellAsNullableString(descriptionCell),
                        getCellAsString(diameterCell),
                        bodegaDTOs,
                        getCellAsNullableDouble(unidadCell),
                        getCellAsNullableDouble(docenaCell),
                        getCellAsNullableDouble(cienCell),
                        getCellAsNullableDouble(pacaCell),
                        getCellAsNullableDouble(unitsInPacaCell).intValue(),
                        getCaps(row.getCell(11)),
                        getCaps(row.getCell(10))), token);

                fileResponses
                        .add(new FileResponse(nameCell.getStringCellValue(), "Tipo de tapa agregado correctamente"));

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
            Cell bodegaCell = row.getCell(4);

            try {

                if (quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del químico y nombre es obligatorio");
                }

                if (quantityCell == null) {
                    throw new RuntimeException("la cantidad del químico es obligatoria");
                }

                if (nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del químico es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                quimicosService.addQuimico(new QuimicoRequestDTO(
                        nameCell.getStringCellValue(),
                        getCellAsNullableString(descriptionCell),
                        bodegaDTOs,
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
            Cell bodegaCell = row.getCell(9);

            try {

                if (quantityCell == null && (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del extracto y nombre es obligatorio");
                }

                if (quantityCell == null) {
                    throw new RuntimeException("la cantidad del extracto es obligatoria");
                }

                if (nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del extracto es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                extractosService.addExtracto(new ExtractoRequest(
                        nameCell.getStringCellValue(),
                        getCellAsNullableString(descriptionCell),
                        bodegaDTOs,
                        getCellAsNullableDouble(ml22cell),
                        getCellAsNullableDouble(ml60cell),
                        getCellAsNullableDouble(ml125cell),
                        getCellAsNullableDouble(ml250cell),
                        getCellAsNullableDouble(ml500cell),
                        getCellAsNullableDouble(ml1000cell)), token);
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Extracto agregado correctamente"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Error, " + e.getMessage()));
            }
        }
        return fileResponses;
    }

    private List<FileResponse> JarInventory(Sheet sheet, String token) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            if (row == null) {
                continue;
            }

            Cell nameCell = row.getCell(0);
            Cell quantityCell = row.getCell(8);
            Cell bodegaCell = row.getCell(9);
            try {
                if (quantityCell == null || (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del frasco y nombre es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                jarService.updateInventoryJar(
                        nameCell.getStringCellValue(),
                        bodegaDTOs,
                        token);

                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Inventario del envase actualizado"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(getCellAsString(nameCell), "Error, " + e.getMessage()));
                e.printStackTrace();
            }
        }
        return fileResponses;
    }

    private List<FileResponse> CapInventory(Sheet sheet, String token) {
        boolean firstRow = true;

        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            if (row == null) {
                continue;
            }

            Cell nameCell = row.getCell(0);

            Cell diameterCell = row.getCell(2);
            Cell unidadCell = row.getCell(3);
            Cell docenaCell = row.getCell(4);
            Cell cienCell = row.getCell(5);
            Cell pacaCell = row.getCell(6);
            Cell unitsInPacaCell = row.getCell(7);
            Cell colorCell = row.getCell(8);
            Cell quantityCell = row.getCell(9);
            Cell bodegaCell = row.getCell(10);
            try {
                if (quantityCell == null || (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad de la tapa y nombre es obligatorio");
                }

                if (getCellAsString(diameterCell).equals("")) {
                    throw new RuntimeException("el diametro de la tapa es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                capService.updateCapInventory(new CapColorRequest(
                        nameCell.getStringCellValue(),
                        getCellAsString(diameterCell),
                        colorCell.getStringCellValue(),
                        unidadCell.getNumericCellValue(),
                        getCellAsNullableDouble(docenaCell),
                        getCellAsNullableDouble(cienCell),
                        getCellAsNullableDouble(pacaCell),
                        getCellAsNullableDouble(unitsInPacaCell).intValue(),
                        bodegaDTOs), token);

                fileResponses.add(new FileResponse(getCellAsString(nameCell) + " " + getCellAsString(colorCell),
                        "Inventario de la tapa ha sido actualizado"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(getCellAsString(nameCell) + " " + getCellAsString(colorCell),
                        "Error al crear, " + e.getMessage()));
                e.printStackTrace();
            }
        }
        return fileResponses;
    }

    private List<FileResponse> QuimicoInventory(Sheet sheet, String token) {
        boolean firstRow = true;
        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }
            if (row == null) {
                continue;
            }
            Cell nameCell = row.getCell(0);
            Cell quantityCell = row.getCell(3);
            Cell bodegaCell = row.getCell(4);
            try {
                if (quantityCell == null || (nameCell == null || nameCell.getStringCellValue().isEmpty())) {
                    throw new RuntimeException("la cantidad del químico y/o nombre es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                quimicosService.updateInventoryQuimico(
                        nameCell.getStringCellValue(),
                        bodegaDTOs,
                        token);

                fileResponses
                        .add(new FileResponse(nameCell.getStringCellValue(), "Inventario del químico actualizado"));
            } catch (Exception e) {
                fileResponses.add(new FileResponse(getCellAsString(nameCell), "Error, " + e.getMessage()));
            }
        }
        return fileResponses;
    }

    private List<FileResponse> extractosInventory(Sheet sheet, String token) {
        boolean firstRow = true;

        List<FileResponse> fileResponses = new ArrayList<>();
        for (Row row : sheet) {
            if (firstRow) {
                firstRow = false;
                continue;
            }

            if (row == null) {
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
            Cell bodegaCell = row.getCell(9);

            try {

                if (nameCell == null) {
                    throw new RuntimeException("la cantidad del extracto y nombre es obligatorio");
                }

                if (quantityCell == null) {
                    throw new RuntimeException("la cantidad del extracto es obligatoria");
                }

                if (nameCell == null || nameCell.getStringCellValue().isEmpty()) {
                    throw new RuntimeException("el nombre del extracto es obligatorio");
                }

                List<BodegaDTO> bodegaDTOs = generateBodegas(bodegaCell, quantityCell);

                extractosService.updateExtractoInventorys(new ExtractoRequest(
                        nameCell.getStringCellValue(),
                        getCellAsNullableString(descriptionCell),
                        bodegaDTOs,
                        getCellAsNullableDouble(ml22cell),
                        getCellAsNullableDouble(ml60cell),
                        getCellAsNullableDouble(ml125cell),
                        getCellAsNullableDouble(ml250cell),
                        getCellAsNullableDouble(ml500cell),
                        getCellAsNullableDouble(ml1000cell)), token);
                fileResponses.add(new FileResponse(nameCell.getStringCellValue(), "Extracto agregado correctamente"));
            } catch (Exception e) {

                fileResponses.add(new FileResponse(getCellAsString(nameCell), "Error, " + e.getMessage()));
            }

        }
        return fileResponses;
    }

    private String getCellAsString(Cell cell) {
        if (cell == null)
            return "";
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

    private List<BodegaDTO> generateBodegas(Cell bodegaCell, Cell quantityCell) {
        String[] bodegaNames;
        String[] quantities;

        if (bodegaCell.getStringCellValue().contains(",")) {
            bodegaNames = bodegaCell.getStringCellValue().split(",");
        } else {
            bodegaNames = new String[] { bodegaCell.getStringCellValue() };
        }

        switch (quantityCell.getCellType()) {
            case STRING:
                quantities = quantityCell.getStringCellValue().split(",");
                break;
            default:
                quantities = new String[] { String.valueOf(quantityCell.getNumericCellValue()) };
                break;
        }

        if (quantities.length != bodegaNames.length) {
            throw new RuntimeException("La cantidad de bodegas y cantidades no coinciden");
        }

        List<BodegaDTO> bodegaDTOs = new ArrayList<>();

        for (int i = 0; i < bodegaNames.length; i++) {
            int quantity = (int) Double.parseDouble(quantities[i].trim());
            bodegaDTOs.add(new BodegaDTO(bodegaNames[i].trim(), quantity));
        }
        return bodegaDTOs;
    }

    private String getCellAsNullableString(Cell cell) {
        if (cell == null) {
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
