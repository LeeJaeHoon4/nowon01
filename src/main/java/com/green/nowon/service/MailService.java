package com.green.nowon.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.mail.util.ByteArrayDataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

public interface MailService {

	ByteArrayOutputStream  sendMail(String name, String address);

	Map<String, String> checkMail(String key, String address);

	String getQrCodeData(String address);

	void sendEmail(String name, String address);

}
