package com.green.nowon.domain.info;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InfoPageCategoryRepository extends JpaRepository< InfoEntity, Long> {

	Optional<InfoEntity> findByCateName(String cate);
	List<InfoEntity> findListByPno(InfoEntity infoEntity);
}
