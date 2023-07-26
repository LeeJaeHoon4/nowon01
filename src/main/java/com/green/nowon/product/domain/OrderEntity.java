package com.green.nowon.product.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import com.green.nowon.domain.BaseDateEntity;
import com.green.nowon.domain.member.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "payment")
public class OrderEntity {
	//구조가 BasketEntity과 거의 똑같다. 주문 번호(imp_uid : String)만 더 넣으면 된다
	//BasketEntity은 바로 삭제 예정이기 때문에 이 Entity에 불러올수 없다
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long ono;//주문-상품 번호(단위가 주문 안의 한 상품)
	@Column(nullable = false)
	private long count;
	@Column(nullable = false)
	private String impno;//결제 번호(단위가 하나의 주문)
	@CreationTimestamp
	@Column(columnDefinition = "timestamp(6) null")
	LocalDateTime createdDate;
	
	//멤버 테이블 참조
	@ManyToOne
	@JoinColumn(name = "MemberEntity_ID")
	private MemberEntity member;
	
	//상품테이블 참조
	@ManyToOne
	@JoinColumn(name = "ProductEntity_ID")
	private ProductEntity product;
}