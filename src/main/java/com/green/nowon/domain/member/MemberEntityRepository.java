package com.green.nowon.domain.member;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberEntityRepository extends JpaRepository<MemberEntity, Long> {

	Optional<MemberEntity> findById(String id);

	String findByNo(MemberEntity no);

	Optional<MemberEntity> findByEmail(String address);	
}
