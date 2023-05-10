package com.cict.core.base;

import com.cict.core.exception.ErrorKey;
import com.cict.core.exception.RestException;
import com.cict.core.request.CodeDescription;

import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.cict.core.base.QueryOperator.GREATER_THAN_FIELD;
import static com.cict.core.base.QueryOperator.LESS_THAN_FIELD;
import static com.cict.core.util.IdConverter.toId;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.Specification.where;

@Log4j2
@Getter
//@Setter
@SuppressWarnings("unchecked")
/**
 *  T = Entity Model/Type
 *  D = Primary DTO Object
 *  R = Repository
 */
//An abstract class cannot be instantiated directly because it is intended to be subclassed by other classes
public abstract class BaseServiceObject<T extends BaseModel, D extends BaseDTO, R extends BaseRepository> extends BaseRestController  {

    @Autowired
    protected EntityResolver resolver;

    @Value("${application.dev.show.model.id:false}")
    boolean showDocumentId;

    R repository;
    Class<?> primaryModelClass;
    Class<?> primaryRepoClass;
    Class<?> primaryDTOClass;
    Class<?> primaryDTOMin;
    Class<?> primaryDTOName;

    /*@Autowired
    AuditEventPublisher auditEventPublisher;*/



    /**
     * Use to initialise the object requirement to automate the base CRUD the default CRUD** to run.

     * @param repository primary Repository
     * @param primaryModel entityModel.class -> primary entity class being handled by this Service
     * @param primaryRepo primaryRepository.class -> primary class of the repository for this service
     * @param primaryDTO primaryDTO.class -> full object resource class
     * @param primaryDTOMin primaryDTOMin.class -> Minimum info object resource class
     * @param primaryDTOName primaryDTOName.class -> simple info
     */
    protected BaseServiceObject( R repository,
                                 Class<?> primaryModel,
                                 Class<?> primaryRepo,
                                 Class<?> primaryDTO,
                                 Class<?> primaryDTOMin,
                                 Class<?> primaryDTOName
    ) {
        this.repository = repository;
        this.primaryModelClass = primaryModel;
        this.primaryRepoClass = primaryRepo;
        this.primaryDTOClass = primaryDTO;
        this.primaryDTOMin = primaryDTOMin;
        this.primaryDTOName = primaryDTOName;

    }


    /**
     * Can be overriden - pre process the resource and manipulate it before creation of entity is called
     * Can be use to additionally validate/manipulate the resource before processing
     *
     * @param res
     * @return the manually updated resource if ever
     */
    protected D iniResource( D res ) { return res; }

    /**
     * by default validate the CRUD automation with existinngg code field
     * overide to use your custom code validation
     * @param sbuId
     * @param res
     * @param isCreate
     * @param id
     * @return
     */
    protected boolean isValid(long sbuId, D res, boolean isCreate, Long id) {
        String code = (String)res.getCode();
        long ctr = repository.validateCountBySbuAndCode(sbuId, code);
        if(ctr > 1) {
            throw new RestException(ErrorKey.DOCUMENT_MULTI_EXISTS);
        }

        Optional<T> objList  =  repository.validateFindBySbuAndCode(sbuId, code);
        if (isCreate && objList.isPresent()) {
            throw new RestException(ErrorKey.DOCUMENT_CODE_EXISTS);
        } else if (!isCreate && objList.isPresent()) {
            T o = objList.get();
            if( o.getId() != id )
                throw new RestException(ErrorKey.DOCUMENT_CODE_IN_USED);
        }
        return true;
    }

    /**
     * optionally can be overriden - can be use to recheck the newly created entity before saving it
     *
     * @param entity entity object to be manully check/updated
     * @return return the manually updated entity if ever.
     */
    private T beforeSave( T entity ) { return entity; }

    /**
     * optionally can be overriden - can be use to recheck the newly created entity before saving it
     *
     * @param entity entity object to be manully check/updated
     * @return return the manually updated entity if ever.
     */
    public T beforeSave( T entity, D dto, boolean isCreate  ) { return beforeSave(entity); }


