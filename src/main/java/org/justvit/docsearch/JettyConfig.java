package org.justvit.docsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Embedded Jetty configuration
 */
@Repository
public class JettyConfig {

    @Bean
    public JettyEmbeddedServletContainerFactory jettyFactory() {
        return new JettyEmbeddedServletContainerFactory();
    }

    @Bean
    public ObjectMapper jsonObjMapper() {
        return new ObjectMapper();
    }

    /**
     * Allowed CORS-requests from everywhere - for dev phase only
     * @return
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

}
