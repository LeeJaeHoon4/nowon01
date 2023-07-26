package com.green.nowon.domain.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	List<MemberEntity> getListWithPagination(RowBounds rowBounds);

}
