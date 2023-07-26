package com.green.nowon.product.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "category")
@Entity
@Getter
public class CategoryEntity{
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long cno;
	@Column(nullable = false)
	private String name;
	
	@JoinColumn(name = "pno")
	@ManyToOne
	private CategoryEntity parent;
	
	//추가
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "category")
	private List<ProductEntity> products;
}