package ru.itis.diplom.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreationApplicationDto {
    private Long id;
    private List<Long> categories;
    private List<Long> documentKind;
    private Date date;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;
    private String group;
    private String status;
}
