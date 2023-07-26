package com.green.nowon.product.domain.dto;

import com.green.nowon.product.domain.CategoryEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CategoryWriteDTO {
	private long cno;
	private String name;
	private CategoryEntity parent;
}