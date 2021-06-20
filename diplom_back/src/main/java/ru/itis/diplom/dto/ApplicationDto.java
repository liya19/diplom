package ru.itis.diplom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.diplom.domain.Application;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private List<CategoryDto> categories;
    private DocumentKindDto documentKind;
    private Date date;
    private String status;
    private Long month;
    private String group;
}
