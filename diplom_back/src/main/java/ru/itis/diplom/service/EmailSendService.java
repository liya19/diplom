package ru.itis.diplom.service;

import ru.itis.diplom.domain.Application;

public interface EmailSendService {
    void sendMessageAboutIssuedApplication(Application application);
    void sendMessageAboutAcceptedApplication(Application application);
}
