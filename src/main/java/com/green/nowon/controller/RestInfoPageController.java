package com.green.nowon.controller;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.dto.BoardControlDTO;
import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.domain.board.dto.authControlDTO;
import com.green.nowon.domain.member.dto.PassChangeDTO;
import com.green.nowon.service.InfoService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class RestInfoPageController {
	
	private final InfoService service;
	
	@PreAuthorize("hasRole('ROLE_MASTER')")
	@ApiOperation(value = "관리자 권한 부여 페이지 이동",httpMethod = "PATCH")
	@PatchMapping("/info/auth/authControll")
	public ModelAndView authControllPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("infopage/layout/admin-auth-layout");
		return mv;
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "신고수로 게시글 검색하는 메소드",httpMethod = "PATCH")
    @ApiImplicitParams({
    			@ApiImplicitParam(name = "page", value = "페이지네이션 해당 페이지 번호", paramType="쿼리 매개변수" ,required = true, dataType = "int"),
    			@ApiImplicitParam(name = "query", value = "검색 조건 1=제목 2=내용 3=제목+내용",paramType="쿼리 매개변수" ,required = true,dataType = "int",allowableValues = "1,2,3"),
    			@ApiImplicitParam(name = "columnName", value = "검색내용",paramType="쿼리 매개변수" , required = true, dataType = "string")
    	})
	@PatchMapping("/info/board/boardControl")
	public ModelAndView boardControl(BoardSearchDTO dto) {
		return service.getReportBoardWithPagination(dto);
	}
	
	//관리자 권한 부여전 유효한 이메일인지 확인
	@PreAuthorize("hasRole('ROLE_MASTER')")
	@ApiOperation(value = "리스트 페이지 네이션 메소드", httpMethod = "POST")
    @ApiImplicitParam(name = "email", value = "관리자 권한 부여하려는 email",paramType="redis키값", required=true,dataType = "string")
	@PostMapping("/info/auth/mailCheck")
	public  Map<String, String> authMailCheck(@RequestParam("email")String email){
		return service.authMailCheck(email);
	}
	
	@PreAuthorize("hasRole('ROLE_MASTER')")
	@ApiOperation(value = "리스트 페이지 네이션 메소드", httpMethod = "POST")
	@PostMapping("/info/auth/adminGranted")
	public void adminGranted(@ApiParam(value = "이메일 리스트", required = true)@RequestBody authControlDTO dto) {
		 service.adminGranted(dto);
	}
	//체크된 버튼의 value를 ArrayList<String>형태로 받아와서 
	//역 직렬화를 위해 DTO를 만들고 받아줌
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "게시글 복구", httpMethod = "POST")
	@PostMapping("/info/auth/boardRePost")
	public void boardDelete(@ApiParam(value = "게시글 번호", required = true)@RequestBody BoardControlDTO dto) {
		service.boardReportControl(dto);
	}
	
	@PreAuthorize("!hasRole('ROLE_MASTER')")
	@ApiOperation(value = "계정 정보 변경 페이지",httpMethod = "PATCH")
	@PatchMapping("/info/board/infoControl")
	public ModelAndView MemberinfoChange() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("/infopage/layout/info-change-layout");
		return mv;
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "탭메뉴 구성", httpMethod = "PATCH")
	@ApiImplicitParam(name = "cate", value = "카테고리pk", required=true,paramType="쿼리 매개변수",dataType = "long")
	@PatchMapping("/infopage/cate")
	public ModelAndView cateLIst(@RequestParam("cate") String cate) {
		return service.findByPno(cate);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "관리 페이지 웰컴 페이지 띄우기", httpMethod = "PATCH")
	@PatchMapping("/infopage/welcome")
	public ModelAndView welcomePage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("infopage/layout/infopage-welcome");
		return mv;
	}
	
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@ApiOperation(value = "리스트 페이지 검색 및 페이지네이션", httpMethod = "PATCH")
	  @ApiImplicitParams({
			@ApiImplicitParam(name = "page", value = "페이지네이션 해당 페이지 번호", paramType="쿼리 매개변수" ,required = true, dataType = "int"),
			@ApiImplicitParam(name = "query", value = "검색 조건 1=제목 2=내용 3=제목+내용",paramType="쿼리 매개변수" ,required = true,dataType = "int",allowableValues = "1,2,3"),
			@ApiImplicitParam(name = "columnName", value = "검색내용",paramType="쿼리 매개변수" , required = true, dataType = "string")
	})
	@PatchMapping("/infopage/boardSearch")
	public ModelAndView infoBoardListSearch(BoardSearchDTO dto) {
		return service.infoBoardSearchWithPagination(dto);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "리스트 페이지 네이션 메소드", httpMethod = "PATCH")
	@PostMapping("/infopage/welcome/passCheck")
	public Map<String,String> welcomePassCheck(@ApiParam(value = "비밀번호를 담은 요청 바디", required = true)@RequestBody Map<String,String> requestBody) {
		String password = requestBody.get("password");
		//securityContextHolder에서 정보 가져오긴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    //String username = authentication.getName();
		return  service.welcomePassCheck(password,authentication);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "회원 비밀번호 변경", httpMethod = "POST")
	  @ApiImplicitParams({
			@ApiImplicitParam(name = "password", value = "회원 기존 비밀번호", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
			@ApiImplicitParam(name = "newPassword", value = "변경할 비밀번호",paramType="쿼리 매개변수" ,required = true,dataType = "string"),
			@ApiImplicitParam(name = "newPasswordC", value = "변경할 비밀번호 확인",paramType="쿼리 매개변수" , required = true, dataType = "string")
	})
	@PostMapping("/infopage/passChange")
	public boolean passChange(PassChangeDTO dto) {
		System.out.println(dto.toString());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return  service.userPassChange(dto,authentication);
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "회원 탈퇴", httpMethod = "DELETE")
	@DeleteMapping("/infopage/userDelete")
	public void passChange() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		service.userDelete(authentication);
	}
	
}
