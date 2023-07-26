package com.green.nowon.domain.board.dto;

import com.github.rkpunjal.sqlsafe.SQLInjectionSafe;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardSearchDTO {
	private String columnName;
	private String query;
	private int page;
	
	
}
