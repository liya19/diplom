package ru.itis.diplom.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
public class HibernateConfig {
    @Value("${spring.jpa.database-platform}")
    private String hibernateDialect;

    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean createLocalSessionFactoryBean(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setPackagesToScan("ru.itis.diplom.domain");
        sessionFactoryBean.getHibernateProperties().put(
                "hibernate.dialect",
                hibernateDialect);
        sessionFactoryBean.getHibernateProperties().put("hibernate.hbm2ddl.auto", "update");
        return sessionFactoryBean;
    }

    @Bean(name = "hibernateTemplate")
    public HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
        HibernateTemplate hibernateTemplate = new HibernateTemplate();
        hibernateTemplate.setSessionFactory(sessionFactory);
        return hibernateTemplate;

    }
}
