package com.green.nowon.domain.board.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CommentDTO {
	private String writer;
	private LocalDateTime createdDate;
	private String content;
	private long cno;
}
