package com.green.nowon.product.domain;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;



public interface CategoryRepo extends JpaRepository<CategoryEntity, Long> {

	Optional<CategoryEntity> findByName(String string);

	List<CategoryEntity> findByParentIsNull();

	List<CategoryEntity> findByParentCno(long cno);
	
	
}
