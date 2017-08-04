package org.justvit.docsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Application start class
 */
@SpringBootApplication
public class DocSearchApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DocSearchApplication.class, args);
    }

}
