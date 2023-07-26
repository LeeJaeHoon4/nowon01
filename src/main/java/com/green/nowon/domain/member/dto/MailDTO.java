package com.green.nowon.domain.member.dto;

import lombok.Data;

@Data
public class MailDTO {
	//발송할 email 주소
	private String address;
	//받을 사람 이름 (회원가입 창에서 받아옴)
	private String name;
}
