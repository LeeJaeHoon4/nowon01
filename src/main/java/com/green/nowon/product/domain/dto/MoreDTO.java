package com.green.nowon.product.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MoreDTO {
	String cNo;
	long more;//0 : 더보기 버튼 있음 // 1: 더보기 버튼 없음
}
