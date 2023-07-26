package com.green.nowon.domain.board;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.green.nowon.domain.board.dto.BoardDTO;

public interface BoardEntityRepository extends JpaRepository<BoardEntity, Long> {
	
	void save(BoardDTO dto);

	BoardEntity findByBno(long bno);
	
	@Query(value="UPDATE board SET CONTENT = :content , SUBJECT = :subject WHERE bno = :bnum",nativeQuery=true)
	void updateContent(@Param("content") String content,@Param("bnum")long  bnum,@Param("subject") String subject);

	List<BoardEntity> findAllByIsShowTrue();
	
}
