package com.green.nowon.product.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.product.domain.dto.CategoryDTO;
import com.green.nowon.product.domain.dto.CategoryWriteDTO;
import com.green.nowon.product.domain.dto.PaymentLogDTO;
import com.green.nowon.product.domain.dto.ProductDTO;
import com.green.nowon.product.domain.dto.TempDTO;
import com.green.nowon.product.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ProductController {
	
	private final ProductService service;
	
/////////상품 관련///////
	//상품 리스트 페이지 이동 : 단순 이동. 비동기로 상품 리스트 불러옴
	@GetMapping("/product/list")
	public String list(Model model) {
		return "product/list";
	}
	
	//상품 리스트 조회 공통 : 페이지 이동 후 비동기로 데이터 불러옴
	@ResponseBody
	@GetMapping("/product/getList")
	public ModelAndView getProduct(@RequestParam(value = "cNo", defaultValue = "-1")String cNo, int count,Pageable pageable) {
		return service.getProduct(cNo, count, pageable);
	}

	//상품 등록 페이지 이동
	@GetMapping("/product/write")
	public String write() {
		return "product/write";
	}
	
	//비동기 상품 이미지 임시 등록 
	@ResponseBody
	@PostMapping("/product/temp-upload")
	public Map<String, String> tempUpload(MultipartFile tempFile, String tempKey) {
		return service.tempUpload(tempFile);
	}
	
	//상품 등록
	@PostMapping("/product")
	public String saveProduct(Authentication auth, ProductDTO dto) {
		service.saveProduct(auth, dto);
		return "redirect:/product/list";
	}
	
	//상품 디테일 페이지 이동
	@GetMapping("/product/detail/{no}")
	public String showDetail(@PathVariable("no") String productNo, Model model) {
		return service.showDetail(productNo , model);
	}
	
	
////////비동기 카테고리 관련////////
	//카테고리 로딩 : 맨 처음 조회시 - 대분류
	@ResponseBody
	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDTO>> list() {
		
		return service.listProcess();
	}
	
	//카테고리 로딩 : 대분류 mouse enter시 - 소분류
	@ResponseBody
	@GetMapping("/categories/{cno}") //{cno} 변수처리
	public ModelAndView subCategory(@PathVariable long cno) {
		return service.subCategoryProcess(cno);
	}
	
	
//////////////장바구니////////
	//장바구니에 물건 넣기(상품 상세페이지)
	@PostMapping("/product/basket")//수량, 상품 번호, 회원 아이디
	public String saveBasket(int count, Long no, Authentication auth) {
		
		service.saveBasket(count, no, auth.getName());
		
//		return "redirect:/product/detail/"+no;
		return "redirect:/product/list";
	}
	
	//비회원 장바구니에 물건 넣기(상품 상세페이지)
	@PostMapping("/product/basket2")//수량, 상품 번호, 회원 아이디
	public String saveBasket(@CookieValue(name="userId", required = false)String userId, int count, Long no,HttpServletResponse response, HttpServletRequest request) {
		
		service.saveBasket(userId, count, no, response,request);
		
//		return "redirect:/product/detail/"+no;
		return "redirect:/product/list";
	}
	
	//회원 장바구니 페이지로 이동
	@GetMapping("/product/basket")
	public String goBasket(Authentication auth, Model model) {
		service.goBasker(auth.getName(), model);
		return "product/basket";
	}
	
	//비회원 장바구니 페이지로 이동
	@GetMapping("/product/basket2")
	public String goBasket(@CookieValue(name="userId", required = false)String userId, HttpServletResponse response, HttpServletRequest request, Model model) {
		
		service.goBasker(userId, response, request, model);
		return "product/basket";
	}
	
	//회원 장바구니 항목 삭제
	@DeleteMapping("/product/basket/{no}")
	public String deleteBasket(@PathVariable long no) {
		
		service.deleteBasket(no);
		
		return "redirect:/product/basket";
	}

	//비회원 장바구니 항목 삭제
	@DeleteMapping("/product/basket/temp/{no}")
	public String deleteTempBasket(@PathVariable long no, @CookieValue(name="userId", required = false)String userId, HttpServletRequest request) {
		
		service.deleteTempBasket(no, userId, request);
		
		return "redirect:/product/basket2";
	}

	//회원 비회원 장바구니 항목 개수 조정
	@ResponseBody
	@PostMapping("/product/basket/change")
	public void basketChange(long count, long bno, @CookieValue(name="userId", required = false)String userId, HttpServletRequest request) {
		 
		//1. 회원 일때. 쿠키 값이 null => 회원 아이디 구해서 간다
		if(userId==null) {
			service.changeBasket(count, bno);
			
		//2. 비회원 일때. 쿠키 값이 null이 아님 => 세션값 구해서 간다 
		} else {
			HttpSession session = request.getSession();
			session.setMaxInactiveInterval(60*60);
			List<TempDTO> dtoList = (List<TempDTO>)session.getAttribute(userId);
			
			service.changeBasket(count, bno, dtoList, session, userId);
		}
	}
	
	
//////카카오, 결제
	//비동기 결제 검증 - 구현 안함
	@ResponseBody
	@PostMapping(value ="/product/none", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void none(@RequestBody Map<String, String> requestData, Principal principal) {
	}

	//결제 검증 성공시 작업 : 1) DB 정리 2) 결제 성공페이지로
	@GetMapping("/product/kakao")
	public String kakao(String imp_uid, String merchant_uid, Principal principal) {
		
		//1) DB 정리
		service.kakao(imp_uid,merchant_uid, principal);
		//2) 결제 성공페이지
		return "product/payment-success";
	}
	
	//주문정보 페이지 이동
	@GetMapping("/product/payment-log")
	public String orederLog() {
		return "product/payment-log";
	}
	
	//주문정보 페이지 정보 가져오기
	@ResponseBody
	@GetMapping("/product/getLog")
	public List<PaymentLogDTO> getLog(Principal principal) {
		return service.getLog(principal);
	}


///////추가만 따로
	
	//write 페이지 select태그 카테고리 DB에서 불러오기
	@ResponseBody
	@GetMapping("/categories/select")
	public List<CategoryWriteDTO> cateWrite() {
		return service.cateWrite();
	}
	
	@ResponseBody
	@GetMapping("/product/search")
	public ModelAndView searchProduct(String data) {
		return service.searchProduct(data);
	}
	
	
}

	





