    /**
     * this will get the dtoType you need base on the current type of object
     *
     * @param dtoType [full|min|name]
     * @return class type of the selected type
     */
    protected Class<?> getDTOClassType(String dtoType) {
        dtoType = dtoType.toLowerCase();
        if(dtoType.equals("full"))
            return getPrimaryDTOClass();
        else if(dtoType.equals("min"))
            return getPrimaryDTOMin();
        else
            return getPrimaryDTOName();
    }


    /**
     * transform entity to the selected Class of DTO type object
     *
     * @param entity the entity to transform
     * @param dtoClass the selected class to transform this entity to.
     * @return object equevalent of the entity base on the selected class
     */
    public Object entityToDTO(T entity, Class<?> dtoClass) {
        BaseMergable ret = modelMapper.map(entity, (Type)dtoClass ) ;
        ret = (BaseMergable)ret.from(entity, dtoClass, resolver, modelMapper);

        if(showDocumentId) {
            ret.setDocumentId(entity.getId());
        }
        return ret;
    }

    /**
     * Get One Eniity base on Primary DTO base on ID
     *
     * @param id
     * @return
     */
    public D getOneDTO(long id) {
        return (D)entityToDTO( getOne(id), getPrimaryDTOClass() );
    }

    /**
     * Get One Entity base on ID
     * @param id
     * @return
     */
    public T getOne(long id) {
        Optional<T> ret = (Optional<T>)(getRepository()).findById(id);
        if(ret.isEmpty()) {
            String className = getPrimaryModelClass().getSimpleName();
            throw new RuntimeException( className + " : Record Not found / Invalid data access");
        }
        return ret.get();
    }

    /**
     * Create a new entity base on the resource feed to it
     * @param sbuId
     * @param resource
     * @return
     */
//    @Transactional(rollbackFor = Exception.class)
    public T createOne(long sbuId, D resource) {

        if ( resource.getCode() != null) { //Validate only code in resource.getCode is exists. Eduard. Apr 25,2022
            if (!isValid(sbuId, resource, true, null))
                return null;
        }

        resource.setSbuId(String.valueOf(sbuId));

        resource = iniResource(resource);       // call iniResource -> can be use to validate/manipulate the payload

        resource.setId("");                     // Create entity should not match any ID if any
        resource.setActive(Boolean.TRUE);       // by default set entity to active
//        resource.setCreatedBy(getUsername());   // get the current user
        // resource.setTimeCreated(---);        // TODO: get the current time of Compny timezone

        // Use model mapper to initially Map the data to entity calss
        T entity = (T)modelMapper.map(resource, getPrimaryModelClass());

        // call external -> entity mapper to further update  the entity data manually
        entity = (T) ((D)resource).to(entity, resolver, modelMapper);

        entity.setSbuId( sbuId );

        // Call the optional before save of entity in Service implements if exists
        entity = beforeSave(entity, resource, true);
        return  (T)(getRepository()).save( entity );
    }

    public T createOne(long sbuId, D resource, long parentId) {

        if ( resource.getCode() != null) { //Validate only code in resource.getCode is exists. Eduard. Apr 25,2022
            if (!isValid(sbuId, resource, true, null))
                return null;
        }

        resource.setSbuId(String.valueOf(sbuId));

        resource = iniResource(resource);       // call iniResource -> can be use to validate/manipulate the payload

        resource.setId("");                     // Create entity should not match any ID if any
        resource.setActive(Boolean.TRUE);       // by default set entity to active
//        resource.setCreatedBy(getUsername());   // get the current user
        // resource.setTimeCreated(---);        // TODO: get the current time of Compny timezone

        // Use model mapper to initially Map the data to entity calss
        T entity = (T)modelMapper.map(resource, getPrimaryModelClass());

        // call external -> entity mapper to further update  the entity data manually
        entity = (T) ((D)resource).to(entity, resolver, modelMapper);

        entity.setSbuId( sbuId );

        // Call the optional before save of entity in Service implements if exists
        entity = beforeSave(entity, resource, true);
        return  (T)(getRepository()).save( entity );
    }

