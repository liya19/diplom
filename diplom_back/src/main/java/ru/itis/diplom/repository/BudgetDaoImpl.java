package ru.itis.diplom.repository;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.HibernateCallback;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.domain.BudgetInfo;

import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Repository
public class BudgetDaoImpl extends HibernateDaoSupport implements BudgetDao {

    public BudgetDaoImpl(SessionFactory sessionfactory) {
        setSessionFactory(sessionfactory);
    }

    @Override
    @Transactional
    public BudgetInfo getCurrentBudget() {
        Calendar now = GregorianCalendar.from(ZonedDateTime.now());

        Calendar date = GregorianCalendar.getInstance();

        date.set(Calendar.YEAR, now.get(Calendar.YEAR));
        date.set(Calendar.DAY_OF_MONTH, 1);

        if (now.get(Calendar.MONTH) > Calendar.JUNE) {
            date.set(Calendar.MONTH, Calendar.SEPTEMBER);
        } else {
            date.set(Calendar.MONTH, Calendar.FEBRUARY);
        }
        DetachedCriteria criteria = DetachedCriteria.forClass(BudgetInfo.class);

        criteria.add(Restrictions.le("dateFrom", date.getTime()));
        criteria.add(Restrictions.ge("dateTo", date.getTime()));


        List<BudgetInfo> budgetInfos = (List<BudgetInfo>) getHibernateTemplate()
                .findByCriteria(criteria, 0, 1);
        if (budgetInfos.size() > 0) {
            return budgetInfos.get(0);
        } else {
            return null;
        }
    }


    @Override
    @Transactional
    public BudgetInfo setBudget(int budget) {
        Calendar now = GregorianCalendar.from(ZonedDateTime.now());

        int currentYear = now.get(Calendar.YEAR);

        Calendar semBegin = GregorianCalendar.getInstance();
        Calendar semEnd = GregorianCalendar.getInstance();

        if (now.get(Calendar.MONTH) > Calendar.JUNE) {
            semBegin.set(Calendar.MONTH, Calendar.SEPTEMBER);
            semEnd.set(Calendar.MONTH, Calendar.FEBRUARY);

            semBegin.set(Calendar.DAY_OF_MONTH, 0);
            semEnd.set(Calendar.DAY_OF_MONTH, 0);

            semBegin.set(Calendar.YEAR, currentYear);
            semEnd.set(Calendar.YEAR, currentYear + 1);
        } else {
            semBegin.set(Calendar.MONTH, Calendar.FEBRUARY);
            semEnd.set(Calendar.MONTH, Calendar.JUNE);

            semBegin.set(Calendar.DAY_OF_MONTH, 0);
            semEnd.set(Calendar.DAY_OF_MONTH, 29);

            semBegin.set(Calendar.YEAR, currentYear);
            semEnd.set(Calendar.YEAR, currentYear);
        }

        BudgetInfo budgetInfo = getCurrentBudget();
        if (budgetInfo == null) {
            budgetInfo = new BudgetInfo();

            budgetInfo.setDateFrom(semBegin.getTime());
            budgetInfo.setDateTo(semEnd.getTime());
            budgetInfo.setFullBudget(budget);
            budgetInfo.setRemainingBudget(budget);

            getHibernateTemplate().save(budgetInfo);
        } else {
            budgetInfo.setFullBudget(budgetInfo.getFullBudget() + budget);
            budgetInfo.setRemainingBudget(budgetInfo.getRemainingBudget() + budget);

            getHibernateTemplate().update(budgetInfo);
        }
        return budgetInfo;
    }

    @Override
    @Transactional
    public int calculateBudgetInMonth(Date dateFrom, Date dateTo) {
        return getHibernateTemplate().execute(session -> {
            Criteria criteria = session.createCriteria(Application.class);
            criteria.add(Restrictions.eq("status", Application.Status.ISSUED));
            criteria.add(Restrictions.between("issuedDate", dateFrom, dateTo));

            List<Application> applications = criteria.list();
            int budget = 0;
            budget += applications.stream().mapToInt((application) -> application.getDocumentKind().getSum()).sum();
            return budget;
        });
    }
}
