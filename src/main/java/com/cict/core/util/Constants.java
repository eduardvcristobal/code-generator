package com.cict.core.util;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Log4j2 // add this import statement
@Configuration
public class Constants implements EnvironmentAware {

    static Environment environment;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        log.info("Setting env");
        Constants.environment = environment;
    }

    public static String getProperty(String key) {
        return environment.getProperty(key);
    }
}

