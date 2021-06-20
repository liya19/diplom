package ru.itis.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.itis.diplom.domain.Category;
import ru.itis.diplom.domain.DocumentKind;
import ru.itis.diplom.dto.CategoryDto;
import ru.itis.diplom.dto.DocumentKindDto;
import ru.itis.diplom.repository.CategoryRepository;
import ru.itis.diplom.repository.SimpleDao;
import ru.itis.diplom.utils.Utils;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private Utils utils;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SimpleDao simpleDao;

    @PostMapping("/category/save")
    public void createCategory(@RequestBody CategoryDto categoryDto) {
        Category category = Category.builder()
                .name(categoryDto.getName())
                .priority(categoryDto.getPriority())
                .build();
        categoryRepository.save(category);
    }

    @GetMapping("/categories")
    @ResponseBody
    @Transactional
    public List<CategoryDto> fetchCategories() {
        return utils.mapCategories(categoryRepository.findAll());
    }

    @PatchMapping("/category")
    @ResponseBody
    @Transactional
    public CategoryDto editCategory(@RequestBody CategoryDto categoryDto) {
        Category category = simpleDao.findById(Category.class, categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setPriority(categoryDto.getPriority());
        simpleDao.update(category);
        return categoryDto;
    }

    @DeleteMapping("/category/delete")
    public void remove(@RequestParam Long id) {
        categoryRepository.deleteById(id);
    }
}
