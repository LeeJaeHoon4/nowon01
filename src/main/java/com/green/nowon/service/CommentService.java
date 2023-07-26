package com.green.nowon.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.green.nowon.domain.board.dto.CommentDTO;

public interface CommentService {

	CommentDTO saveComment(String content, String writer,Long bno);

	Map<String, Object> findByBno(long bno, int page, Authentication auth);

	Map<String, Boolean> findByBnoDelete(long bno, String pass, String id,long cno);
}
