package com.dguzowski.supermarket.checkout.config;

import com.dguzowski.supermarket.checkout.domain.Purchase;
import com.dguzowski.supermarket.checkout.repositories.ProductRepository;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.SQLException;

@Configuration
@EnableSwagger2
@EntityScan(basePackageClasses = {Purchase.class})
@EnableJpaRepositories(basePackageClasses = {ProductRepository.class})
public class AppConfiguration {

    private static Logger log = LoggerFactory.getLogger(AppConfiguration.class);
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dguzowski.supermarket.checkout.rest"))
                .paths(PathSelectors.ant("/api/**"))
                .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TCPServer() throws SQLException {
        Server server =  Server.createTcpServer("-tcp","-tcpAllowOthers");
        log.debug("h2 db server runs at: "+server.getURL());
        return server;
    }
}
