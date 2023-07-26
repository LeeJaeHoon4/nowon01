package com.green.nowon.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.green.nowon.domain.board.BoardEntity;
import com.green.nowon.domain.board.BoardEntityRepository;
import com.green.nowon.domain.board.CommentEntity;
import com.green.nowon.domain.board.CommentEntityRepository;
import com.green.nowon.domain.board.dto.CommentDTO;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.service.CommentService;
import com.green.nowon.util.passwordMatcher.PasswordMatchUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
	
	private final CommentEntityRepository cRepo;
	
	private final BoardEntityRepository bRepo;
	
	private final MemberEntityRepository mRepo;
	
	private final PasswordEncoder encoder;
	@Override
	public CommentDTO saveComment(String content, String writer,Long bno) {
			//mno 라는 컬럼을 새로 생성 했음
			// mno는 member테이블의 pk no를 참조하는 fk임
			Optional<MemberEntity> findMember = mRepo.findById(writer);
			MemberEntity member = findMember.get();
			//댓글을 db에 저장함
			BoardEntity board = bRepo.findByBno(bno);
			 CommentEntity entity =  cRepo.save(CommentEntity.builder()
									.content(content)
									.writer(writer)
									.bno(board)
									.no(member)
									.build());
			// 저장된 댓글을 dto에 담아서 리턴해줌
			long lastCno = entity.getCno();
			CommentEntity lastComment = cRepo.findByCno(lastCno);
			CommentDTO result = new CommentDTO();
			result.setCno(lastComment.getCno());
			result.setContent(lastComment.getContent());
			result.setCreatedDate(lastComment.getCreatedDate());
			result.setWriter(lastComment.getWriter());
			return result; 
	}
	
	//댓글 가져오는 거 ajax로 따로 처리 이후 댓글 등록하면 댓글 가져오는 ajax 다시 실행하면 댓글 창이 새로고침된다.
	@Override
	public Map<String, Object> findByBno(long bno,int page,Authentication auth) {	
		//가져올 댓글수(페이지당) 5개 가져오기
		int limit = 5;
		//페이지수 * 5 만큼 건너뛰고 가져옴
		int offset = (page-1)*limit;	
		System.out.println(offset);
		BoardEntity board = bRepo.findByBno(bno);
		//총 개시글수 
		int countAll = cRepo.findByBno(board).size();
		//5개씩 끊어서 나온 페이지수
		int pageTotal = countAll/limit;
		//나머지가 있으면 페이지수 +1
		if(countAll%5 != 0)
			pageTotal++;
		//sql쿼리문 파라미터로 쓰기위한 bno추출
		long bnum = board.getBno();
		List<CommentEntity> cEntity = cRepo.findByBnoWithLimit(bnum,limit,offset);
		List<CommentDTO> cResult = cEntity.stream().map((e) -> {CommentDTO dto = new CommentDTO();
										dto.setContent(e.getContent());
										dto.setWriter(e.getWriter());
										dto.setCreatedDate(e.getCreatedDate());
										dto.setCno(e.getCno());
										return dto;
										})
						.collect(Collectors.toList());
		//결과물을 Map에담아서 리턴해줌
		Map<String, Object> result = new HashMap<>();
		result.put("comments",cResult);
		result.put("pageTotal",pageTotal);
		result.put("commentsNum", countAll);
		result.put("auth", auth.getName().toString());
		return result;
	}
	
	//password encoder를 바로 가져와서 인코딩한 뒤 비교할 경우 springsecurity 의
	//bcrypt 알고리즘은 randomsalt incorporation(랜덤 솔트 통함)때문에
	// 같은 값이라도 다른 값으로 인코딩 해버릴 수가있는데
	// 아래의 함수(passwordencoder의 matches()함수를 사용하면 비교가 가능해진다.
	
	//댓글 지우기
	@Override
	public Map<String, Boolean> findByBnoDelete(long bno, String pass, String id,long cno) {
		Optional<MemberEntity> member =  mRepo.findById(id);
		Map<String, Boolean> result = new HashMap<>();
		if(member.isPresent()) {
			String encodedPassword = member.get().getPassword();
			if(PasswordMatchUtils.verifyPassword(encoder,pass , encodedPassword)) {
				cRepo.deleteById(cno);
				result.put("result", true);
			}else {
				result.put("result", false);
			}
		}
		return result;
	}
}
