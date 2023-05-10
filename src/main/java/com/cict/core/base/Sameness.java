package com.cict.core.base;

/**
 * Defines a measure of sameness between two objects.
 */
public enum Sameness {

    /**
     * The two objects are completely different and cannot be merged.
     */
    DIFFERENT,

    /**
     * The two objects are different but the "other" object can be merged to
     * using the {@link Mergeable#to(Object, EntityResolver)}.
     */
    MERGEABLE,

    /**
     * The two objects are equal and no merging should occur. The destination object
     * should not be touched.
     */
    EQUAL
}