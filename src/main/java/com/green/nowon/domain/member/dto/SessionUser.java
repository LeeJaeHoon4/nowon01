package com.green.nowon.domain.member.dto;

import com.green.nowon.domain.member.MemberEntity;

import lombok.Getter;

@Getter
public class SessionUser {
	private String id;
	private String email;
	
	public SessionUser(MemberEntity member) {
		this.id = member.getId();
		this.email = member.getEmail();
	}

}

