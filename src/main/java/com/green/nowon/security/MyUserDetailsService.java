package com.green.nowon.security;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;



public class MyUserDetailsService implements UserDetailsService  {
	
	
	@Autowired
	private MemberEntityRepository repo;		
	//formlogin에서 로그인 처리에 사용할 UserDetails를 구현한 User객체 리턴
	//일반 유저 로그인
	@Transactional
	@Override
	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
		MemberEntity result = repo.findById(id).orElseThrow(()->new UsernameNotFoundException(id));
		Collection<? extends GrantedAuthority> auth =  result.getRole()
													.stream()
													.map(role->new SimpleGrantedAuthority(role.roleName()))
													.collect(Collectors.toSet());
//		MemberEntity result = repo.findById(id).orElseThrow(()->new UsernameNotFoundException(id));
//		Set<SimpleGrantedAuthority> auth =  result.getRole()
//													.stream()
//													.map(role->new SimpleGrantedAuthority(role.roleName()))
//													.collect(Collectors.toSet());
		return new User(result.getId(), result.getPassword(), auth);
	}

}
