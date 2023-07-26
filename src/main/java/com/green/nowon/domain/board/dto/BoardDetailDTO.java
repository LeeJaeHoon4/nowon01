package com.green.nowon.domain.board.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class BoardDetailDTO {
	private long bno;
	private String content;
	private String writer;
	private LocalDateTime createdDate;
	private String subject;
}


