package com.utils;

public interface CrudRepository <T>{
    void save(T entity);
    void deleteById(Long id);
    T getById(Long id);
    boolean exists(Long id);
    void clearAll();
    int count();
}
