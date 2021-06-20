package ru.itis.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.itis.diplom.dto.ApplicationDto;
import ru.itis.diplom.dto.CreationApplicationDto;
import ru.itis.diplom.dto.EditableApplicationDto;
import ru.itis.diplom.service.ApplicationService;
import ru.itis.diplom.service.ExcelGenerationService;
import ru.itis.diplom.utils.Utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private Utils utils;

    @Autowired
    private ExcelGenerationService excelGenerationService;

    @PostMapping("/application/save")
    @ResponseBody
    public List<ApplicationDto> submitApplication(@RequestBody CreationApplicationDto editableApplicationDto) {
        applicationService.submitApplication(editableApplicationDto);
        return utils.parseApplications(applicationService.getApplications());
    }

    @GetMapping("/applications")
    @ResponseBody
    @Transactional
    public List<ApplicationDto> getApplications() {
        return utils.parseApplications(applicationService.getApplications());
    }

    @GetMapping("/applications/issued")
    @ResponseBody
    public List<ApplicationDto> getIssuedApplication() {
        return utils.parseApplications(applicationService.getIssuedApplications());
    }


    @PatchMapping("/application")
    @ResponseBody
    public ApplicationDto updateApplication(@RequestBody EditableApplicationDto editableApplicationDto) {
        return applicationService.update(editableApplicationDto);
    }

    @DeleteMapping("/application/delete")
    @ResponseBody
    public void remove(@RequestParam(value = "id") Long id) {
        applicationService.remove(id);
    }

    @PostMapping("/application/suggest")
    @ResponseBody
    public List<ApplicationDto> suggest() {
        applicationService.suggest();
        return utils.parseApplications(applicationService.getApplications());
    }

    @GetMapping("/application/submit")
    @ResponseBody
    public List<ApplicationDto> submit(@RequestParam List<Long> listIds) {
        if (listIds != null)
            applicationService.issueApplications(listIds);
        return utils.parseApplications(applicationService.getApplications());
    }

    @GetMapping("/application/excel/download")
    @CrossOrigin
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFile(HttpServletResponse response) throws IOException {
        return excelGenerationService.excelFileForAllNotIssuedApplications(response);
    }

    @RequestMapping(value = "/application/excel/import", method = RequestMethod.POST)
    @ResponseBody
    public List<ApplicationDto> submit(@RequestParam("file") MultipartFile file) {
        if (excelGenerationService.parseExcelFile(file)) {
            return utils.parseApplications(applicationService.getAllApplications());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
