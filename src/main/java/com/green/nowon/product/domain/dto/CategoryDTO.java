package com.green.nowon.product.domain.dto;

import com.green.nowon.product.domain.CategoryEntity;

import lombok.Getter;

@Getter
public class CategoryDTO {
	private long cno;
	private String name;
	
	public CategoryDTO(CategoryEntity e) {
		this.cno = e.getCno();
		this.name = e.getName();
	}
}
