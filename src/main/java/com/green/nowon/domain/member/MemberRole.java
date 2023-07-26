package com.green.nowon.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
//회원 등급을 관리하는 ENUM
@RequiredArgsConstructor
public enum MemberRole {
	USER("ROLE_USER","회원"),
	SALER("ROLE_Saler","판매자"),
	ADMIN("ROLE_ADMIN","관리자"),
	MASTER("ROLE_MASTER","마스터"),
	SOCIAL("ROLE_SOCIAL","소셜회원");
	
	private final String roleName;
	private final String korName;
	
	public final String roleName() {
		return roleName;
	}
	
	public final String korName() {
		return korName;
	}
}
