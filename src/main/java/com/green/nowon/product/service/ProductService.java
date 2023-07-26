package com.green.nowon.product.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.product.domain.dto.CategoryDTO;
import com.green.nowon.product.domain.dto.CategoryWriteDTO;
import com.green.nowon.product.domain.dto.PaymentLogDTO;
import com.green.nowon.product.domain.dto.ProductDTO;
import com.green.nowon.product.domain.dto.TempDTO;

public interface ProductService {

	Map<String, String> tempUpload(MultipartFile tempFile);

	void saveProduct(Authentication auth, ProductDTO dto);

	String showDetail(String productNo, Model model);

	ResponseEntity<List<CategoryDTO>> listProcess();

	ModelAndView subCategoryProcess(long cno);

	void saveBasket(int count, Long no, String name);
	
	void saveBasket(String userId, int count, Long no,HttpServletResponse response, HttpServletRequest request);

	void goBasker(String name, Model model);
	
	void goBasker(String userId, HttpServletResponse response, HttpServletRequest request, Model model);
	
	void deleteBasket(Long no);

	void moveBasket(List<TempDTO> dtoList, String name);

	void deleteTempBasket(long no, String userId, HttpServletRequest request);

	void changeBasket(long count, long bno);

	void changeBasket(long count, long bno, List<TempDTO> dtoList, HttpSession session, String userId);

	List<CategoryWriteDTO> cateWrite();

	ModelAndView getProduct(String cNo, int count, Pageable pageable);

	void kakao(String imp_uid, String merchant_uid, Principal principal);

	List<PaymentLogDTO> getLog(Principal principal);

	ModelAndView searchProduct(String data);



}
