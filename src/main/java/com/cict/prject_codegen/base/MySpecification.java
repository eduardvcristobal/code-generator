package com.cict.prject_codegen.base;

import com.cict.core.base.FilterCriteria;
import com.cict.core.base.FilterRequest;
import com.cict.core.base.QueryOperator;
import com.cict.prject_codegen.dto.BaseDTO;
import com.cict.prject_codegen.dao.BaseRepository;
import com.cict.prject_codegen.model.BaseEntity;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.*;

//MySpecification contains the following code: ModelMapper, EntityManager, and MapperService, primaryEntityManager
//E=Entity
//D=DTO
//R=Repository
//allows you to create a reusable code that can work with different data types
//The R generic type is defined as extending the BaseRepository class, which means that any implementation of the MySpecification abstract class must provide a repository that extends the BaseRepository
//We created a BaseRepository so that we can pass any Repository in the subclass and use its methods.
//Since subclass Repository extends BaseRepository, the super class should extend BaseRepository as well.
//if Author Repositoy extends BaseRepository, Book Repository extends BaseRepository, etc.
//In the generic we can use {R extends BaseRepository<E,Long>}
//The E and D are the Entity and DTO. The R is the Repository
//Since E and D does not have extends, then the usage of this will just be use a a Return Type. like example List<E> findAll();
//for specification, you can use the E and D as a parameter. like example Specification<E> specification = (root, query, criteriaBuilder) -> {
@Getter
public abstract class MySpecification<E extends BaseEntity, D extends BaseDTO, R extends BaseRepository<E,Long>> extends MyMapper<E, D> {

    R repository;
    Class<E> entityClass;
    Class<?> dtoClass;