    /**
     * Update the exisitng entity base on ID -> resource payload
     * @param sbuId
     * @param id
     * @param resource
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public T updateOne(long sbuId, String id, D resource) {
        //TODO: validate ID match
        if ( resource.getCode() != null) { //Validate only code in resource.getCode is exists. Eduard. Apr 25,2022
            if(!isValid(sbuId,  resource, false, toId(id)))
                return null;
        }

        if( !resource.getId().equals(id) ) {
            throw new RestException( ErrorKey.RECORD_PAYLOAD_NOT_MATCH);
        }
        long longId = toId(id);

        //will check if item exists
        T entity = this.getOne(longId);

        resource.setSbuId(String.valueOf(sbuId));
        resource = iniResource(resource);       // call iniResource -> can be use to validate/manipulate the payload

        resource.setId( Long.toString(longId) );
        resource.setCreatedBy(null);
        resource.setTimeCreated(null);
//        resource.setUpdatedBy(getUsername());   // get the current user

        // resource.setTimeCreated(---);        // TODO: get the current time of Compny timezone

        entity = (T) ((D)resource).to(entity, resolver, modelMapper);

        entity.setSbuId( sbuId );

        // Call the optional before save of entity in Service implements if exists
        entity = beforeSave(entity, resource, false);

        entity = (T) (getRepository()).save( entity );

        return  entity;
    }

    /**
     * default findall by name search query use for get all default CRUD implementation
     * @param sbuId
     * @param search
     * @param isActive
     * @param page
     * @param dtoType
     * @return
     */
    public SearchDTO findAllByName(long sbuId, String search, boolean isActive, Pageable page, String dtoType) {
        Class<?> finalDtoClass = getDTOClassType(dtoType);
        return findAllByName(sbuId, search, isActive, page, finalDtoClass);
    }

    /**
     * default findall by name search query use for get all default CRUD implementation
     * @param sbuId
     * @param search        search term by default search the name field
     * @param isActive      show only actiive or deleted data
     * @param page
     * @param cls           actual Class to be use for dTO mapping (xxxDTO, xxxDTOMin, xxxDTOName)
     * @return
     */
    public SearchDTO findAllByName(long sbuId, String search, boolean isActive, Pageable page, Class<?> cls) {
        //adjusted this to display the pages.getTotalElements(), pages.getTotalPages(), pages.getSize(), pages.getNumber(), pages.isLast()
        Page<T> pages = (Page<T>) ( (R) getRepository()).findAllData( sbuId, "%" + search.trim().toLowerCase() + "%", isActive, page);
        return SearchDTO.of(page.getPageNumber(), page.getPageSize(), pages.getContent()
                        .stream().map(a -> modelMapper.map(a, cls)).collect(toList()), pages.getTotalElements(),
                pages.getTotalPages(), pages.getSize(), pages.getNumber(), pages.isLast() );
        //old return
        //return SearchDTO.of(page.getPageNumber(), page.getPageSize(), pages.getContent().stream().map(a -> entityToDTO(a, cls)).collect(toList()) );
    }

    /**
     * default getOne item and convert it to DTO Object base on param
     *
     * @param id long id of the record
     * @param dtoType String [full|min|name] -> DTO Type to return
     * @return DTO base on dtoType Param
     */
    public Object getOneDTO(String id, String dtoType) {
        long longId = toId(id);
        Class<?> finalDtoClass = getDTOClassType(dtoType);
        return  entityToDTO( getOne(longId), finalDtoClass );
    }

    /**
     * default getOne item and convert it to DTO Object base on param (actual class)
     *
     * @param id
     * @param cls
     * @return
     */
    public Object getOneDTO(String id, Class<?> cls) {
        long longId = toId(id);
        return  entityToDTO( getOne(longId), cls );
    }

