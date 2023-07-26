package com.green.nowon.controller;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.service.BoardService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RestBoardController {
	
	private final BoardService service;
	//비동기 게시판 메인으로 이동
	@ApiOperation(value = "조건에 맞는 게시글 목록을 반환하는 메소드",httpMethod = "GET")
	@GetMapping("/board/rest-main")
	public ModelAndView restBoard() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("board/rest-board");
		return mv;
	}
	
	//어떠한 권한이 필요한지 알려줌
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "리스트 페이지 네이션 메소드", httpMethod = "PATCH")
	//String 타입이 아닌경우 example로 테스트 케이스를 돌릴수 있게해줌 example에 정의되지 않으면 에러나요
	//allowableValues = "1,2,3" 를 넣어주면 변수로 사용할 value를 여러개 선언 가능해요
	//String 타입은 string int는 int long은 long으로 표기함 아니면 에러남
    @ApiImplicitParam(name = "page", value = "페이지네이션 해당 페이지 번호", required=true,dataType = "int")
	@PatchMapping("/board/rest-list")
	public ModelAndView restBoardList(@RequestParam(defaultValue = "1") int page) {
		System.out.println(page);
		return service.getListWithPagination(page);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "게시글 검색 메소드",httpMethod = "PATCH")
    @ApiImplicitParams({
    			@ApiImplicitParam(name = "page", value = "페이지네이션 해당 페이지 번호", paramType="쿼리 매개변수" ,required = true, dataType = "int"),
    			@ApiImplicitParam(name = "query", value = "검색 조건 1=제목 2=내용 3=제목+내용",paramType="쿼리 매개변수" ,required = true,dataType = "int",allowableValues = "1,2,3"),
    			@ApiImplicitParam(name = "columnName", value = "검색내용",paramType="쿼리 매개변수" , required = true, dataType = "string")
    	})
	@PatchMapping("/rest-boards/search")
	public ModelAndView restSearchList( BoardSearchDTO dto) {
		return service.getListWithPagination(dto);
	}
	
}
