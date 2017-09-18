package se.inera.statistikapi.config;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.AbstractPathProvider;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2
@Configuration
@EnableAutoConfiguration
public class SwaggerConfig {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                    .apis(RequestHandlerSelectors.basePackage("se.inera.statistikapi.web.rest.v1.api"))
                    .paths(PathSelectors.any())
                    .build()
                .pathProvider(new BaseVersionProvider())
//                .enableUrlTemplating(true)
                .apiInfo(metadata());
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Statistik")
                .description("Statistik API. Tanken med API:et är att tillgängligöra statistik från NTjP.")
                .version("1.0.0")
                .build();
    }


    public class BaseVersionProvider extends AbstractPathProvider {

        public static final String ROOT = "/statistik";

        public BaseVersionProvider() {
            super();
        }

        @Override
        protected String applicationPath() {
            return ROOT;
        }

        @Override
        protected String getDocumentationPath() {
            return ROOT;
        }

        @Override
        public String getApplicationBasePath() {
            return ROOT;
        }

    }


}
