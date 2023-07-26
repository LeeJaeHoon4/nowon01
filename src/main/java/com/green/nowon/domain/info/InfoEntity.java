package com.green.nowon.domain.info;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name="infoCategory")
@ToString
public class InfoEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long ino;
	
	@Column(nullable = false, unique = true)
	private String cateName;
	
	@ManyToOne
    @JoinColumn(name = "parent_no", referencedColumnName = "ino")
	private InfoEntity pno;
}
