package com.jayway.rps.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.hateoas.config.EnableEntityLinks;

@ComponentScan(basePackages = "com.jayway")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.jayway")
@EnableEntityLinks
public class RpsMain {

    public static void main(String[] args) {
        SpringApplication.run(RpsMain.class, args);
    }


}
