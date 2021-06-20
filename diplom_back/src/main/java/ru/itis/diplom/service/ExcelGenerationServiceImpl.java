package ru.itis.diplom.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbookType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.repository.ApplicationRepository;
import ru.itis.diplom.repository.SimpleDao;
import ru.itis.diplom.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ExcelGenerationServiceImpl implements ExcelGenerationService {

    @Autowired
    private Utils utils;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private SimpleDao simpleDao;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public ResponseEntity<InputStreamResource> excelFileForAllNotIssuedApplications(HttpServletResponse response) throws IOException {
        Workbook wb = getWorkbook(applicationRepository.findAllByStatusOrStatus(Application.Status.ACCEPTED, Application.Status.SUGGESTED));
        MediaType mediaType = MediaType.parseMediaType("application/vnd.ms-excel");

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        wb.write(stream);
        ByteArrayInputStream inStream = new ByteArrayInputStream(stream.toByteArray());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=applications.xlsx")
                .contentType(mediaType)
                .body(new InputStreamResource(inStream));
    }

    @Override
    @Transactional
    public Boolean parseExcelFile(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(file.getBytes()));
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = sheet.rowIterator();
            List<Long> issuedApplicationIdList = new ArrayList<>();
            List<Long> acceptedApplicationIdList = new ArrayList<>();
            if (iterator.hasNext()) iterator.next();
            while (iterator.hasNext()) {
                Row row = iterator.next();
                Cell cell = row.getCell(0);
                double id = cell.getNumericCellValue();
                if (id != 0D) {
                    Long longId = (long) id;
                    String status = row.getCell(5).getStringCellValue();
                    if (status.equals("+")) {
                        issuedApplicationIdList.add(longId);
                    } else if (status.equals("-")) {
                        acceptedApplicationIdList.add(longId);
                    }
                }
            }
            applicationService.issueApplications(issuedApplicationIdList);
            applicationService.makeApplicationsAccepted(acceptedApplicationIdList);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Workbook getWorkbook(List<Application> applications) {
        XSSFWorkbook workbook = new XSSFWorkbook(XSSFWorkbookType.XLSX);
        Sheet sheet = workbook.createSheet();

        applications = utils.sortApplication(applications);

        fillHeader(sheet);

        fillTableBody(sheet, applications);

        return workbook;
    }

    private void fillHeader(Sheet sheet) {
        Row header = sheet.createRow(0);
        Cell cellId = header.createCell(0);
        cellId.setCellValue("ID");
        Cell cellGroup = header.createCell(1);
        cellGroup.setCellValue("Группа");
        Cell cellFIO = header.createCell(2);
        cellFIO.setCellValue("ФИО");
        Cell cellCategory = header.createCell(3);
        cellCategory.setCellValue("Категория");
        Cell cellKind = header.createCell(4);
        cellKind.setCellValue("Тип");
        Cell cellStatus = header.createCell(5);
        cellStatus.setCellValue("Статус");
    }

    private void fillTableBody(Sheet sheet, List<Application> applications) {
        int rowNumb = 1;
        for (Application application : applications) {
            Row row = sheet.createRow(rowNumb++);

            Cell cellId = row.createCell(0);
            cellId.setCellValue(application.getId());
            Cell cellGroup = row.createCell(1);
            cellGroup.setCellValue(application.getPerson().getGroup());
            Cell cellFIO = row.createCell(2);
            cellFIO.setCellValue(application.getPerson().getLastName()
                    + " " + application.getPerson().getFirstName()
                    + " " + application.getPerson().getMiddleName());
            Cell cellCategory = row.createCell(3);
            StringJoiner joiner = new StringJoiner(", ");
            application.getCategories().forEach(category -> joiner.add(category.getName()));
            cellCategory.setCellValue(joiner.toString());
            Cell cellKind = row.createCell(4);
            cellKind.setCellValue(application.getDocumentKind().getName());
            Cell cellStatus = row.createCell(5);
            String status = "";
            switch (application.getStatus()) {
                case ACCEPTED: {
                    status = "-";
                    break;
                }
                case SUGGESTED: {
                    status = "+";
                    break;
                }
            }
            cellStatus.setCellValue(status);
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
    }
}