    /**
     * Create One and Return as Primary DTO
     *
     * @param resource Primary DTO Object Struture -> entity to be created
     * @return return as newly created item as a primary DTO
     */
    public D createOneDTO(long sbuId, D resource) {
        long startTime = System.currentTimeMillis();
        final D data;
        try {
            T entity = createOne(sbuId, resource);
            data = (D) entityToDTO(entity, getPrimaryDTOClass());
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(resource);
//            auditEventPublisher.publishEvent(getUsername(), requestBody, System.currentTimeMillis() - startTime, null);
        }catch (Exception ex){
//            auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".").concat( resource.toString()), System.currentTimeMillis() - startTime, null);
            throw new IllegalStateException(ex.getMessage());
        }

        return data;
    }


    /**
     * Update entity base on ID and return a mapped to Priimariy DTO
     * @param sbuId
     * @param id
     * @param resource
     * @return
     */
    public D updateOneDTO(long sbuId, String id, D resource) {
        long startTime = System.currentTimeMillis();
        try {
            T entity = updateOne(sbuId, id, resource);
            final D map = (D) modelMapper.map(entity, getPrimaryDTOClass());
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(resource);
//            auditEventPublisher.publishEvent(getUsername(), requestBody, System.currentTimeMillis() - startTime, null);
            return map;
        }catch (Exception ex){
//            auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".")
//                    .concat( resource.toString()), System.currentTimeMillis() - startTime, null);
            throw new IllegalStateException(ex.getMessage());
        }
    }

    /**
     * Tag enity as deleted
     *
     * @param id
     *
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOne(String id) {
        long startTime = System.currentTimeMillis();
        long longId = toId(id);
        final T one = this.getOne(longId);

        CodeDescription codeDescription = new CodeDescription();
        codeDescription.setId(one.getId());
        codeDescription.setCode(one.getCode());
        codeDescription.setDescription(one.getDescription());
        try {
//            repository.setInActive(longId, getUsername(), LocalDateTime.now());
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(codeDescription);

//            auditEventPublisher.publishEvent(getUsername(),  requestBody, System.currentTimeMillis() - startTime, null);
        }catch (Exception ex){
            /*auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".")
                    .concat( codeDescription.toString()), System.currentTimeMillis() - startTime, null);*/
            throw new RestException( ErrorKey.DELETE_RESTRICT);
        }

