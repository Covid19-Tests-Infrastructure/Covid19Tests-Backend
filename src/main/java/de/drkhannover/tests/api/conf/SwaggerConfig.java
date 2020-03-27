package de.drkhannover.tests.api.conf;
import org.springframework.beans.factory.annotation.Value;
//
//import java.awt.print.Pageable;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
//
//import com.google.common.collect.Lists;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.ApiKey;
//import springfox.documentation.service.AuthorizationScope;
//import springfox.documentation.service.Contact;
//import springfox.documentation.service.SecurityReference;
//import springfox.documentation.service.VendorExtension;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spi.service.contexts.SecurityContext;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
@Configuration
//@EnableSwagger2
public class SwaggerConfig {
	@Bean
	 public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
	   return new OpenAPI()
	          .components(new Components()
	          .addSecuritySchemes("bearer-key", 
	          new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
	          .info(new Info().title("Covid19 Tests API").version(appVersion));
			   //.license(new License().name("Apache 2.0").url("http://springdoc.org")));;
	}
//
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    private final Logger log = LoggerFactory.getLogger(SwaggerConfig.class);
//
//    @Bean
//    public Docket api() {
//        var watch = new StopWatch();
//        watch.start();
//        List<VendorExtension> vext = new ArrayList<>();
//        log.debug("Starting Swagger");
//        Contact contact = new Contact(
//            "Software System Engeneering - Universität Hildesheim",
//            "https://sse.uni-hildesheim.de",
//            "");
//        ApiInfo apiInfo = new ApiInfo(
//                "SparkyService API",
//                "Authentication and routing backend",
//                "",
//                "",
//                contact,
//                "Apache2",
//                "",
//                vext);
//        
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo)
//                .pathMapping("/")
//                .apiInfo(ApiInfo.DEFAULT)
//                .forCodeGeneration(true)
//                .genericModelSubstitutes(ResponseEntity.class)
//                .ignoredParameterTypes(Pageable.class)
//                .ignoredParameterTypes(java.sql.Date.class)
//                .directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
//                .directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
//                .directModelSubstitute(java.time.LocalDateTime.class, Date.class)
//                .securityContexts(Lists.newArrayList(securityContext()))
//                .securitySchemes(Lists.newArrayList(apiKey()))
//                .useDefaultResponseMessages(true);
//        docket = docket.select()
//                .paths(PathSelectors.any())
//                .build();
//        watch.stop();
//        log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
//        return docket;
//    }
//
//    private ApiKey apiKey() {
//        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
//    }
//
//    private SecurityContext securityContext() {
//        return SecurityContext.builder()
//            .securityReferences(defaultAuth())
//            .forPaths(PathSelectors.regex(ControllerPath.GLOBAL_PREFIX + "/.*"))
//            .build();
//    }
//
//    private List<SecurityReference> defaultAuth() {
//        AuthorizationScope authorizationScope
//            = new AuthorizationScope("", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Lists.newArrayList(new SecurityReference("JWT", authorizationScopes));
//    }
}