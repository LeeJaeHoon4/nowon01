package com.green.nowon.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.domain.member.dto.ConsumerDTO;
import com.green.nowon.domain.member.dto.SalerDTO;
import com.green.nowon.service.SignUpService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class SignUpController {
	
	private final SignUpService service;
	
	private final MemberEntityRepository repo;
	
	
	@ApiOperation(value = "판매자 회원가입",httpMethod = "POST")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "입력된 회원 id", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
		@ApiImplicitParam(name = "password", value = "입력된 회원 비밀번호", paramType="쿼리 매개변수" ,required = true, dataType = "string")
})
	@PostMapping("/login/signup/consumer")
	@ResponseBody
	public String signupA(ConsumerDTO dto) {
		service.signupConsumer(dto);
		return "/";
	}
	
	
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@ApiOperation(value = "게시글 검색 메소드",httpMethod = "PATCH")
    @ApiImplicitParams({
    			@ApiImplicitParam(name = "id", value = "입력된 회원 id", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
    			@ApiImplicitParam(name = "email", value = "입력된 회원 이메일",paramType="쿼리 매개변수" ,required = true,dataType = "string"),
    			@ApiImplicitParam(name = "password", value = "입력된 회원 비밀번호", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
    			@ApiImplicitParam(name = "consumerName", value = "입력된 회원 이름",paramType="쿼리 매개변수" , required = true, dataType = "string")
    	})
	@PostMapping("/login/signup/saler")
	@ResponseBody
	public String signupB(SalerDTO dto) {
		service.signupSaler(dto);
		return "redirect:/";
	}
	
	//user-id input의 value를 {id:"String"}형식의 json데이터로 가져와서 service를 호출
	//return타입은 boolean
	@ApiOperation(value = "회원 아이디 중복 체크", httpMethod = "POST")
    @ApiImplicitParam(name = "id", value = "입력된 회원 ID",paramType="쿼리 매개변수", required=true,dataType = "string")
	@PostMapping("/idCheck")
	@ResponseBody
	public boolean idCheck(@RequestParam("id") String id) {
		boolean result  = service.idCheck(id);
		return result;
	}
	
	@ApiOperation(value = "회원 이메일 중복 체크 (사용X)", httpMethod = "GET")
    @ApiImplicitParam(name = "email", value = "입력된 회원 ID", required=true, paramType="쿼리 매개변수",dataType = "string")
	@GetMapping("/login/signup/email-check")
	@ResponseBody
	public boolean emailCheck(@RequestParam("email") String email) {
		System.out.println(email);
		return false;
	}
}
