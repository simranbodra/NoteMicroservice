package com.bridgelabz.microserice.fundoonotes.note.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import static springfox.documentation.builders.PathSelectors.regex;
import static com.google.common.base.Predicates.or;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket postsApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("public-api")
				.apiInfo(apiInfo()).select().paths(PathSelectors.any()).build();
	}
	
	/*private Predicate<String> postPaths() {
		return or(regex("/.*"), regex("/note.*"));
	}*/


	@SuppressWarnings("deprecation")
	  private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("FundooNoteAppln").description("Taking notes using Spring Boot And MongoDB ")
                .contact("simranbodra6@gmail.com").version("1.0").build();
    }
	
	@Bean
	public SecurityConfiguration security() {
		return new SecurityConfiguration(null, null, null, null, "Authorization",
				ApiKeyVehicle.HEADER, "Authorization", ",");
	}

}

	    /*@ApiModelProperty(required = true,example = "2016-01-01")
	    @JsonFormat(pattern = DATE_FORMAT)
	    private LocalDate date; */

