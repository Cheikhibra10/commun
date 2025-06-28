package com.cheikh.commun.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MapperService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.registerModule(new JavaTimeModule());
    }

    private MapperService() {
        // Empêche l'instanciation
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T, R> T mapToEntity(R source, Class<T> targetClass) {
        return MAPPER.convertValue(source, targetClass);
    }

    public static <T, R> T mapToDtoResponse(R source, Class<T> targetClass) {
        return MAPPER.convertValue(source, targetClass);
    }

    public static <T, R> List<T> mapToListEntity(List<R> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> MAPPER.convertValue(source, targetClass))
                .collect(Collectors.toList());
    }

    public static <T, R> List<T> mapToListDto(List<R> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> MAPPER.convertValue(source, targetClass))
                .collect(Collectors.toList());
    }

    public static <T, D> void patchEntityFromDto(T entity, D dto) {
        //BeanWrapper: une classe de Spring qui me permet d'accéder dynamiquement aux propriétés d’un objet
        BeanWrapper entityWrapper = new BeanWrapperImpl(entity);
        BeanWrapper dtoWrapper = new BeanWrapperImpl(dto);

        Set<String> excludedFields = getRelationalFieldNames(entity.getClass());
        excludedFields.add("id"); // Toujours exclure "id"

        //PropertyDescriptor: une propriété JavaBean qui représente les champs d’un objet
        for (PropertyDescriptor pd : dtoWrapper.getPropertyDescriptors()) {
            String name = pd.getName();

            if (excludedFields.contains(name)) continue;

            Object value = dtoWrapper.getPropertyValue(name);
            if (value != null && entityWrapper.isWritableProperty(name)) {
                entityWrapper.setPropertyValue(name, value);
            }
        }
    }

    private static Set<String> getRelationalFieldNames(Class<?> entityClass) {
        Set<String> relations = new HashSet<>();
        for (Field field : entityClass.getDeclaredFields()) {
            if (isRelationalField(field)) {
                relations.add(field.getName());
            }
        }
        return relations;
    }

    private static boolean isRelationalField(Field field) {
        return field.isAnnotationPresent(ManyToOne.class) ||
                field.isAnnotationPresent(OneToOne.class) ||
                field.isAnnotationPresent(OneToMany.class) ||
                field.isAnnotationPresent(ManyToMany.class);
    }
}


