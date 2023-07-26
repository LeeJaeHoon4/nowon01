package com.green.nowon.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class TempDTO {
	//상품 번호, 장바구니 수량
	private long no;
	private int count;
	
}
