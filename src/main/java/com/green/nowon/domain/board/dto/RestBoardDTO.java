package com.green.nowon.domain.board.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RestBoardDTO {
	private long bno;
	private String content;
	private long mno;
	private LocalDateTime createdDate;
	private String writer;
	private int readCount;
	private String subject;
}
