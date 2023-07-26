package com.green.nowon.domain.board.dto;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardControlDTO {
	private List<String> bno;
}
