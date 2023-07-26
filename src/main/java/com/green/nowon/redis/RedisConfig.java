package com.green.nowon.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {
	//application context 호스트
	@Value("${spring.redis.host}")
	private String host;
	//application context 포트
	@Value("${spring.redis.port}")
	private int port;
	
	@Bean
	RedisConnectionFactory factory() {
		//jedis : 단일 쓰레드에서만 동작하며 멀티 쓰레드는 별도의 세팅이 필요하다
		//lettuce : ㅣ비동기 및 반응형 프로그래밍 모델을 지원하며 성능과 확장성이 뛰어나다
		return new LettuceConnectionFactory(host, port);
	}
	
	@Bean
	//?<- 모든 데이터 타입으로 템플릿을 저렇게 만들면 확장성이 뛰어나다
	RedisTemplate<?, ?> redisTemplate(){
		RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory());
		return redisTemplate;
	}
}
