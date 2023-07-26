package com.green.nowon.service;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.dto.BoardControlDTO;
import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.domain.board.dto.authControlDTO;
import com.green.nowon.domain.member.dto.PassChangeDTO;

public interface InfoService {
	Map<String, String> authMailCheck(String email);

	void adminGranted(authControlDTO dto);

	ModelAndView getReportBoardWithPagination(BoardSearchDTO dto);

	void boardReportControl(BoardControlDTO dto);

	ModelAndView findByPno(String cate);

	Map<String,String> welcomePassCheck(String password, Authentication authentication);

	ModelAndView infoBoardSearchWithPagination(BoardSearchDTO dto);

	boolean userPassChange(PassChangeDTO dto, Authentication authentication);

	void userDelete(Authentication authentication);
	
	
}
