package com.green.nowon.product.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.green.nowon.domain.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "product")
@Entity
public class ProductEntity {
	
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long pno;
	@Column(nullable = false, unique = true)
	private String title;
	@Column(nullable = false)
	private long price;
	@Column(nullable = false)
	private String simple;
	@Column(name = "detail", nullable = false)
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "MemberEntity_ID")
	private MemberEntity member;
	
	//추가
	@ManyToOne
	@JoinColumn(nullable = false)
	private CategoryEntity category;
	
	
}












