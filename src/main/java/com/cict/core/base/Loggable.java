package com.cict.core.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class of classes capable of logging.
 */
public class Loggable {
    protected Logger log = LoggerFactory.getLogger(getClass());
}