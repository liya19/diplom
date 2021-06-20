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
import ru.itis.diplom.domain.DocumentKind;
import ru.itis.diplom.dto.DocumentKindDto;
import ru.itis.diplom.repository.DocumentKindRepository;
import ru.itis.diplom.repository.SimpleDao;
import ru.itis.diplom.utils.Utils;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class DocumentKindController {
    @Autowired
    private DocumentKindRepository documentKindRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private SimpleDao simpleDao;

    @PostMapping("/documentKind/save")
    public void createDocumentKind(@RequestBody DocumentKindDto documentKindDto) {
        DocumentKind documentKind = DocumentKind.builder()
                .name(documentKindDto.getName())
                .sum(documentKindDto.getSum())
                .build();
        documentKindRepository.save(documentKind);
    }

    @GetMapping("/documentKinds")
    @ResponseBody
    public List<DocumentKindDto> fetchDocumentKinds() {
        return utils.mapDocumentKinds(documentKindRepository.findAll());
    }

    @PatchMapping("/documentKind")
    @ResponseBody
    @Transactional
    public DocumentKindDto editDocumentKind(@RequestBody DocumentKindDto documentKindDto) {
        DocumentKind documentKind = simpleDao.findById(DocumentKind.class, documentKindDto.getId());
        documentKind.setName(documentKindDto.getName());
        documentKind.setSum(documentKindDto.getSum());
        simpleDao.update(documentKind);
        return documentKindDto;
    }

    @DeleteMapping("/documentKind/delete")
    public void remove(@RequestParam Long id) {
        documentKindRepository.deleteById(id);
    }
}
