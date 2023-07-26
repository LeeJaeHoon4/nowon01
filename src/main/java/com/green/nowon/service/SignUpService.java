package com.green.nowon.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.green.nowon.domain.member.dto.ConsumerDTO;
import com.green.nowon.domain.member.dto.SalerDTO;

public interface SignUpService {


	void signupConsumer(ConsumerDTO dto);

	void signupSaler(SalerDTO dto);

	boolean idCheck(String id);


}
