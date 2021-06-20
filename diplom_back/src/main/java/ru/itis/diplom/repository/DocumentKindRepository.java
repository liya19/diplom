package ru.itis.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.diplom.domain.DocumentKind;

@Repository
public interface DocumentKindRepository extends JpaRepository<DocumentKind, Long> {

}
