package com.green.nowon.product.domain.dto;

import javax.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class BasketDTO {
	
	private long no;
	private String title;
	private long price;
	private String imgUrl;
	private long count;
	
}
