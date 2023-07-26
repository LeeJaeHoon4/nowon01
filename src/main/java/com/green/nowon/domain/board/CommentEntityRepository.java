package com.green.nowon.domain.board;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CommentEntityRepository extends JpaRepository<CommentEntity, Long> {

	List<CommentEntity> findByBno(BoardEntity board);

	CommentEntity findByCno(long lastCno);

	//bno(bnum)을 기반으로 댓글을 찾은뒤 limit offset쿼리를 사용해 원하는 갯수만큼 가져오는 쿼리 작성 (최신순 댓글 가져오기)
	//SELECT * FROM comment WHERE bno = 5 ORDER BY created_date DESC  LIMIT 5 OFFSET 5; 이런식의 쿼리가 날아감
	@Query(value = "SELECT * FROM comment WHERE bno = :bnum ORDER BY created_date DESC  LIMIT :limit OFFSET :offset", nativeQuery = true)
	List<CommentEntity> findByBnoWithLimit(@Param("bnum") long bnum,@Param("limit") int limit, @Param("offset") int offset);
}
