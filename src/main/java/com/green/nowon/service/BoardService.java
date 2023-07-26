package com.green.nowon.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.BoardEntity;
import com.green.nowon.domain.board.dto.BoardDTO;
import com.green.nowon.domain.board.dto.BoardSearchDTO;

public interface BoardService {

	void save(BoardDTO dto,  Authentication auth);

	String findAll(Model model);

	String findByBno(long bno, Model model,Authentication auth);

	Map<String, Object> findByBno(long bno,String content,String subject);

	void deleteByBno(long bno);

	ModelAndView getListWithPagination(int page);

	ModelAndView getListWithPagination(BoardSearchDTO dto);

	void controlReadCount(String ip, long bno);

	Map<String, Object> postReport(long bno,String ip);
}
