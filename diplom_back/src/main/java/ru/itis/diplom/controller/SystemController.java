package ru.itis.diplom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.diplom.domain.BudgetInfo;
import ru.itis.diplom.dto.BudgetAnalytics;
import ru.itis.diplom.service.BudgetService;

import java.util.List;

@RestController
public class SystemController {
    @Autowired
    private BudgetService budgetService;

    @GetMapping("/system/budget")
    public BudgetInfo getBudget() {
        return budgetService.getCurrentBudget();
    }

    @GetMapping("/system/budget/history")
    public List<BudgetInfo> getBudgetHistory() {
        return budgetService.getBudgetHistory();
    }

    @PostMapping("/system/budget")
    public void setBudget(@RequestParam(value = "budget") Integer budget) {
        budgetService.setBudget(budget);
    }

    @GetMapping("/system/budget/analytics")
    public BudgetAnalytics getCurrentMonthAnalytics() {
        return budgetService.getCurrentBudgetAnalytics();
    }
}
