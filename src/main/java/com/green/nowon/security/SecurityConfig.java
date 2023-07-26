package com.green.nowon.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@EnableWebSecurity
public class SecurityConfig  {
	
	@Bean
	UserDetailsService userDetails() {
		return new MyUserDetailsService();
	}
	
	@Autowired
	private CustomAuthenticationSuccessHandler sHandler;
	@Autowired
	private CustomAuthenticationFailureHandler fHandler;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http)  throws Exception {
		
		http
		//header옵션등을 통해서 보안강화 가능
		//xss공격을 브라우저의 정책에 따라 막음
		//.headers(header-> header.contentSecurityPolicy("default-src 'self'"))
		.authorizeHttpRequests(request -> request
										//부트 3.0 버전 이상에서는 antMatcher가 아니라 requestMathcer로 사용함
										.antMatchers("/css/**","/js/**","/image/**","/resources/**").permitAll()
										.antMatchers("/layout/**","/login/**","/emart/**","/idCheck","/infopage/**").permitAll()
										.antMatchers("/board/**","/user/**").hasRole("USER")
										.antMatchers("/","/login/*","/board", "/favicon.ico").permitAll()
										.antMatchers("/admin/*").hasRole("ADMIN")
										//swagger사용을 위한 세큐리티 설정 
										.antMatchers("/swagger-ui/**", "/v3/api-docs/**","/",
						                        "/v2/api-docs", "/swagger-resources/**", "/swagger-ui/index.html", "/swagger-ui.html","/webjars/**", "/swagger/**",
						                        "/h2-console/**",
						                        "/favicon.ico").hasRole("ADMIN")
										/* 6.5 변경. 상품 등록에 대해 다 열어놓은 상태 */
										.antMatchers("/product/**","/product","/categories","/categories/**").permitAll()
										.anyRequest()
										.authenticated()
										)
		.formLogin(form -> form
							.loginPage("/login/forlogin")
							.loginProcessingUrl("/login")
							.usernameParameter("id")
							.passwordParameter("password")
							.successHandler(sHandler)
							.failureHandler(fHandler)
							.permitAll()
							)
		.logout(logout -> logout
							.logoutUrl("/logout")
							.logoutSuccessUrl("/")
							.deleteCookies("JSESSIONID")
							.permitAll()
							)
		//소셜 로그인 시 oauth2방식을 사용해서 이쪽으로 빠짐
		.oauth2Login(oauth -> oauth
								.defaultSuccessUrl("/",true)
								//인증 완료 이후 사용자 정보 가공을 위한 처리
								.userInfoEndpoint()
									//OAuth2UserService<OAuth2UserRequest, OAuth2User> userService <<- UserDetailsService에서 반환하는 User객체 처럼 필요함
									.userService(myOAuth2UserService())
									);
		
		return http.build();
	}
	
	@Bean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> myOAuth2UserService() {
		return new MyOAuth2UserService();
	}
	
}
