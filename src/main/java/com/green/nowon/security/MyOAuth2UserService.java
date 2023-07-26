package com.green.nowon.security;



import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import com.green.nowon.domain.member.MemberRole;


//public class DefaultOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>
//OAuth2UserService<OAuth2UserRequest, OAuth2User>를 구현해주는 클래스 DefaultOAuth2UserService를 사용한다.
public class MyOAuth2UserService extends DefaultOAuth2UserService {

	@Autowired
	//패스워드 인코더가 스타트 파일에 지정되지 않은 등의 이유로 나중에 지정할시 순환 참초 및 빌드 순서에 이상이 생길 수 있다.
	// 그럴경우 fetchType을lazy로 해주면되긴함
	private PasswordEncoder encoder;
	@Override
	//UserDetails에서는 loadUserByUsername(String id)가 넘어온것 처럼 oauth2에서는 userRequest 객체가 넘어온다.
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		//OAuth2UserService<OAuth2UserRequest, OAuth2User> 에서 사용하는 loadUser불러와서 사용
		//소셜 로그인할 소셜에서 인증 처리 완료휴 유저 정보를 받아온것
		OAuth2User oAuth2User = super.loadUser(userRequest);
		//provider 즉 내가 사용하려는 소셜 로그인 주체가 누구인지 가져옴 ex) google,kakao ...
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		String registrationId = clientRegistration.getRegistrationId();
		//소셜 로그인시 사이트에서 전달해주는 데이터를 찍어보기 위함 정보에 맞게 커스터마이징 해야함
		Map<String,Object> attributes = oAuth2User.getAttributes();
		attributes.keySet().forEach(key->{
			Object value =attributes.get(key);
			System.out.println(key+":"+value);
		});
		return myLoadUser(registrationId,oAuth2User);
	}

	private OAuth2User myLoadUser(String registrationId, OAuth2User oAuth2User) {
		String email = null;
		String id = null;
		String name = null;
		String pass = null;
		//권한설정을 위한 변수 설정
		List<GrantedAuthority> authorities = new ArrayList<>();
		if(registrationId.equals("google")) {
			email = oAuth2User.getAttribute("email");
			id = oAuth2User.getAttribute("email");
			name = oAuth2User.getAttribute("name");
			String sub = oAuth2User.getName();//sub
			pass = oAuth2User.getAttribute("sub");
			pass = encoder.encode(pass);
			authorities.add(new SimpleGrantedAuthority("ROLE_SOCIAL"));
		}else if(registrationId.equals("naver")) {
			Map<String, Object> response =  oAuth2User.getAttribute("response");
			email = (String) response.get("email");
			id = (String) response.get("email");
			name = (String) response.get("name");
			pass = encoder.encode((String) response.get("id"));
			authorities.add(new SimpleGrantedAuthority("ROLE_SOCIAL"));
		}
		
		return  new MyUserDetails(email, name, authorities, pass);
	}

}
