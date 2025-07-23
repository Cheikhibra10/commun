package com.cheikh.commun.services.base;

import com.cheikh.commun.core.PageResponse;

import java.util.List;
import java.util.Map;

public interface DefaultService<T, D, R> {
    R create(D d);
    R update(long id, D d);
    PageResponse<R> findAll(int page, int size);
    R delete(Long id);
    R archive(Long id);
    R getById(Long id);
    T getEntityById(Long id);
     R restore(Long id);
     R patchFields(Long id, Map<String, Object> fields);
}
