package ru.itis.diplom.dto;

import lombok.Data;
import ru.itis.diplom.domain.BudgetInfo;

import java.util.List;
import java.util.Map;

@Data
public class BudgetAnalytics {
    private BudgetInfo currentBudget;
    List<BudgetInfo> budgetInfos;
}
