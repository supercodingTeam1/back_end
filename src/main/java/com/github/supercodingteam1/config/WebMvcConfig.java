package com.github.supercodingteam1.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //@Value("${uploadPath}")
    String uploadPath="file:/uploads/shop/";

    //@Value("${MAX_AGE_SECS}")
    Integer MAX_AGE_SECS=3600;

    /**
     *  MAX_AGE_SECS
     * CORS 설정에서 maxAge 는 브라우저가 CORS preflight 요청의 결과를 캐시하는
     * 시간을 설정하는 값입니다.
     * 이 값은 서버의 CORS 설정이 변경되지 않을 것으로 예상되는 시간을 기준으로 설정
     * 개발 환경에서는 maxAge 를 1시간(3600초)으로 설정하고,
     * 프로덕션 환경에서는 maxAge 를 24시간(86400초) 또는 그 이상으로 설정
     */
    //@Value("${CORS_MAX_AGE_SECS}")
    long CORS_MAX_AGE_SECS=86400;




    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .setCachePeriod(MAX_AGE_SECS)  // 개발 1시간
                .addResourceLocations(uploadPath);

        registry
                .addResourceHandler("/upload/**")
                .addResourceLocations(uploadPath+"/upload/")
                .setCachePeriod(MAX_AGE_SECS)  // 1시간
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }


    @Override
    public void addCorsMappings(CorsRegistry registry) {

        /**  
         *     restapi 가 아닌 경로까지 적용되면  restapi 아닌 페이지는  발생한다.
         /모든 경로에 대해 cors 허용 ex)
         registry.addMapping("/**")
         *      registry.addMapping("/cart/**"),
         *      registry.addMapping("/members/**")
         */
        
        registry.addMapping("/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(CORS_MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
        ).exposedHeaders("authorization");

        
        //cors 오류가 날경우 다음과 같이 개별 설정
        registry.addMapping("/auth/**")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)  // 'Access-Control-Allow-Credentials' header 는 요청 시 자격 증명이 필요함
                .maxAge(CORS_MAX_AGE_SECS)
                .allowedOrigins(
                        "http://localhost:3000/"
                ).exposedHeaders("authorization");  //authorization 헤더를 넘기 위해 exposedHeaders 조건을 추가


    }




}