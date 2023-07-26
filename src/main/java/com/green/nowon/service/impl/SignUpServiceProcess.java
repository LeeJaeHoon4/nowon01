package com.green.nowon.service.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.domain.member.MemberRole;
import com.green.nowon.domain.member.dto.ConsumerDTO;
import com.green.nowon.domain.member.dto.SalerDTO;
import com.green.nowon.service.SignUpService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SignUpServiceProcess implements SignUpService {
	
	private final MemberEntityRepository repo;
	
	private final PasswordEncoder encoder;
	
	@Override
	public void signupConsumer(ConsumerDTO dto) {
		
		repo.save(MemberEntity.builder()
				.id(dto.getId())
				.password(encoder.encode(dto.getPassword()))
				.email(dto.getEmail())
				.name(dto.getConsumerName())
				.build()
				.addRole(MemberRole.USER));
	}

	@Override
	public void signupSaler(SalerDTO dto) {
		
		repo.save(MemberEntity.builder()
				.id(dto.getId())
				.password(encoder.encode(dto.getPassword()))
				.build()
				.addRole(MemberRole.USER)
				.addRole(MemberRole.SALER)
				);
	}

	//받아온 id를 바탕으로 데이터 베이스에 접근하여 이미 사용중인 id인지 판별하고 
	// 만약 db에 존재하는 id일경우에 false 리턴
	@Override
	public boolean idCheck(String id) {
		Optional<MemberEntity> reuslt = repo.findById(id);
		//리턴할 flag값 설정
		boolean flag = true;
		//db에 정보가 존재할경우 false로 세팅
		if(reuslt.isPresent())
			flag = false;
		return flag;		
	}

}
