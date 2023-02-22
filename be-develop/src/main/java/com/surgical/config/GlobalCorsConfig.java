//package com.medical.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class GlobalCorsConfig implements WebMvcConfigurer{
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry){
//        //映射路徑
//        System.out.println("CORS CONFIG INIT");
//        
//        registry.addMapping("/**")
//            //允許跨網域請求的來源
//            .allowedOrigins("*")
//            //允許跨域攜帶cookie資訊，預設跨網域請求是不攜帶cookie資訊的。
//            .maxAge(3600)
//            .allowCredentials(true)
//            //允許使用那些請求方式
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
//            //允許哪些Header
//            .allowedHeaders("*");
//            //可獲取哪些Header（因為跨網域預設不能取得全部Header資訊）
//            //.exposedHeaders("Header1", "Header2");
//    }
//}
