package com.jayway.rps.app;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.jayway")
public class RpsMain {

    public static void main(String[] args) {
        SpringApplication.run(RpsMain.class, args);
    }


}
