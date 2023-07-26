package com.green.nowon.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchDTO {
	
	private long pno;
	private String title;
	private long price;
	private String imgUrl;
	
}
