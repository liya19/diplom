package ru.itis.diplom.service;

import lombok.Data;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.domain.BudgetInfo;
import ru.itis.diplom.dto.BudgetAnalytics;
import ru.itis.diplom.repository.BudgetDao;
import ru.itis.diplom.repository.SimpleDao;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;


@Component
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetDao budgetDao;

    @Autowired
    private SimpleDao simpleDao;

    @Override
    @Transactional
    public BudgetInfo getCurrentBudget() {
        return budgetDao.getCurrentBudget();
    }

    @Override
    @Transactional
    public void setBudget(Integer budget) {
        budgetDao.setBudget(budget);
    }

    @Override
    public List<BudgetInfo> getBudgetHistory() {
        return simpleDao.findAll(BudgetInfo.class);
    }

    @Override
    public BudgetAnalytics getCurrentBudgetAnalytics() {
        BudgetInfo budgetInfo = getCurrentBudget();

        if (budgetInfo == null) {
            return null;
        }

        int moneySpent = 0;

        BudgetAnalytics budgetAnalytics = new BudgetAnalytics();

        budgetAnalytics.setCurrentBudget(budgetInfo);

        Calendar dateFrom = GregorianCalendar
                .from(ZonedDateTime.ofInstant(budgetInfo.getDateFrom().toInstant(),
                        ZoneId.systemDefault()));


        Calendar dateTo = GregorianCalendar
                .from(ZonedDateTime.ofInstant(budgetInfo.getDateTo().toInstant(),
                        ZoneId.systemDefault()));

        int month = dateFrom.get(Calendar.MONTH) + 1;
        int remainingMonths = 5;

        List<BudgetInfo> budgetInfos = new ArrayList<>();
        while (month <= dateTo.get(Calendar.MONTH)) {
            Calendar now = GregorianCalendar.getInstance();
            now.setTime(new Date());


            BudgetInfo currentMonthBudget = new BudgetInfo();
            Calendar dateBegin = GregorianCalendar.getInstance();

            if (month <= 11) {
                dateBegin.set(Calendar.MONTH, month);
                dateBegin.set(Calendar.DAY_OF_MONTH, 0);
                dateBegin.set(Calendar.YEAR, dateFrom.get(Calendar.YEAR));
            } else {
                dateBegin.set(Calendar.MONTH, month - 12);
                dateBegin.set(Calendar.DAY_OF_MONTH, 0);
                dateBegin.set(Calendar.YEAR, dateFrom.get(Calendar.YEAR) + 1);
            }
            currentMonthBudget.setDateFrom(dateBegin.getTime());

            Calendar dateEnd = GregorianCalendar.getInstance();
            dateEnd.set(Calendar.DAY_OF_MONTH, 0);
            if (month < 11) {
                dateEnd.set(Calendar.MONTH, month + 1);
                dateEnd.set(Calendar.YEAR, dateBegin.get(Calendar.YEAR));
            } else if (month == 11) {
                dateEnd.set(Calendar.MONTH, 0);
                dateEnd.set(Calendar.YEAR, dateBegin.get(Calendar.YEAR) + 1);
            } else {
                dateEnd.set(Calendar.YEAR, dateBegin.get(Calendar.YEAR));
                dateEnd.set(Calendar.MONTH, dateBegin.get(Calendar.MONTH) + 1);
            }
            currentMonthBudget.setDateTo(dateEnd.getTime());
            currentMonthBudget.setRemainingBudget(budgetDao.calculateBudgetInMonth(dateBegin.getTime(), dateEnd.getTime()));
            if (now.get(Calendar.MONTH) + 1 >= month) {
                if (month > dateEnd.get(Calendar.MONTH)) {
                    remainingMonths = 12 + dateTo.get(Calendar.MONTH) - month;
                } else {
                    remainingMonths = dateTo.get(Calendar.MONTH) - month;
                }
            }
//            if (remainingMonths != 0) {
            currentMonthBudget.setFullBudget((budgetInfo.getFullBudget() - moneySpent) / (remainingMonths + 1));
//            } else {
//                currentMonthBudget.setFullBudget((budgetInfo.getFullBudget() - moneySpent));
//            }

            moneySpent += currentMonthBudget.getRemainingBudget();

            month++;
            budgetInfos.add(currentMonthBudget);
        }
        budgetAnalytics.setBudgetInfos(budgetInfos);

        return budgetAnalytics;
    }

}