//        //TODO: check cascade delete if have child
//        //TODO: also update the user and time
    }


    @Transactional(rollbackFor = Exception.class)
    public void activateOne(String id) {
        long startTime = System.currentTimeMillis();
        long longId = toId(id);
        final T one = this.getOne(longId);

        CodeDescription codeDescription = new CodeDescription();
        codeDescription.setId(one.getId());
        codeDescription.setCode(one.getCode());
        codeDescription.setDescription("activate");
        try {
//            repository.setActive(longId, getUsername(), LocalDateTime.now());
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(codeDescription);
//            auditEventPublisher.publishEvent(getUsername(),  requestBody, System.currentTimeMillis() - startTime, null);
        }catch (Exception ex){
            /*auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".")
                    .concat( codeDescription.toString()), System.currentTimeMillis() - startTime, null);*/
            throw new RestException( ErrorKey.DELETE_RESTRICT);
        }

//        //TODO: check cascade delete if have child
//        //TODO: also update the user and time
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOneFromDb(String id) {
        long startTime = System.currentTimeMillis();
        long longId = toId(id);
        final T one = this.getOne(longId);

        CodeDescription codeDescription = new CodeDescription();
        codeDescription.setId(one.getId());
        codeDescription.setCode(one.getCode());
        codeDescription.setDescription(one.getDescription());
        try {
            repository.delete(one);
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(codeDescription);
//            auditEventPublisher.publishEvent(getUsername(), requestBody, System.currentTimeMillis() - startTime, null);
        } catch (Exception ex) {
            /*auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".")
                    .concat(codeDescription.toString()), System.currentTimeMillis() - startTime, null);*/
            throw new RestException(ErrorKey.DELETE_RESTRICT);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOnePermanent(String id) {
        long longId = toId(id);
        final T one = this.getOne(longId);
        long startTime = System.currentTimeMillis();

        CodeDescription codeDescription = new CodeDescription();
        codeDescription.setId(one.getId());
        codeDescription.setCode(one.getCode());
        codeDescription.setDescription(one.getDescription());


        try {
            repository.deletePermanent(longId);
            objectMapper.disable(SerializationFeature.INDENT_OUTPUT);
            String requestBody = objectMapper.writeValueAsString(codeDescription);
//            auditEventPublisher.publishEvent(getUsername(),  requestBody, System.currentTimeMillis() - startTime, null);
        }catch (Exception ex){
            /*auditEventPublisher.publishEventError(getUsername(), "error:".concat(ex.getMessage() + ".")
                    .concat( codeDescription.toString()), System.currentTimeMillis() - startTime, null);*/
            throw new RestException( ErrorKey.DELETE_RESTRICT);
        }
    }

    protected Specification<T> getSpecificationFromFilters(long sbuId, boolean isActive, List<Filter> filter) {
        List<Filter> filtersList = new ArrayList<>();
        for (Filter fil : filter) { filtersList.add(fil);}

//        filtersList.add(new Filter("sbuId", QueryOperator.EQUALS,  String.valueOf(sbuId)  ));
//        filtersList.add(new Filter("active", QueryOperator.EQUALS, String.valueOf(isActive) ));

        Specification<T> specification = null;
        int count = 1;
        for (Filter input : filtersList) {
            if (count == 1) {
                specification = where(getSpecificationByField(input));
            } else {
                specification = specification.and(getSpecificationByField(input));
            }
            count += 1;
        }
        return specification;
    }

    public List<?> getQueryResultByFilter(long sbuId, boolean isActive, List<Filter> filters, String dtoType){
        Class<?> finalDtoClass = getDTOClassType(dtoType);
        List<T> retVal = repository.findAll(getSpecificationFromFilters(sbuId, isActive, filters));
        return retVal.stream().map(a -> entityToDTO(a, finalDtoClass)).collect(toList());
    }

    public SearchDTO getQueryResultByFilterWithPage(long sbuId, boolean isActive, List<Filter> filters, String dtoType, Pageable p){
        Class<?> finalDtoClass = getDTOClassType(dtoType);
        Page<T> retVal = repository.findAll(getSpecificationFromFilters(sbuId, isActive, filters), p);
        return SearchDTO.of(p.getPageNumber(), p.getPageSize(), retVal.stream().map(a -> entityToDTO(a, finalDtoClass)).collect(toList()), retVal.getTotalElements(),
                retVal.getTotalPages(), retVal.getSize(), retVal.getNumber(), retVal.isLast());
    }

    public SearchDTO getQueryResultByFilterAndPage(long sbuId, boolean isActive, List<Filter> filters, Pageable page, String dtoType) {
        Class<?> finalDtoClass = getDTOClassType(dtoType);
        return getQueryResultByFilterAndPage(sbuId, isActive, filters, page, finalDtoClass);
    }

    public SearchDTO getQueryResultByFilterAndPage(long sbuId, boolean isActive, List<Filter> filters, Pageable page, Class<?> cls) {
        if (filters.size() > 0) {
            Page<T> pages = (Page<T>) ((R) getRepository()).findAll(getSpecificationFromFilters(sbuId, isActive, filters), page);

            return SearchDTO.of(page.getPageNumber(), page.getPageSize(), pages.getContent()
                            .stream().map(a -> modelMapper.map(a, cls)).collect(toList()), pages.getTotalElements(),
                    pages.getTotalPages(), pages.getSize(), pages.getNumber(), pages.isLast());

        } else {
            Page<T> pages = (Page<T>) ((R) getRepository()).findAll(page);
            return SearchDTO.of(page.getPageNumber(), page.getPageSize(), pages.getContent()
                            .stream().map(a -> modelMapper.map(a, cls)).collect(toList()), pages.getTotalElements(),
                    pages.getTotalPages(), pages.getSize(), pages.getNumber(), pages.isLast());
        }
    }

    protected Specification<T> createSpecification(Filter input) {
        switch (input.getOperator()) {
            case EQUALS:
                return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(input.getField()),
                        castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case NOT_EQUALS:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.notEqual(root.get(input.getField()),
                                castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case GREATER_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.gt(root.get(input.getField()),
                                (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LESS_THAN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.lt(root.get(input.getField()),
                                (Number) castToRequiredType(root.get(input.getField()).getJavaType(), input.getValue()));
            case LIKE:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get(input.getField()), "%" + input.getValue() + "%");
            case IN:
                return (root, query, criteriaBuilder) ->
                        criteriaBuilder.in(root.get(input.getField()))
                                .value(castToRequiredType(root.get(input.getField()).getJavaType(), input.getValues()));
            default:
                throw new RuntimeException("Operation not supported yet");
        }
    }

    private Object castToRequiredType(Class fieldType, String value) {
        if (fieldType.isAssignableFrom(LocalDate.class)){
            return LocalDate.parse(value);
        } else if (fieldType.isAssignableFrom(LocalDateTime.class)){
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf(fieldType, value);
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else {
            return String.valueOf(value);
        }
//        return null;
    }

    private Object castToRequiredTypeV2(Class fieldType, String value, boolean flag) {
        if (flag) {
            return String.valueOf(value);
        }
        if (fieldType.isAssignableFrom(LocalDate.class)){
            return LocalDate.parse(value);
        } else if (fieldType.isAssignableFrom(LocalDateTime.class)){
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else if (fieldType.isAssignableFrom(Double.class)) {
            return Double.valueOf(value);
        } else if (fieldType.isAssignableFrom(Integer.class)) {
            return Integer.valueOf(value);
        } else if (Enum.class.isAssignableFrom(fieldType)) {
            return Enum.valueOf(fieldType, value);
        } else if (fieldType.isAssignableFrom(Boolean.class)) {
            return Boolean.valueOf(value);
        } else {
            return String.valueOf(value);
        }
//        return null;
    }

    private Object castToRequiredType(Class fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }


    private Specification<T> getSpecificationByJoinTables(String huid) {
        return (root, query, criteriaBuilder) -> {
            Join<?, ?> join = null;
            join = (Join<?, ?>) root.fetch("deliveryInformations", JoinType.INNER);
            join = (Join<?, ?>) join.fetch("materialVerification", JoinType.LEFT);

            Expression<String> expression;
            if (join != null) {
                expression = join.get(huid);
            } else {
                expression = root.get(huid);
            }
            return criteriaBuilder.equal(expression, huid);
        };
    }

    private Specification<T> getSpecificationByField(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            Join<?, ?> join = null;
            String fieldName = filter.getField();
            if (fieldName.contains(".")) {
                String[] combineFields = fieldName.split("\\.");
                fieldName = combineFields[combineFields.length - 1];
                List<String> listCombinedFields = new ArrayList<>(List.of(combineFields));
                //remove the last word because it's a value not table to join
                listCombinedFields.remove(fieldName);
                int count = 0;
                for (String table : listCombinedFields) {
                    if (count == 0) {
                        join = (Join<?, ?>) root.fetch(table, JoinType.INNER);
                    } else if (join != null){
                        join = (Join<?, ?>) join.fetch(table, JoinType.INNER);
                    }
                    count++;
                }
            }

            Expression<Object> expression;

            if (join != null) {
                expression = join.get(fieldName);
            } else {
                expression = root.get(fieldName);
            }

            Object valueCompare = new Object();
            if(filter.getValue() != null
                    && !filter.getOperator().equals(GREATER_THAN_FIELD)
                    && !filter.getOperator().equals(LESS_THAN_FIELD)
            ){
                valueCompare = castToRequiredType(expression.getJavaType(), filter.getValue());
            } else if (
                    filter.getOperator().equals(GREATER_THAN_FIELD)
                            || filter.getOperator().equals(LESS_THAN_FIELD)
            ) {
                valueCompare = castToRequiredTypeV2(expression.getJavaType(), filter.getValue(), true);
            }

            switch (filter.getOperator()) {
                case IS_NULL:
                    return criteriaBuilder.isNull(expression);
                case IS_NOT_NULL:
                    return criteriaBuilder.isNotNull(expression);
                case EQUALS:
                    if (valueCompare.getClass().isAssignableFrom(LocalDateTime.class)) {
                        return criteriaBuilder.equal(expression.as(LocalDateTime.class), (LocalDateTime) valueCompare);
                    } else if (valueCompare.getClass().isAssignableFrom(LocalDate.class)) {
                        return criteriaBuilder.equal(expression.as(LocalDate.class), (LocalDate) valueCompare);
                    }
                    return criteriaBuilder.equal(expression, valueCompare);
                case NOT_EQUALS:
                    return criteriaBuilder.notEqual(expression, valueCompare);
                case GREATER_THAN:
                    if (valueCompare.getClass().isAssignableFrom(LocalDateTime.class)) {
                        return criteriaBuilder.greaterThan(expression.as(LocalDateTime.class), (LocalDateTime) valueCompare);
                    }else if (valueCompare.getClass().isAssignableFrom(LocalDate.class)) {
                        return criteriaBuilder.greaterThan(expression.as(LocalDate.class), (LocalDate) valueCompare);
                    }
                    else if (valueCompare.getClass().isAssignableFrom(Integer.class)) {
                        return criteriaBuilder.greaterThan(expression.as(Integer.class), (Integer) valueCompare);
                    }
                    return criteriaBuilder.greaterThan(expression.as(Double.class), (Double) valueCompare);
                case GREATER_THAN_FIELD: {
                    Path<Integer> exp1 = root.get(filter.getValue());
                    Expression<Integer> exp2 = root.get(fieldName);
                    return criteriaBuilder.greaterThan(exp2, exp1);
                }
                case LESS_THAN_FIELD: {
                    Path<Integer> exp1 = root.get(filter.getValue());
                    Expression<Integer> exp2 = root.get(fieldName);
                    return criteriaBuilder.lessThan(exp2, exp1);
                }
                case GREATER_THAN_OR_EQUAL_TO:
                    if (valueCompare.getClass().isAssignableFrom(LocalDateTime.class)) {
                        return criteriaBuilder.greaterThanOrEqualTo(expression.as(LocalDateTime.class), (LocalDateTime) valueCompare);
                    }else if (valueCompare.getClass().isAssignableFrom(LocalDate.class)) {
                        return criteriaBuilder.greaterThanOrEqualTo(expression.as(LocalDate.class), (LocalDate) valueCompare);
                    }else if (valueCompare.getClass().isAssignableFrom(Integer.class)) {
                        return criteriaBuilder.greaterThan(expression.as(Integer.class), (Integer) valueCompare);
                    }
                    return criteriaBuilder.greaterThan(expression.as(Double.class), (Double) valueCompare);
                case LESS_THAN_OR_EQUAL_TO:
                    if (valueCompare.getClass().isAssignableFrom(LocalDateTime.class)) {
                        return criteriaBuilder.lessThanOrEqualTo(expression.as(LocalDateTime.class), (LocalDateTime) valueCompare);
                    } else if (valueCompare.getClass().isAssignableFrom(LocalDate.class)) {
                        return criteriaBuilder.lessThanOrEqualTo(expression.as(LocalDate.class), (LocalDate) valueCompare);
                    }
                    return criteriaBuilder.lessThan(expression.as(Double.class), (Double) valueCompare);
                case BETWEEN:
                    return criteriaBuilder.between(expression.as(LocalDate.class), filter.getDateFrom(), filter.getDateTo());
                case LIKE:
                    return criteriaBuilder.like(expression.as(String.class), "%" + filter.getValue() + "%");
                case IN:
                    if(filter.getValuesInt() != null){
                        CriteriaBuilder.In<Integer> inInteger = criteriaBuilder.in(expression.as(Integer.class));
                        for (Integer s : filter.getValuesInt()) {
                            inInteger.value(s);
                        }
                        return inInteger;
                    }else{
                        CriteriaBuilder.In<String> in = criteriaBuilder.in(expression.as(String.class));
                        for (String s : filter.getValues()) {
                            in.value(s);
                        }
                        return in;
                    }
                default:
                    throw new RuntimeException("Operation not supported yet");
            }
        };
    }
}
