package ru.itis.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.diplom.domain.Person;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findPersonByFirstNameAndMiddleNameAndLastName(String firstName, String middleName, String lastName);

    List<Person> findPersonByLastNameContaining(String lastName);
}
