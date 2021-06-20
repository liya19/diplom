package ru.itis.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.diplom.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
