package com.green.nowon.product.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.green.nowon.domain.member.MemberEntity;

public interface BasketRepo extends JpaRepository<BasketEntity, Long> {

	Optional<List<BasketEntity>> findByMember(MemberEntity mE);

	Optional<BasketEntity> findByMemberAndProduct(MemberEntity mE, ProductEntity pE);

	Optional<BasketEntity> findByProduct(ProductEntity pE);

	Optional<List<BasketEntity>> findByMember(Optional<MemberEntity> findById);

	void deleteByMember(MemberEntity mE);

}
