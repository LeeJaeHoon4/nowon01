package com.green.nowon.security;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class MyUserDetails extends User implements OAuth2User {
	
	private String email;
	private String name;
	private Map<String, Object> attributes;
	
	public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public MyUserDetails(String email, String name, Collection<? extends GrantedAuthority> authorities,String pass) {
		this(email, pass, authorities);
		this.email =email;
		this.name = name;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return attributes;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

}
