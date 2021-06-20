package ru.itis.diplom.repository;

import ru.itis.diplom.domain.Application;

import java.util.List;

public interface ApplicationDao {
    void save(Application application);

    Application update(Application application);

    List<Application> findAll();

    List<Application> findAllApplicationsInThisSem();
}
