package com.cict.core.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
//USAGE
// Create a specification instance
EntitySpecification<Product> specification = new EntitySpecification<>();

// Add search criteria
    specification.add(new SearchCriteria("name", SearchOperation.LIKE, "iphone"));
    specification.add(new SearchCriteria("category", SearchOperation.EQUAL, "Electronics"));
    specification.add(new SearchCriteria("price", SearchOperation.LESS_THAN, 1000.0));

// Use the specification in your repository or service
    List<Product> products = productService.getProductsBySpecification(specification);
//  List<Product> products = repository.findAll(specification);

public List<Product> getProductsBySpecification(Specification<Product> specification) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Product> query = builder.createQuery(Product.class);
    Root<Product> root = query.from(Product.class);

    Predicate predicate = specification.toPredicate(root, query, builder);
    query.where(predicate);

    TypedQuery<Product> typedQuery = entityManager.createQuery(query);
    return typedQuery.getResultList();
}
* */
@Slf4j
public class EntitySpecification<T> implements Specification<T> {

    private final List<SearchCriteria> criteriaList;
    private final Boolean isPairedSwipes;

    public EntitySpecification(Boolean isPairedSwipes) {
        this.isPairedSwipes = isPairedSwipes;
        this.criteriaList = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        criteriaList.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : criteriaList) {
            String[] keyParts = criteria.getKey().split("\\.");

            Path<?> path = root;
            Join<?, ?> join = null;

            for (String part : keyParts) {
                if (path != null) {
                    if (path.get(part).getJavaType().isAnnotationPresent(javax.persistence.Entity.class)) {
                        join = root.join(part, JoinType.LEFT);
                        path = join;
                    } else if (Collection.class.isAssignableFrom(path.get(part).getJavaType())) {
                        // Handle collection attributes (OneToMany, ManyToMany)
                        join = ((From<?, ?>) path).join(part, JoinType.LEFT);
                        path = join;
                        log.info(String.valueOf(path));
                    } else {
                        path = path.get(part);
                    }
                }
            }

            predicates.add(buildPredicate(path, builder, criteria));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildPredicate(Path<?> path, CriteriaBuilder builder, SearchCriteria criteria) {
        Predicate predicate;

        if (criteria.getOperation() == SearchOperation.LESS_THAN) {
            predicate = buildLessThanPredicate(path, builder, criteria);
        } else if (criteria.getOperation() == SearchOperation.GREATER_THAN) {
            predicate = buildGreaterThanPredicate(path, builder, criteria);
        } else if (criteria.getOperation() == SearchOperation.IN) {
            predicate = buildInPredicate(path, criteria);
        } else if (criteria.getOperation() == SearchOperation.LIKE) {
            predicate = buildLikePredicate(path, builder, criteria);
        } else {
            throw new IllegalArgumentException("Invalid search operation: " + criteria.getOperation());
        }

        return predicate;
    }

    private Predicate buildLessThanPredicate(Path<?> path, CriteriaBuilder builder, SearchCriteria criteria) {
        if (AppUtils.isDate(criteria.getValue().toString())) {
            if (AppUtils.isLocalDateTime(criteria.getValue().toString())) {
                return builder.lessThan((Expression<? extends Comparable>) path, (Comparable) AppUtils.isLocalDateTime(criteria.getValue().toString()));
            } else {
                return builder.lessThan((Expression<? extends Comparable>) path, (Comparable) AppUtils.parseLocalDate(criteria.getValue().toString()));
            }
        } else {
            return builder.lessThan((Expression<? extends Comparable>) path, (Comparable) criteria.getValue());
        }
    }

    private Predicate buildGreaterThanPredicate(Path<?> path, CriteriaBuilder builder, SearchCriteria criteria) {
        if (AppUtils.isDate(criteria.getValue().toString())) {
            if (AppUtils.isLocalDateTime(criteria.getValue().toString())) {
                return builder.greaterThan((Expression<? extends Comparable>) path, (Comparable) AppUtils.isLocalDateTime(criteria.getValue().toString()));
            } else {
                return builder.greaterThan((Expression<? extends Comparable>) path, (Comparable) AppUtils.parseLocalDate(criteria.getValue().toString()));
            }
        } else {
            return builder.greaterThan((Expression<? extends Comparable>) path, (Comparable) criteria.getValue());
        }
    }

    private Predicate buildInPredicate(Path<?> path, SearchCriteria criteria) {
        if (Enum.class.isAssignableFrom(path.getJavaType())) {
            List<Enum<?>> enumList = new ArrayList<>();
            for (String field : criteria.getValue().toString().split(",")) {
                Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>) path.getJavaType(), field);
                enumList.add(enumValue);
            }
            return path.in(enumList);
        } else {
            return path.in((Object[]) criteria.getValue());
        }
    }

    private Predicate buildLikePredicate(Path<?> path, CriteriaBuilder builder, SearchCriteria criteria) {
        if (AppUtils.isDate(criteria.getValue().toString())) {
            if (AppUtils.isLocalDateTime(criteria.getValue().toString())) {
                return builder.equal(path, AppUtils.isLocalDateTime(criteria.getValue().toString()));
            } else {
                return builder.equal(path, AppUtils.parseLocalDate(criteria.getValue().toString()));
            }
        } else if (Long.class.isAssignableFrom(path.getJavaType()) || Integer.class.isAssignableFrom(path.getJavaType())
                || Double.class.isAssignableFrom(path.getJavaType()) || Float.class.isAssignableFrom(path.getJavaType())) {
            return builder.equal(path, criteria.getValue());
        } else if (Enum.class.isAssignableFrom(path.getJavaType())) {
            Enum<?> enumValue = Enum.valueOf((Class<? extends Enum>) path.getJavaType(), criteria.getValue().toString());
            return builder.equal(path, enumValue);
        } else if (Boolean.class.isAssignableFrom(path.getJavaType())) {
            return builder.equal(path, Boolean.parseBoolean(criteria.getValue().toString()));
        } else if (criteria.getValue().toString().equalsIgnoreCase("null")) {
            return builder.isNull(path);
        } else {
            return builder.like((Expression<String>) path, "%" + criteria.getValue() + "%");
        }
    }
}
