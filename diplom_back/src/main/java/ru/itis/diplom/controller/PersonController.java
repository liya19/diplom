package ru.itis.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.itis.diplom.domain.Person;
import ru.itis.diplom.dto.PersonDto;
import ru.itis.diplom.repository.PersonRepository;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping(value = "/api/person")
    @ResponseBody
    public List<Person> getPersons(@RequestParam(value = "lastName") String lastName) {
        return personRepository.findPersonByLastNameContaining(lastName);
    }
}
