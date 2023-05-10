package com.cict.core.base;

import com.cict.core.exception.ErrorKey;
import com.cict.core.exception.RestException;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Used to resolve any primary entity type that is managed by the DB. Often this is used to create
 * entities that cannot be found using source data from the REST resource.
 */
@Service
public class BaseEntityResolver extends BaseRestController  implements EntityResolver{


    @Override
    public <T> T getEntity(Class<?> cls, String id) {
        return null;
    }

    @Override
    public <T, R> T getEntity(R resource) {
        return null;
    }
}