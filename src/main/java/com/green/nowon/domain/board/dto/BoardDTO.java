package com.green.nowon.domain.board.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BoardDTO {
	private @NotBlank String subject;
	private @NotBlank String content;
}
