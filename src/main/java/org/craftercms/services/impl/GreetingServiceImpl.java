package org.craftercms.services.impl;

import org.craftercms.ext.spring.Log;
import org.craftercms.services.api.GreetingService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Implements GreetingService.
 */
@Service
public class GreetingServiceImpl implements GreetingService {
    /**
     * Logger.
     */
    @Log
    private Logger log;
    /**
     * Configurable Greet Message.
     */
    @Value("${default.greet}")
    private String greet;

    /**
     * Default empty ctor.
     */
    public GreetingServiceImpl() {
    }

    @Override
    public String greet(final String name) {
        log.debug("Greeting User {}", name);
        return greet + " " + name;
    }
}
