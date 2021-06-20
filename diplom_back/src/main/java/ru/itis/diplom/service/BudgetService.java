package ru.itis.diplom.service;

import ru.itis.diplom.domain.BudgetInfo;
import ru.itis.diplom.dto.BudgetAnalytics;

import java.util.List;

public interface BudgetService {
    BudgetInfo getCurrentBudget();

    void setBudget(Integer budget);

    List<BudgetInfo> getBudgetHistory();

    BudgetAnalytics getCurrentBudgetAnalytics();
}
