package com.green.nowon.service;

import org.springframework.stereotype.Service;


public interface LoginService {

	void socialLogin(String code, String registrationId);

}
