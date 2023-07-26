package com.green.nowon.domain.member.dto;

import com.green.nowon.domain.member.MemberEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO {
	private String id;
	private String email;
	private String name;
	
	  public MemberDTO(MemberEntity entity) {
	        this.id = entity.getId();
	        this.email = entity.getEmail();
	        this.name = entity.getName();
	    }
}
