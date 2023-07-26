package com.green.nowon.product.domain.dto;

import java.util.List;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.green.nowon.domain.member.MemberEntity;

import lombok.Data;

@Data
public class ProductDTO {
	
	private String title;
	private long price;
	private String simple;
	private String content;
	
	private long bigcate;
	private long smallcate;
	
	private List<String> imgs;
	private List<String> tempKey;
	
	@ManyToOne
	@JoinColumn(name = "MemberEntity_ID")
	private MemberEntity member;
	
}
