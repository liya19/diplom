package ru.itis.diplom.repository;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.diplom.domain.Application;
import ru.itis.diplom.domain.BudgetInfo;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.itis.diplom.domain.Application.Status;

@Repository
public class ApplicationDaoImpl extends HibernateDaoSupport implements ApplicationDao {

    @Autowired
    private BudgetDao budgetDao;

    public ApplicationDaoImpl(SessionFactory sessionfactory) {
        setSessionFactory(sessionfactory);
    }

    @Override
    public void save(Application application) {
        getHibernateTemplate().save(application);
    }

    @Override
    @Transactional
    public Application update(Application application) {
        getHibernateTemplate().update(application);
        return application;
    }

    @Override
    public List<Application> findAll() {
        return getHibernateTemplate().loadAll(Application.class);
    }

    public List<Application> findAllApplicationsInThisSem() {
        BudgetInfo sem = budgetDao.getCurrentBudget();
        return getHibernateTemplate().execute((session) -> {
            Criteria c = session.createCriteria(Application.class);
            c.add(Restrictions.between("date", sem.getDateFrom(), sem.getDateTo()));
            c.add(Restrictions.eq("status", Status.ISSUED));
            return c.list();
        });
    }
}
