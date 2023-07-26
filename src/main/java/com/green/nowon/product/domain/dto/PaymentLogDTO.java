package com.green.nowon.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentLogDTO {
	
	private long pno; //상품 번호(상품엔티티의 pk)
	private String url;//대표이미지
	private String title;//상품명
	private long price;//가격
	private long count;//수량
	private String date;//날짜
	private String impno;//결제번호
//	private long totalAmount;//총 가격 : 가격*수량
}