package com.cict.core.base;
import com.cict.core.exception.ErrorKey;
import com.cict.config.web.RequestContext;
import com.cict.core.exception.RestException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

//TODO: still a lot of cleaning up to do here

/**
 * Base class for all rest controllers.
 */
@Getter
public abstract class BaseRestController extends Loggable {
    protected static final String API_BASE = "/api";
    protected static final String ID = "/{id}";
    protected static final String STATUS = "/status";
    protected static final String UPLOAD = "/upload";
    protected static final String MONO = "/mono/{mono}";
    protected static final String SLASH = "/";

    @Value("${jwt.header}")
    protected String tokenHeader;

    @Value("${application.assets.path}")
    protected String uploadPath;

    @Value("${application.assets.absolute}")
    protected String absolutePath;

    @Value("${application.code}")
    protected String appCode;


    @Autowired
    private RequestContext requestContext;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected ObjectMapper objectMapper;




}