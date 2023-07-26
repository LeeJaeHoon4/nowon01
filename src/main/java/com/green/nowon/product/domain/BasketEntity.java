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
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name="basket")
@Entity
@Setter
public class BasketEntity {
	
	//이 장바구니 항목의 고유 번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long no;

	//상품 수량
	@Column(nullable = false)
	private long count;
	
	//멤버 테이블 참조
	@ManyToOne
	@JoinColumn(name = "MemberEntity_ID")
	private MemberEntity member;
	
	//상품테이블 참조
	@ManyToOne
	@JoinColumn(name = "ProductEntity_ID")
	private ProductEntity product;
	
}











