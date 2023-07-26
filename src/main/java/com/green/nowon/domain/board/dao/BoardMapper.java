package com.green.nowon.domain.board.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.domain.board.dto.RestBoardDTO;

@Mapper
public interface BoardMapper {

	List<RestBoardDTO> getListWithPagination(RowBounds rowBounds);

	int countAll();

	List<RestBoardDTO> findAllBySearch(BoardSearchDTO dto, RowBounds rowBounds);

	int coutAllBySearch(BoardSearchDTO dto);

	List<RestBoardDTO> getListWithReportCount(BoardSearchDTO dto,RowBounds rowBounds);

	int countAllByReportCount();

	List<RestBoardDTO> findAllBySearchInfoBoard(BoardSearchDTO dto, RowBounds rowBounds);

}
