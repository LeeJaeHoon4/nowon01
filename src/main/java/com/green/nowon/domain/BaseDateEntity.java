package com.green.nowon.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;

//MappedSuperclass를 통해 자동으로 다른 테이블에 삽입
@MappedSuperclass
public class BaseDateEntity {
	
	@CreationTimestamp
	@Column(columnDefinition = "timestamp(6) null")
	LocalDateTime createdDate;
	
	@UpdateTimestamp
	@Column(columnDefinition = "timestamp(6) null")
	LocalDateTime updateDate;
	
}
