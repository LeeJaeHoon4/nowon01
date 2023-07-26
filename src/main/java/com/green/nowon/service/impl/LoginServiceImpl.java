package com.green.nowon.service.impl;

import org.springframework.stereotype.Service;

import com.green.nowon.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	@Override
	public void socialLogin(String code, String registrationId) {
		System.out.println(code);
		System.out.println(registrationId);
	}

}
