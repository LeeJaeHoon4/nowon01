package com.green.nowon.domain.member.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
@ToString
@Builder
@Getter
public class OAuthAttributes {
	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private String picture;
	public OAuthAttributes(Map<String, Object> attributes,
			String nameAttributeKey, String name,
			String email,			 String picture) {
	this.attributes = attributes;
	this.nameAttributeKey = nameAttributeKey;
	this.name = name;
	this.email = email;
	this.picture = picture;
}
/* of()
 * OAuth2User에서 반환하는 사용자 정보는 Map이기 때문에 값 하나하나 변환
 */

public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
	return OAuthAttributes.builder()
		.name((String) attributes.get("name"))
		.email((String) attributes.get("email"))
		.picture((String) attributes.get("picture"))
		.attributes(attributes)
		.nameAttributeKey(userNameAttributeName)
		.build();
}

}
