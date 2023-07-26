package com.green.nowon.configuration;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxThymeleafRequestContext;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration  implements WebMvcConfigurer{
	
	//thymeleaf사용중이고 actuator랑 충돌나면 이거 꼭 적어서 해결해주세요
	@Bean
	public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
		return new BeanPostProcessor() {

			@Override
			public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
				if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof SpringWebFluxThymeleafRequestContext) {
					customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
				}
				return bean;
			}

			private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(
					List<T> mappings) {
				List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null)
						.collect(Collectors.toList());
				mappings.clear();
				mappings.addAll(copy);
			}

			@SuppressWarnings("unchecked")
			private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
				try {
					Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
					field.setAccessible(true);
					return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}
	    @Bean
	    public Docket api() {
	        return new Docket(DocumentationType.SWAGGER_2)
	                .select()
	                //내 컨트롤러 패키지 경로
	                .apis(RequestHandlerSelectors.basePackage("com.green.nowon.controller"))
	                //내 컨트롤러 패키지 경로+controller위치
	                ////api/** 인경우 api패키지아래 모든 컨트롤러
	                .paths(PathSelectors.any())
	                .build()
	                .apiInfo(apiInfoMetaData());
	    }

	    
	    private ApiInfo apiInfoMetaData() {
	        return new ApiInfoBuilder()
	                .title("NO.1 쇼핑몰 & 게시판")
	                //@GetMapping등의  Mapping어노테이션 위에
	                //@PreAuthorize("hasRole('ROLE_MASTER')")권한 필요시
	                //@ApiOperation(value = "조건에 맞는 게시글 목록을 반환하는 메소드")
	                //@ApiImplicitParam(name = "tagIds", value = "검색할 태그 ID를 담은 리스트", dataType = "list")
	                //등의 식으로 작성
	                .description("API Endpoint Decoration")
	                //팀이름 , 홈페이지, 연락처 적기
	                .contact(new Contact("NO.1-TEAM", "https://www.dev-team.com/", "velo57234@gmail.com"))
	                //라이센스 이름
	                //딱히 없으면 MIT License/Apache License 2.0/GNU General Public License (GPL)/Creative Commons licenses 중에 골라적자(오픈 소스 라이브러리임)
	                .license("Apache License 2.0")
	                //라이센스 정보 표시 URL
	                .licenseUrl("https://example.com/license") 
	                .version("1.0.0")
	                .build();
	    }
	   
	    @Override
	    public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        registry.addResourceHandler("swagger-ui.html")
	                .addResourceLocations("classpath:/META-INF/resources/");
	        registry.addResourceHandler("/webjars/**")
	                .addResourceLocations("classpath:/META-INF/resources/webjars/");
	    }
	}
