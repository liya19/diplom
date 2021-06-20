package ru.itis.diplom.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public interface ExcelGenerationService {
    ResponseEntity<InputStreamResource> excelFileForAllNotIssuedApplications(HttpServletResponse response) throws IOException;

    Boolean parseExcelFile(MultipartFile file);
}
