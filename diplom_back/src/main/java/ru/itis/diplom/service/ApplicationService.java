package ru.itis.diplom.service;

import ru.itis.diplom.domain.Application;
import ru.itis.diplom.dto.ApplicationDto;
import ru.itis.diplom.dto.CreationApplicationDto;
import ru.itis.diplom.dto.EditableApplicationDto;

import java.util.List;

public interface ApplicationService {
    void submitApplication(CreationApplicationDto editableApplicationDto);

    List<Application> getIssuedApplications();

    List<Application> getApplications();

    List<Application> getAllApplications();

    ApplicationDto update(EditableApplicationDto editableApplicationDto);

    void remove(Long id);

    List<ApplicationDto> suggest();

    void issueApplications(List<Long> listIds);

    void makeApplicationsAccepted(List<Long> listIds);
}
