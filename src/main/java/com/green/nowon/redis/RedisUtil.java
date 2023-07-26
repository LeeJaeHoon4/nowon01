package com.green.nowon.redis;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
//레디스사용시 쓸 기능들 미리 정의해서 bean으로 등록해두기
@Component
public class RedisUtil {
	 
	@Autowired
	private StringRedisTemplate template;
	
	//레디스에 저장 하는 메소드
	public void save(String key, String value) {
		//만료시간을 3분으로 함 바꾸길 원하면 time 변수 바꾸기
		//ValueOperation 객체에서 Duration  객체를 통해 만료 시간 설정하기 때문에 Duration타입으로 만들어줌
		long time = 3L;
		Duration duration = Duration.ofMinutes(time);
		//ValueOperation 객체를 통해 redis에 저장함
		ValueOperations<String, String> ops = template.opsForValue();
		ops.setIfAbsent(key, value, duration);
	}
	
	//인증번호와 이메일이 일치하는지 알아보는 메소드
	public boolean isMatch(String key,String verificationCode) {
		boolean flag = false;
		ValueOperations<String, String> ops = template.opsForValue();
		if(ops.get(key).equals(verificationCode)) {
			flag = true;
		}
		return flag;
	}
	
//	//인증번호를 많이 보냈는지 알아보기
//	public int findByEmail(String key) {
//		int count = 0;
//		ValueOperations<String, String> ops = template.opsForValue();
//	}
}
