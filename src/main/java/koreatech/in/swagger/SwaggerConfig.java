package koreatech.in.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("koreatech.in.controller"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(Arrays.asList(apiKey()));
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("KOIN API Document")
                .description("사용중인 서비스 \n" +
                             "- [koreatech.in](https://koreatech.in) \n" +
                             "- [bcsdlab.com](https://bcsdlab.com) \n" +
                             "- [Kakao 코인 - 한기대사람들 채널](https://pf.kakao.com/_twMBd)\n" +
                             "- BCSDLab Slack 질의응답 채널")
                .version("1.0.0")
                .build();
    }
}