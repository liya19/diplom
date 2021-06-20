package ru.itis.diplom.repository;

import java.io.Serializable;
import java.util.List;

public interface SimpleDao {
    <T> T findById(Class<T> entityClass, Serializable id);

    <T> T findByField(Class<T> entityClass, String fieldName, String value);

    <T> List<T> findAllByField(Class<T> entityClass, String fieldName, String value);

    <T> Serializable save(T entity);

    <T> void saveOrUpdate(T entity);

    <T> void update(T entity);

    <T> void delete(T entity);

    void flush();

    <T> List<T> findAll(Class<T> entityClass);
}