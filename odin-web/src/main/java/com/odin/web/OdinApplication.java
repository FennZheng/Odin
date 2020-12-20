package com.odin.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;


@ComponentScan("com.odin")
@SpringBootApplication
@ServletComponentScan
@EnableCaching
@EnableAsync
public class OdinApplication {

    public static void main(String[] args) {
        SpringApplication.run(OdinApplication.class, args);
    }
}