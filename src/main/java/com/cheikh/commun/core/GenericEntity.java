package com.cheikh.commun.core;

import com.cheikh.commun.config.EntityUtils;

public interface GenericEntity<T> {
    // update current instance with provided data
   default void update(T source){
        EntityUtils.updateNonNullFields(this, source);
    };


    Long getId();

    T createNewInstance();
}
