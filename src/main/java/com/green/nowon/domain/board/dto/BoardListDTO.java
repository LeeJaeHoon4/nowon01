package com.green.nowon.domain.board.dto;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.green.nowon.domain.board.BoardEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardListDTO {
	private long bno;
	private String content;
	private LocalDateTime createdDate;
	private String writer;
	private String subject;
	private boolean isShow;
}
