package ru.itis.diplom.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.domain.BudgetInfo;
import ru.itis.diplom.domain.Category;
import ru.itis.diplom.domain.DocumentKind;
import ru.itis.diplom.domain.Person;
import ru.itis.diplom.dto.ApplicationDto;
import ru.itis.diplom.dto.CreationApplicationDto;
import ru.itis.diplom.dto.EditableApplicationDto;
import ru.itis.diplom.repository.ApplicationRepository;
import ru.itis.diplom.repository.CategoryRepository;
import ru.itis.diplom.repository.DocumentKindRepository;
import ru.itis.diplom.repository.PersonRepository;
import ru.itis.diplom.repository.SimpleDao;
import ru.itis.diplom.utils.Utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ApplicationServiceImpl implements ApplicationService {
    @Autowired
    private Utils utils;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private DocumentKindRepository documentKindRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailSendService emailSendService;

    @Autowired
    private SimpleDao simpleDao;

    @Autowired
    private BudgetService budgetService;

    @Override
    @Transactional
    public void submitApplication(CreationApplicationDto editableApplicationDto) {
        List<Category> categories = editableApplicationDto.getCategories() != null ?
                categoryRepository.findAllById(editableApplicationDto.getCategories()) : Collections.emptyList();

        Optional<Person> personOpt = personRepository
                .findPersonByFirstNameAndMiddleNameAndLastName(editableApplicationDto.getFirstName(),
                        editableApplicationDto.getMiddleName(),
                        editableApplicationDto.getLastName());
        Person person;
        if (personOpt.isPresent()) {
            person = personOpt.get();
            person.setCategories(categories);
        } else {
            person = Person.builder()
                    .firstName(editableApplicationDto.getFirstName())
                    .lastName(editableApplicationDto.getLastName())
                    .middleName(editableApplicationDto.getMiddleName())
                    .categories(categories)
                    .email(editableApplicationDto.getEmail())
                    .group(editableApplicationDto.getGroup())
                    .build();
        }

        personRepository.save(person);
        for (Long documentKindId : editableApplicationDto.getDocumentKind()) {

            Optional<DocumentKind> documentKind = documentKindRepository.findById(documentKindId);

            if (documentKind.isPresent()) {
                Application application = Application
                        .builder()
                        .categories(categories)
                        .date(editableApplicationDto.getDate())
                        .person(person)
                        .documentKind(documentKind.get())
                        .status(Application.Status.ACCEPTED)
                        .build();

                if (application.getPerson().getEmail() != null) {
                    new Thread(() -> emailSendService.sendMessageAboutAcceptedApplication(application)).start();
                }

                applicationRepository.save(application);
            }
        }
    }

    @Override
    public List<Application> getIssuedApplications() {
        return applicationRepository.findAllByStatus(Application.Status.ISSUED);
    }

    @Override
    public List<Application> getApplications() {
        return applicationRepository.findAllByStatusOrStatus(Application.Status.ACCEPTED, Application.Status.SUGGESTED);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    @Override
    @Transactional
    public ApplicationDto update(EditableApplicationDto editableApplicationDto) {
        Application application;
        if (Objects.nonNull(editableApplicationDto.getId())) {
            application = simpleDao.findById(Application.class, editableApplicationDto.getId());
        } else {
            throw new IllegalArgumentException("There is no application with such id");
        }

        List<Category> categories = editableApplicationDto.getCategories() != null ?
                categoryRepository.findAllById(editableApplicationDto.getCategories()) : Collections.emptyList();

        Optional<Person> personOpt = personRepository
                .findPersonByFirstNameAndMiddleNameAndLastName(editableApplicationDto.getFirstName(),
                        editableApplicationDto.getMiddleName(),
                        editableApplicationDto.getLastName());
        Person person;
        if (personOpt.isPresent()) {
            person = personOpt.get();
            person.setCategories(categories);
        } else {
            person = Person.builder()
                    .firstName(editableApplicationDto.getFirstName())
                    .lastName(editableApplicationDto.getLastName())
                    .middleName(editableApplicationDto.getMiddleName())
                    .categories(categories)
                    .build();
            personRepository.save(person);
        }

        Optional<DocumentKind> documentKind = documentKindRepository.findById(editableApplicationDto.getDocumentKind());

        if (documentKind.isPresent()) {
            application.setCategories(categories);
            application.setDocumentKind(documentKind.get());
            application.setDate(editableApplicationDto.getDate());
            application.setPerson(person);
            application.setStatus(Application.Status.valueOf(editableApplicationDto.getStatus()));
        }
        simpleDao.update(application);
        return utils.parseApplication(application);
    }

    @Override
    @Transactional
    public void remove(Long id) {
        applicationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<ApplicationDto> suggest() {
        this.updateSuggested();
        List<Application> applications = utils.sortApplication(applicationRepository.findAllByStatus(Application.Status.ACCEPTED));

        BudgetInfo currentBudget = budgetService.getCurrentBudget();

        assert currentBudget != null;

        Calendar now = GregorianCalendar.from(ZonedDateTime.now());

        Calendar dateEnd = GregorianCalendar
                .from(ZonedDateTime.ofInstant(currentBudget.getDateTo().toInstant(),
                        ZoneId.systemDefault()));

        int budget;
        if (now.getTime().after(currentBudget.getDateFrom())) {
            int remainingMonths;
            if (now.get(Calendar.MONTH) > dateEnd.get(Calendar.MONTH)) {
                remainingMonths = 12 + dateEnd.get(Calendar.MONTH) - now.get(Calendar.MONTH);
            } else {
                remainingMonths = dateEnd.get(Calendar.MONTH) - now.get(Calendar.MONTH);
            }
            budget = currentBudget.getRemainingBudget() / (remainingMonths + 1);
        } else {
            budget = currentBudget.getRemainingBudget() / 5;
        }

        List<Application> result = new ArrayList<>();
        for (Application application : applications) {
            if (budget - application.getDocumentKind().getSum() >= 0) {
                budget -= application.getDocumentKind().getSum();
                application.setStatus(Application.Status.SUGGESTED);
                simpleDao.update(application);
                result.add(application);
            } else break;
        }
        return utils.parseApplications(result);
    }

    @Override
    @Transactional
    public void issueApplications(List<Long> listIds) {
        this.updateSuggested();
        List<Application> applications = applicationRepository.findAllById(listIds);

        int moneySpent = applications
                .stream()
                .mapToInt(application -> application.getDocumentKind().getSum())
                .sum();

        BudgetInfo budgetInfo = budgetService.getCurrentBudget();

        if (budgetInfo == null) {
            throw new IllegalArgumentException("Budget is not defined");
        }

        budgetInfo.setRemainingBudget(budgetInfo.getRemainingBudget() - moneySpent);

        simpleDao.update(budgetInfo);

        applications.forEach(application -> {
            if (application.getPerson().getEmail() != null) {
                new Thread(() -> emailSendService.sendMessageAboutIssuedApplication(application)).start();
            }
            application.setStatus(Application.Status.ISSUED);
            simpleDao.update(application);
        });


    }

    @Override
    @Transactional
    public void makeApplicationsAccepted(List<Long> listIds) {
        List<Application> applications = applicationRepository.findAllById(listIds);
        applications.forEach(application -> {
            application.setStatus(Application.Status.ACCEPTED);
            simpleDao.update(application);
        });
    }

    private void updateSuggested() {
        List<Application> applications = applicationRepository.findAllByStatus(Application.Status.SUGGESTED);
        applications.forEach(application -> {
            application.setStatus(Application.Status.ACCEPTED);
            simpleDao.update(application);
        });
    }
}
