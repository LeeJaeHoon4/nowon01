package com.green.nowon.product.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<ProductEntity, Long>{

	Page<ProductEntity> findByCategoryIn(List<CategoryEntity> cEList, Pageable pageable);

	Page<ProductEntity> findByCategory(CategoryEntity categoryEntity, Pageable pageable);

	Page<ProductEntity> findByTitleLike(String realCNo, Pageable pageable);

	
}
