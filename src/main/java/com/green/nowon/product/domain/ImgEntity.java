package com.green.nowon.product.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "productImgs")
public class ImgEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long ino;
	private String orgName; //파일이름
	private String newName; //이미지 url, S3폴더 이름은 뺐다. 어차피 공통적으로 쓰기에
							//하지만 매번 이미지의 주소가 바뀌는 프로그램이면 계속 넣어줘야한다
	private boolean defYn; //대표 이미지인지
	@ManyToOne
	@JoinColumn(name = "ProductEntity_ID")
	private ProductEntity product;
}
