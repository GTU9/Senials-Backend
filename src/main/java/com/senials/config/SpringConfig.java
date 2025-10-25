package com.senials.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {"com.senials.*.entity"})
@EnableJpaRepositories(basePackages = {"com.senials.*.repository"})
@ComponentScan(basePackages = {
        "com.senials.*.controller"
        , "com.senials.*.service"
        , "com.senials.*.repository"
        , "com.senials.common.**"
        , "com.senials.security.*"
})
public class SpringConfig {

}
