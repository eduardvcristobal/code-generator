package com.cict.core.base;

/**
 * Used to resolve DB-managed entities during {@link Mergeable} merging.
 */
public interface EntityResolver {

    /**
     * Resolves the DB-managed entity using the class and its string id.
     */
    <T> T getEntity(Class<?> cls, String id);

    /**
     * Resolves the DB-managed entity using the input resource.
     */
    <T, R> T getEntity(R resource);
}