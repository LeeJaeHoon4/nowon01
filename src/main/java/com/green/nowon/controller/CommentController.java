package com.green.nowon.controller;

import java.util.List;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.domain.board.dto.CommentDTO;
import com.green.nowon.service.CommentService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CommentController {
	
	private final CommentService service;
	
	private final PasswordEncoder encoder;
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "댓글 저장",httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글번호", paramType="쿼리 매개변수" ,required = true, dataType = "long"),
		@ApiImplicitParam(name = "content", value = "내용",paramType="쿼리 매개변수" , required = true,dataType = "string")
		})
	@PostMapping("/board/comment")
	@ResponseBody
	public CommentDTO saveComment(@RequestParam("content") String content,@RequestParam("bno") long bno ,Authentication auth) {
		String writer = auth.getName();			
		return service.saveComment(content,writer,bno);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "댓글 불러오기",httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글번호", paramType="쿼리 매개변수" ,required = true, dataType = "long"),
		@ApiImplicitParam(name = "page", value = "댓글 페이지번호",paramType="쿼리 매개변수" , required = true,dataType = "int")
		})
	@PostMapping("/board/getComments")
	@ResponseBody
	public Map<String, Object> getComment(	
			@RequestParam("bno") long bno,
			@RequestParam(defaultValue = "1") int page,
			Authentication auth) {
		return service.findByBno(bno,page,auth);
	}
	
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "댓글 삭제",httpMethod = "DELETE")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글번호", paramType="쿼리 매개변수" ,required = true, dataType = "long"),
		@ApiImplicitParam(name = "pw", value = "비밀번호",paramType="쿼리 매개변수" , required = true,dataType = "string"),
		@ApiImplicitParam(name = "cno", value = "댓글번호", paramType="쿼리 매개변수" ,required = true, dataType = "long"),
		})
	@DeleteMapping("/board/delete")
	@ResponseBody
	public Map<String, Boolean> delteComment(
			@RequestParam("pw") String pw,
			@RequestParam("bno") long bno,
			@RequestParam("cno") long cno,
			Authentication auth){
			String pass = pw;
			String id = auth.getName();
			return service.findByBnoDelete(bno, pass, id, cno);
	}
		
}
