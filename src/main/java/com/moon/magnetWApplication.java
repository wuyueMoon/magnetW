package com.moon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wuyue
 * @date 2019-08-09 16:10
 */
@SpringBootApplication()
@EnableScheduling
public class magnetWApplication {

    public static void main(String[] args) {
        SpringApplication.run(magnetWApplication.class, args);
    }
}
