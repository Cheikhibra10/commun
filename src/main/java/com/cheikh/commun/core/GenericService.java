package com.cheikh.commun.core;

import com.cheikh.commun.exceptions.EntityNotFoundException;
import com.cheikh.commun.services.MapperService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GenericService<T extends GenericEntity<T>, D, R> {

    /*
     * D : ResponseDto
     * R : ResquestDto
     * T : entity
     *  */

    private final GenericRepository<T> repository;

    protected Class<T> TClass;
    protected Class<D> DClass;

    public GenericService(GenericRepository<T> repository, Class<T> TClass, Class<D> DClass) {
        this.repository = repository;
        this.TClass = TClass;
        this.DClass = DClass;
    }

    public D get(Long id){
        T entity= repository.findById(id).orElse(null);
        return MapperService.mapToEntity(entity,DClass);
    }

//    @Transactional
//    public D update(T updated) {
//        T entity = repository.findById(updated.getId()).orElseThrow(() -> new EntityNotFoundException("Entité non trouvée"));
//        entity.update(updated);
//        repository.save(entity);
//        return MapperService.mapToEntity(entity, DClass);
//    }


    public D delete(long deleted){
        T entity = repository.findById(deleted).orElse(null);
        repository.deleteById(deleted);
        return MapperService.mapToEntity(entity,DClass);
    }

    @Transactional
    public D create(T newDomain){
        T entity = newDomain.createNewInstance();
        return MapperService.mapToEntity(entity,DClass);
    }

    public List<D> findAll() {
        List<T> entities =repository.findAll();
        return MapperService.mapToListEntity(entities,DClass);
    }

}
