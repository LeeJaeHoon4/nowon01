package com.green.nowon.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgRepo extends JpaRepository<ImgEntity, Long> {


	List<ImgEntity> findByProduct(ProductEntity productEntity);

	ImgEntity findByProductAndDefYn(ProductEntity pE, boolean b);
	

}
