package com.cheikh.commun.services.base;

import java.util.List;

public interface DefaultService<T, D, R> {
    R create(D d);
    R update(long id, D d);
    List<R> findAll();
    R delete(Long id);
    R archive(Long id);
    R getById(Long id);
    T getEntityById(Long id);
}
