package com.cict.config.web;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Contains a {@link ThreadLocal} that holds the current {@link HttpServletRequest}.
 * This request can be retrieved within backend classes that need access to the request
 * but don't have the luxury of being passed the current request.
 */
@Component
public class RequestContext {

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    /**
     * Sets the current {@link HttpServletRequest} or nulls it out if the request
     * is null. The request should be nulled out at the end of the request chain.
     */
    static void set(HttpServletRequest request) {
        requestHolder.set(request);
    }

    /**
     * Returns the current request or null if we're not with a request context.
     */
    public HttpServletRequest get() {
        return requestHolder.get();
    }
}
