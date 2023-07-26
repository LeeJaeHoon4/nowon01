package com.green.nowon.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.domain.board.dto.BoardDTO;
import com.green.nowon.service.BoardService;
import com.green.nowon.service.CommentService;
import com.green.nowon.util.clientIP.ClientIP;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	//서비스  DI
	private final BoardService service;
	//댓글 조회를 위한 CommentService DI 
	private final CommentService cService;
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "비동기가 아닌 게시판 링크(현재 사용 X)",httpMethod = "GET")
	@GetMapping("/board/main")
	public String boardMain(Model model) {
		return service.findAll(model);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "글쓰기 페이지 이동",httpMethod = "GET")
	@GetMapping("/board/write")
	public String boardWrite() {
		return "/board/write";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "쓴글 저장",httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "subject", value = "제목", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
		@ApiImplicitParam(name = "content", value = "내용",paramType="쿼리 매개변수" , required = true,dataType = "string")
		})
	@PostMapping("/board/save")
	@ResponseBody
	public String boardSave(@Valid BoardDTO dto, Authentication auth) {
		//Authentication << 현재 페이지에 인증된 회원 정보
			service.save(dto,auth);
			return "/board/rest-main";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "글 상세보기 페이지",httpMethod = "GET")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글 번호", paramType="쿼리 매개변수" ,required = true, example="741",dataType = "long")
		})
	@ApiResponses(value = {
			@ApiResponse(code = 200,  message = "성공" ),
		    @ApiResponse(code = 404, message = "실패" )
		})
	@GetMapping("/board/{bno}")
	public String boardDetails(@PathVariable("bno") long bno,Model model,Authentication auth,HttpServletRequest request) {
		//ip를 확인
		String ip = ClientIP.getClientIP(request);
		//redis를 이용한 게시글 조횟수 처리 
		service.controlReadCount(ip,bno);
		//게시글 상세 조회
		return service.findByBno(bno, model, auth);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "글 수정하기",httpMethod = "PUT")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "subject", value = "제목", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
		@ApiImplicitParam(name = "content", value = "내용",paramType="쿼리 매개변수" , required = true,dataType = "string"),
		@ApiImplicitParam(name = "bno", value = "글 번호", paramType="쿼리 매개변수" ,required = true, dataType = "long")
		})
	@PutMapping("/board/postChange/{bno}")
	@ResponseBody
	//Map<String,Object> 형식의 데이터는 자동으로 JSON형식의 데이터로 매핑된다.
	public Map<String, Object> postChangeAndSave(
			@RequestParam("subject") String subject,
			@RequestParam("content") @NotBlank String content,
			@PathVariable("bno") long bno) {
		return service.findByBno(bno,content,subject);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "글 삭제하기",httpMethod = "DELETE")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글 번호", paramType="쿼리 매개변수" ,required = true, example="741",dataType = "long")
		})
	@DeleteMapping("/board/delete/{bno}")
	@ResponseBody
	public String postDelete(@PathVariable("bno") long bno){
		service.deleteByBno(bno);
		return "/board/rest-main";
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "글 신고하기",httpMethod = "GET")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "bno", value = "글 번호", paramType="쿼리 매개변수" ,required = true, example="741",dataType = "long")
		})
	@GetMapping("/board/{bno}/report")
	@ResponseBody
	public Map<String, Object> postReport(@PathVariable("bno") long bno,HttpServletRequest request) {
		String ip = ClientIP.getClientIP(request);
		return service.postReport(bno,ip);
	}
}
