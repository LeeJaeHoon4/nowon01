package com.green.nowon.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.mail.util.ByteArrayDataSource;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.green.nowon.service.MailService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Controller
public class MailController {
	private final MailService service;
	
//	@PostMapping("/login/send")
//	@ResponseBody
//	public Map<String,byte[]> sendMessage(@RequestParam("name") String name, @RequestParam("address") String address){
//		//qrcodeImg를 만들고 있는 ByteArrayOutPutStream 가져오기
//		ByteArrayOutputStream  qrCodeImg = service.sendMail(name,address);
//		//qr코드 이미지의 정보 
//	    ByteArrayDataSource qrCodeData = new ByteArrayDataSource(qrCodeImg.toByteArray(), "image/png");
//	    //ajax의 응답 결과를 담을 Map세팅
//	    Map<String, byte[]> responseData = new HashMap<>();
//	    //byteArray로 만듬
//	    byte[] qrCodeBytes;
//	    try (InputStream inputStream = qrCodeData.getInputStream()) {
//	        qrCodeBytes = inputStream.readAllBytes();
//	    } catch (IOException e) {
//	    	//에러시 빈 배열 반환
//	        qrCodeBytes = new byte[0]; 
//	    }
//	    responseData.put("qrImg", qrCodeBytes);
//		return responseData;
//	}
	
	@ApiOperation(value = "인증번호와 이메일 redis에 저장하면서 qr코드 만들기", httpMethod = "GET")
	@ApiImplicitParam(name = "address", value = "회원가입 이메일주소",required=true, dataType = "string")
	@GetMapping("/login/send")
	public ResponseEntity<String> generateQrCode(@RequestParam("address") String address){
		String  response = service.getQrCodeData(address);
		return ResponseEntity.ok(response);
	}
	
	
	@ApiOperation(value = "이메일 보내기 기능",httpMethod = "POST")
    @ApiImplicitParams({
    			@ApiImplicitParam(name = "name", value = "회원 이름", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
    			@ApiImplicitParam(name = "address", value = "회원가입 이메일주소",required=true, dataType = "string")
    	})
	@PostMapping("/login/send")
	@ResponseBody
	public void sendEail(@RequestParam("name") String name, @RequestParam("address") String address) {
		System.out.println(address);
		service.sendEmail(name,address);
	}
	
	@ApiOperation(value = "이메일 인증 횟수체크",httpMethod = "POST")
    @ApiImplicitParams({
    			@ApiImplicitParam(name = "key", value = "redis에서 인증횟수 조회할 key", paramType="쿼리 매개변수" ,required = true, dataType = "string"),
    			@ApiImplicitParam(name = "address", value = "회원가입 이메일주소",required=true, dataType = "string")
    	})
	@PostMapping("login/check")
	@ResponseBody
	public Map<String, String> mailCheck(@RequestParam("key") String key, @RequestParam("address") @NotNull String address){
		return service.checkMail(key,address);
	}
	
	
	
}
