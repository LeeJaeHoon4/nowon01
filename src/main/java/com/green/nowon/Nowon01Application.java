package com.green.nowon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Nowon01Application {

	public static void main(String[] args) {
		SpringApplication.run(Nowon01Application.class, args);
	}
	
	@Bean
	PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(13);
	}
	
	//6.5 변경. S3 사용시 나오는 에러메세지 제거용
	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
	}
}
