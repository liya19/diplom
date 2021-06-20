package ru.itis.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.diplom.domain.Application;

@Service
public class EmailSendServiceImpl implements EmailSendService {

    @Autowired
    private JavaMailSender emailSender;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendMessageAboutIssuedApplication(
            Application application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(application.getPerson().getEmail());
        message.setSubject("Вам назначена материальная помощь");
        message.setText(application.getPerson().getLastName() + " "
                + application.getPerson().getFirstName() +
                ", вам была назначена мат. помощь на сумму "
                + application.getDocumentKind().getSum() + " рублей");
        emailSender.send(message);
    }

    @Override
    public void sendMessageAboutAcceptedApplication(Application application) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(application.getPerson().getEmail());
        message.setSubject("Ваше заявление принято.");
        message.setText(application.getPerson().getLastName() + " "
                + application.getPerson().getFirstName() +
                ", ваше заявление на  \""
                + application.getDocumentKind().getName() + "\" было принято");
        emailSender.send(message);
    }
}