    //The R Repository is created an instance of the repository in the constructor and extends the BaseRepository.
    // For Example you Pass a bookRepository from the subclass, the bookRepository should also extends the BaseRepository
    // if you dont Extends it will not know its other methods like. find all, find by id, delete by id, etc.
    //the instance is being initialized in {ServiceImpl} when you call super(repository, entityClass, dtoClass); in the constructor
    public MySpecification(R repository, Class<E> entityClass,  Class<?> dtoClass) {
        this.repository = repository;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    public E createOne(long sbuId, D resource) {
        resource.setSbuId(String.valueOf(sbuId));
        E entity = modelMapper.map(resource, getEntityClass());
        // call external -> entity mapper to further update  the entity data manually
        entity = (E)resource.to(entity, modelMapper);
        return  getRepository().save( entity );
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Optional<E> findById(Long id) {
        return repository.findById(id);
    }

    public List<E> findAll() {
        return (List<E>) repository.findAll();
    }

    public Page<E> findAll(Pageable pageable) {
        Page<E> entityPage = repository.findAll(pageable);
        List<E> entities = entityPage.getContent();
        return new PageImpl<>(entities, pageable, entityPage.getTotalElements());
    }

    /*Page<Book> bookPage = bookService.findAll(pageable);
    List<BookDTO> dtoList = bookPage
            .stream()
            .map(this::apply).collect(Collectors.toList());
    final PageImpl<BookDTO> bookDTOS = new PageImpl<>(dtoList, pageable, bookPage.getTotalElements());*/

    public Object update(D dto, Long id, Class<?> dtoClass) {
        Optional<E> optional = findById(id);
        if (optional.isPresent()) {
            E updateBook = optional.get();
            //SET the values of the entity to the values of the Entity
            //updateBook.updateFrom(entity);
             updateBook =  save(updateBook);
             return asDTOObject(updateBook, dtoClass);
        }
        return null;
    }

    public List<E> findAllSpecsAsList(FilterRequest filter) {
        Specification<E> spec =  getSpecificationFromFilters(filter);
        return repository.findAll(spec);
    }

    public Page<E> findAllSpecsAsPage(FilterRequest filter, Pageable pageable) {

        Specification<E> spec =   getSpecificationFromFilters(filter);
        return repository.findAll(spec, pageable);
    }

    public List<?> findAllSpecsAsListWithFields(FilterRequest filter, Class<E> entity) {

        if(filter.getFields().length > 0)
            return getSpecificationFromFiltersWithFields( entity,  filter);

        else{
            Specification<E> spec =  getSpecificationFromFilters(filter);
            return repository.findAll(spec);
        }
    }

    public E save(E entity) {
        return (E)this.repository.save(entity);
    }

    public E saveV2(D dto) {
        E entity = asEntity(dto, entityClass);
        return repository.save(entity);
    }
    
    public D saveDTO(D dto, Class<E> entityClass, Class<D> dtoClass) {
        // Convert DTO to entity
        E entity = modelMapper.map(dto, entityClass);

       /* // Get entity
        Author entity = authorService.findById(dto.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("Author not found"));

        // Set entity
        entity.setAuthor(entity);*/

        // Save entity
        entity = (E)repository.save(entity);

        // Convert entity to DTO
        D savedDTO = asDTO(entity, dtoClass);

        // Return response with saved entity DTO
        return savedDTO;
    }

    public List<E> save(List<E> entity) {
        return null;
    }
    
    public List<D> saveAll(List<D> dtoList, Class<E> entityClass, Class<D> dtoClass) {
        List<E> lists = new ArrayList<>();
        for (D dto : dtoList) {
            // Map the DTO to the entity*/
            E entity = asEntity(dto, entityClass);
            lists.add(entity);
        }
        return asDTOList(repository.saveAll(lists), dtoClass);
    }

    /*public List<D> saveAll(List<D> dtoList, Class<E> entityClass, Class<D> dtoClass) {
        List<E> lists = new ArrayList<>();
        for (D dto : dtoList) {
            // Set the entity
            Author entity = authorService.findById(dto.getAuthor().getId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            // Map the DTO to the entity
            E entity = asEntity(dto, entityClass);
            entity.setAuthor(entity);
            lists.add(entity);
        }
        return asDTOList(repository.saveAll(lists), dtoClass);
    }*/

    //saveAll method that can handle creating new books and linking them to existing authors:
    /*public <E, D> List<D> saveAllWithDetails(List<D> dtoList, Class<E> entityClass, Class<D> dtoClass) {
        List<E> entityList = new ArrayList<>();
        for (D dto : dtoList) {
            E entity = modelMapper.map(dto, entityClass);
            entityList.add(entity);
        }
        Iterable<E> savedEntities = repository.saveAll(entityList);
        return asDTOList(savedEntities, dtoClass);
    }*/

   /* public <E extends BaseEntity, D> List<D> saveAllWithDetails(List<D> dtoList, Class<E> entityClass, Class<D> dtoClass) {
        List<E> entityList = new ArrayList<>();
        for (D dto : dtoList) {
            E entity = modelMapper.map(dto, entityClass);
            entityList.add(entity);
        }
        Iterable<E> savedEntities = repository.saveAll(entityList);
        return asDTOList(savedEntities, dtoClass);
    }*/




    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    //NO FIND ALL FROM DB. just direct QUERY FRM DB. IF FIND ALL WILL GET ALL THE FIELDS
    protected  List<E> getSpecificationFromFiltersWithFields(Class<E> entityClass,  FilterRequest filters) {
        Specification<E> spec = Specification.where(null);
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createQuery(Tuple.class);
        Root<E> root = query.from(entityClass);

        // Process filters
        if (filters.getFilters().size() > 0) {
            for (FilterCriteria filter : filters.getFilters()) {
                String[] fieldNames = filter.getField().split("\\.");
                Path<?> path = root;
                for (String fieldName : fieldNames) {
                    path = path.get(fieldName);
                }
                Predicate predicate = getPredicate(builder, path, filter.getOperator(), filter.getValue());
                if (predicate != null) {
                    query.where(predicate);
                }
            }
        }

        // Process fields
        List<Selection<?>> selections = new ArrayList<>();
        for (String field : filters.getFields()) {
            String[] fieldNames = field.split("\\.");
            Path<?> path = root;
            for (String fieldName : fieldNames) {
                path = path.get(fieldName);
            }
            selections.add(path.alias(field.replaceAll("\\.", "_")));
        }
        query.multiselect(selections);


        Predicate predicate = spec.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
        }

        List<Tuple> results = entityManager.createQuery(query).getResultList();
        List<E> resultList = new ArrayList<>();
        for (Tuple tuple : results) {
            E obj;
            try {
                obj = entityClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating object of type " + entityClass.getSimpleName(), e);
            }
            for (int i = 0; i < filters.getFields().length; i++) {
                Object value = tuple.get(i);
                try {
                    Field field = entityClass.getDeclaredField(filters.getFields()[i]);
                    field.setAccessible(true);
                    field.set(obj, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Error setting value for field " + filters.getFields()[i] +
                            " in object of type " + entityClass.getSimpleName(), e);
                }
            }
            resultList.add(obj);
        }
        return resultList;
    }

    private Predicate getPredicate(CriteriaBuilder builder, Path<?> path, QueryOperator operator, Object value) {
        switch (operator) {
            case EQUALS:
                return builder.equal(path, value);
            case NOT_EQUALS:
                return builder.notEqual(path, value);
            case GREATER_THAN:
                return builder.greaterThan((Path<Comparable>) path, (Comparable) value);
            case GREATER_THAN_OR_EQUAL:
                return builder.greaterThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
            case LESS_THAN:
                return builder.lessThan((Path<Comparable>) path, (Comparable) value);
            case LESS_THAN_OR_EQUAL:
                return builder.lessThanOrEqualTo((Path<Comparable>) path, (Comparable) value);
            case LIKE:
                return builder.like((Path<String>) path, "%" + value + "%");
            case IN:
                return path.in((Collection<?>) value);
            case BETWEEN:
                Object[] range = (Object[]) value;
                return builder.between((Path<Comparable>) path, (Comparable) range[0], (Comparable) range[1]);
            case IS_NOT_NULL:
                return builder.isNotNull(path);
            case IS_NULL:
                return builder.isNull(path);
            default:
                return null;
        }
    }

    protected Specification<E> getSpecificationFromFilters(FilterRequest filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters.getFilters().size() > 0) {
                for (FilterCriteria filter : filters.getFilters()) {
                    String[] fieldNames = filter.getField().split("\\.");
                    Path<?> path = root;
                    for (String fieldName : fieldNames) {
                        path = path.get(fieldName);
                    }
                    Predicate predicate = getPredicate(criteriaBuilder, path, filter.getOperator(), filter.getValue());
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    protected Specification<E> getSpecificationFromFilters(FilterRequest filters, JoinType joinType) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters.getFilters().size() > 0) {
                for (FilterCriteria filter : filters.getFilters()) {
                    String[] fieldNames = filter.getField().split("\\.");
                    Path<?> path = root;

                    // Apply join type
                    if (joinType == JoinType.INNER) {
                        path = root.join(fieldNames[0], JoinType.INNER);
                    } else if (joinType == JoinType.LEFT) {
                        path = root.join(fieldNames[0], JoinType.LEFT);
                    }

                    for (int i = 1; i < fieldNames.length; i++) {
                        path = path.get(fieldNames[i]);
                    }

                    Predicate predicate = getPredicate(criteriaBuilder, path, filter.getOperator(), filter.getValue());
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    //String[] fieldNames = {"id", "title", "author.name"};
     /*[
        {
            "fields": [
            "id",
                    "title",
                    "author"
        ]
            "operator": "like",
            "value": "title"
        }
    ]*/
    protected Specification<E> getSpecificationFromFilters(FilterRequest filters, String[] fieldNames) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filters.getFilters().size() > 0) {
                for (FilterCriteria filter : filters.getFilters()) {
                    //String[] fieldNames = filter.getFields();

                    Predicate[] orPredicates = new Predicate[fieldNames.length];
                    for (int i = 0; i < fieldNames.length; i++) {
                        Path<String> path = root.get(fieldNames[i]);
                        Predicate likePredicate = criteriaBuilder.like(path, "%" + filter.getValue() + "%");
                        orPredicates[i] = likePredicate;
                    }

                    Predicate orPredicate = criteriaBuilder.or(orPredicates);
                    predicates.add(orPredicate);
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    /*@Transactional
    public void saveAll(D dto) {
        E entity = asEntity(dto, entityClass);
        *//*Set<ConstraintViolation<E>> violations = validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }*//*

        repository.save(entity);

        // Save related entities
        if (entity instanceof Author) {
            List<Book> books = ((Author) entity).getBooks();
            if (books != null) {
                for (Book book : books) {
                    book.setAuthor((Author) entity);
                    repository.save(book);
                }
            }
        } else if (entity instanceof Book) {
            Author author = ((Book) entity).getAuthor();
            if (author != null) {
                repository.save(author);
            }
        }
    }*/




}
