package com.green.nowon.product.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class FindDetailDTO {
	//상품 디테일 페이지 DTO
	//상품번호, 상품명, 가격, 상세설명 + 사진 전부
	
	//상품 번호
	private long no;
	//상품 이름
	private String title;
	//상품 가격
	private long price;
	//상품 상세 설명
	private String detail;
	
	//상품 이미지 url
	private List<String> imgUrl;
	
}
