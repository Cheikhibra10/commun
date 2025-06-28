package com.cheikh.commun.config;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EntityUtils {

    public static <T> void updateNonNullFields(T target, T source) {
        if (source == null || target == null) return;

        Class<?> clazz = source.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                // ignore les champs static ou final ou id
                if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers()) || field.getName().equals("id")) {
                    continue;
                }

                Object value = field.get(source);
                if (value != null || field.getType().isPrimitive()) {
                    field.set(target, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erreur mise à jour: " + field.getName(), e);
            }
        }
    }

    public static <T, D> void patchEntityFromDto(T entity, D dto) {
        BeanWrapper entityWrapper = new BeanWrapperImpl(entity);
        BeanWrapper dtoWrapper = new BeanWrapperImpl(dto);

        for (PropertyDescriptor pd : dtoWrapper.getPropertyDescriptors()) {
            String name = pd.getName();
            if ("id".equalsIgnoreCase(name)) continue;

            Object value = dtoWrapper.getPropertyValue(name);
            if (value != null && entityWrapper.isWritableProperty(name)) {
                if (!isEntity(value)) { // Exclure les entités liées
                    entityWrapper.setPropertyValue(name, value);
                }
            }
        }
    }

    public static boolean isEntity(Object obj) {
        // Simple rule: si l'objet a un champ @Id → c'est une entité
        return obj != null && obj.getClass().isAnnotationPresent(jakarta.persistence.Entity.class);
    }

}