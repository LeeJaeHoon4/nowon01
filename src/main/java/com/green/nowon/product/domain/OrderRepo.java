package com.green.nowon.product.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<OrderEntity, Long>{

	List<OrderEntity> findByMemberIdOrderByCreatedDateDesc(String name);

}
