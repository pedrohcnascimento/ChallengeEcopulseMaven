package br.com.EcoPulse.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GenericRepository<T> {
    private final List<T> data = new ArrayList<>();

    public T save(T entity) {
        data.add(entity);
        return entity;
    }

    public List<T> findAll() {
        return new ArrayList<>(data);
    }

    public void delete(T entity) {
        data.remove(entity);
    }
}
