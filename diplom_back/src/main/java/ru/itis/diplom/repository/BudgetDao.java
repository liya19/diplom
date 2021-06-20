package ru.itis.diplom.repository;

import ru.itis.diplom.domain.BudgetInfo;

import java.util.Date;

public interface BudgetDao {
    BudgetInfo getCurrentBudget();

    BudgetInfo setBudget(int budget);

    int calculateBudgetInMonth(Date dateFrom, Date dateTo);
}
