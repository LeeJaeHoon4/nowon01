package com.green.nowon.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//상품 일반 조회 DTO
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FindNomalDTO {
	
	//상품 번호
	private long no;
	//상품 이름
	private String title;
	//상품 간단 설명
	private String simple;
	//상품 가격
	private long price;
	
	//상품 이미지 url
	private String imgUrl;
	
	
}
