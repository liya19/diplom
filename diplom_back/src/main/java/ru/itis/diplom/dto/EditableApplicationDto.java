package ru.itis.diplom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.diplom.domain.Application;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditableApplicationDto {
    private Long id;
    private List<Long> categories;
    private Long documentKind;
    private Date date;
    private String firstName;
    private String lastName;
    private String middleName;
    private String group;
    private String status;
}
