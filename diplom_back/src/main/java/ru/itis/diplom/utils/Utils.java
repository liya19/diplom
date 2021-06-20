package ru.itis.diplom.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.domain.Category;
import ru.itis.diplom.domain.DocumentKind;
import ru.itis.diplom.dto.ApplicationDto;
import ru.itis.diplom.dto.CategoryDto;
import ru.itis.diplom.dto.DocumentKindDto;
import ru.itis.diplom.repository.ApplicationDao;
import ru.itis.diplom.repository.DocumentKindRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Utils {
    @Autowired
    DocumentKindRepository documentKindRepository;

    @Autowired
    ApplicationDao applicationDao;

    public List<Application> sortApplication(List<Application> applications) {
        Map<Application, Float> coefficientMap = new HashMap<>();
        Map<Long, Integer> documentKindCoefficient = new HashMap<>();
        List<DocumentKind> documentKinds = documentKindRepository.findAll();

        documentKinds.sort((o1, o2) -> o2.getSum() - o1.getSum());
        Integer prevSum = documentKinds.get(0).getSum();
        int countDistinctSum = 0;
        documentKindCoefficient.put(documentKinds.get(0).getId(), 0);
        for (int i = 1; i < documentKinds.size(); i++) {
            if (documentKinds.get(i).getSum() < prevSum) {
                prevSum = documentKinds.get(i).getSum();
                countDistinctSum++;
            }
            documentKindCoefficient.put(documentKinds.get(i).getId(), countDistinctSum);
        }

        applications.forEach(application -> {
            coefficientMap.putIfAbsent(application, 0F);
            Float categoryCoefficient = 0F;
            categoryCoefficient += application.getCategories().stream()
                    .map(Category::getPriority).reduce(Float::sum).orElse(0F);
            coefficientMap.put(application, coefficientMap.get(application) + categoryCoefficient);

            coefficientMap.put(application, coefficientMap.get(application)
                    + documentKindCoefficient.get(application.getDocumentKind().getId()).floatValue() * 0.5F);
        });

        Map<Long, Long> personRepeat = new HashMap<>();

        List<Application> semesterApplications = applicationDao.findAllApplicationsInThisSem();

        Map<Long, Long> countOfIssuedApplicationOfPerson = new HashMap<>();

        semesterApplications.forEach(application -> {
            if (application.getStatus().equals(Application.Status.ISSUED)) {
                countOfIssuedApplicationOfPerson.merge(application.getPerson().getId(), 1L, Long::sum);
            }
        });

        coefficientMap.entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue() - o2.getValue() == 0) {
                        return o2.getKey().getDate().compareTo(o1.getKey().getDate());
                    } else {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                })
                .map(Map.Entry::getKey)
                .forEachOrdered(application -> {
                    if (personRepeat.get(application.getPerson().getId()) == null) {
                        personRepeat.put(application.getPerson().getId(), 1L);
                    } else {
                        personRepeat.put(application.getPerson().getId(),
                                personRepeat.get(application.getPerson().getId()) + 1L);
                        coefficientMap.put(application, coefficientMap.get(application) -
                                (personRepeat.get(application.getPerson().getId()) * 4));
                    }
                    if (countOfIssuedApplicationOfPerson.get(application.getPerson().getId()) != null)
                        coefficientMap.put(application, coefficientMap.get(application) - (
                                countOfIssuedApplicationOfPerson.get(application.getPerson().getId()).floatValue() / 2));
                });
        applications = coefficientMap.entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getValue() - o2.getValue() == 0) {
                        return o2.getKey().getDate().compareTo(o1.getKey().getDate());
                    } else {
                        return o2.getValue().compareTo(o1.getValue());
                    }
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        return applications;
    }

    public List<ApplicationDto> parseApplications(List<Application> applications) {
        return applications.stream().map(this::parseApplication)
                .collect(Collectors.toList());
    }

    public ApplicationDto parseApplication(Application application) {
        return ApplicationDto.builder()
                .id(application.getId())
                .firstName(application.getPerson().getFirstName())
                .lastName(application.getPerson().getLastName())
                .middleName(application.getPerson().getMiddleName())
                .documentKind(mapDocumentKind(application.getDocumentKind()))
                .categories(mapCategories(application.getCategories()))
                .status(application.getStatus().toString())
                .group(application.getPerson().getGroup())
                .date(application.getDate())
                .build();
    }

    public CategoryDto mapCategory(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .id(category.getId())
                .priority(category.getPriority())
                .build();
    }


    public List<CategoryDto> mapCategories(List<Category> categories) {
        return categories.stream().map(this::mapCategory).collect(Collectors.toList());
    }

    public DocumentKindDto mapDocumentKind(DocumentKind documentKind) {
        return DocumentKindDto.builder()
                .id(documentKind.getId())
                .sum(documentKind.getSum())
                .name(documentKind.getName()).build();
    }

    public List<DocumentKindDto> mapDocumentKinds(List<DocumentKind> documentKinds) {
        return documentKinds.stream().map(this::mapDocumentKind).collect(Collectors.toList());
    }
}

