	package com.green.nowon.service.impl;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.ibatis.session.RowBounds;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.BoardEntity;
import com.green.nowon.domain.board.BoardEntityRepository;
import com.green.nowon.domain.board.CommentEntityRepository;
import com.green.nowon.domain.board.dao.BoardMapper;
import com.green.nowon.domain.board.dto.BoardDTO;
import com.green.nowon.domain.board.dto.BoardDetailDTO;
import com.green.nowon.domain.board.dto.BoardListDTO;
import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.domain.board.dto.RestBoardDTO;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.domain.member.MemberRole;
import com.green.nowon.service.BoardService;
import com.green.nowon.util.page.PagingUtils;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@Service
public class BoardServiceImplements implements BoardService{
	
	private final BoardEntityRepository bRepo;
	
	private final MemberEntityRepository mRepo;
	
	private final CommentEntityRepository cRepo;
	//mybatis mapper DI
	private final BoardMapper bMapper;
	//redis 사용을 위한 기본 Template DI
	private final StringRedisTemplate redisTemplate;
	
	//글쓰기
	@Override
	public void save(BoardDTO dto,  Authentication auth) {
		//auth.name << 현재 인증된 회원의 id값 가져옴
		String id = auth.getName();
		//findById(String id)를 사용중 << id값을 가지고 db에 쿼리 날림
		MemberEntity member = mRepo.findById(id).get();
		bRepo.save(BoardEntity.builder()
								.content(dto.getContent())
								.subject(dto.getSubject())
								.isShow(true)
								.no(member)
								.build());
	}
	
	@Override
	public String findAll(Model model) {
		//BoardEntity type으로 받은다음 StreamAPI의 map기능을 사용해 BoardListDTO로 바꿔주는 작업
		List<BoardEntity> board = bRepo.findAllByIsShowTrue();
		List<BoardListDTO> list =  board.stream().map((e) -> {BoardListDTO dto = new BoardListDTO();
																dto.setBno(e.getBno());
																dto.setContent(e.getContent());
																dto.setCreatedDate(e.getCreatedDate());
																MemberEntity member = e.getNo();
																if(member != null) {
																	dto.setWriter(member.getId());
																}
																dto.setSubject(e.getSubject());
																return dto;
																})
					  .collect(Collectors.toList());
		model.addAttribute("list", list);
		return "/board/board";
	}
	
	//member table의 role이 fetchTYPE이 lazy라서 transactional 안 붙혀주면 데이터 못가져옴
	@Transactional
	//게시글 상세 페이지에 뿌려줄 정보를 가져오는 서비스
	@Override
	public String findByBno(long bno, Model model, Authentication auth) {
		String view = "/board/details";
		int reportNum =bRepo.findByBno(bno).getReportCount();
		//admin이상의 권한을 가지고 있거나 신고수가 20미만이면 글 보여줌(admin이상은 무조건 볼 수 있음)
		if(reportNum <20 || auth.getAuthorities().contains(new SimpleGrantedAuthority(MemberRole.ADMIN.roleName()))) {
		//pathVariable을 사용해서 가져온 글의 bno를 바탕으로 dto에 정보를 가져와서  model에 담아서 리턴함
		BoardEntity entity = bRepo.findByBno(bno);
		BoardDetailDTO result = new BoardDetailDTO();
		result.setBno(entity.getBno());
		//위지윅스 쓰면 Xss보안 처리하면 깨져서 나옴 HtmlEscape.unescapeHtml 유틸로 태그 복원해서 살려버림
		//이런방식은 lucy에 내가 설정한 filter와 맞지 않을 경우가 생길수도있음
		//String content = HtmlEscape.unescapeHtml(entity.getContent());
		//이 방식은 위지윅스 사용시 스크립트가 실행됩니다.
		//String origin = entity.getContent();
		//String content = XssPreventer.unescape(origin); 
		//System.out.println(content);
		result.setContent(entity.getContent());
		result.setSubject(entity.getSubject());
		
		//MemberEntity 타입의 member로 fk값을 가지고 있기때문에 받아서 원하는 정보 추출하기
		MemberEntity member = entity.getNo();
		//게시글 수정 버튼의 활성화를 위한 데이터 값 flag설정
		//th:if는 null이면 false를 리턴 하니까 기본 값 null로 설정
		String flag = null;
		if(member != null) {
			 result.setWriter(member.getId());
			 //작성자 = 현재 로그인된 회원 or role 이 admin이면 게시글 수정 삭제 가능
			 if(member.getId().equals(auth.getName())|| member.getRole().contains("ADMIN"))
				 flag = "true";
		}
		result.setCreatedDate(entity.getCreatedDate());
		model.addAttribute("result", result);
		model.addAttribute("flag", flag);
		}else {
			view ="/error/board-error";
		}
		return view ;
	}
	
	//게시글 수정시 
	@Override
	public Map<String, Object> findByBno(long bno,String content,String subject) {
		BoardEntity entity = bRepo.findByBno(bno);
		long bnum= entity.getBno();
		bRepo.updateContent(content,bnum,subject);
		boolean flag = false;
		//수정된 db 칼럼 다시 가져옴
		entity = bRepo.findByBno(bno);
		//수정된 데이터 와 내가 수정한 데이터가 일치하면 flag = true
		if(entity.getContent().equals(content)) {
			flag = true;
		}
		
		Map<String, Object> result = new HashMap<>();
		result.put("flag", flag);
		result.put("subject",subject);
		return result;		
	}
	
	//게시글 삭제
	//db에서는 살아있지만 html에는 표시안함
	@Override
	public void deleteByBno(long bno) {
		BoardEntity entitiy =  bRepo.findByBno(bno);
		entitiy.setIsShowFalse();
		bRepo.save(entitiy);
	}
	

	//비동기 게시판 mybatis의 rowBounds 기능을 활용해서 pagination해서 가져가기
	@Override
	public ModelAndView getListWithPagination(int page) {
		int limit = 10;
		int offset = (page-1)*limit;
		ModelAndView mv = new ModelAndView(); 
		
		mv.setViewName("board/rest-list");
		
		//RowBounds(offset,limit) 형식으로 적으면됨
		RowBounds rowBounds = new RowBounds(offset,limit);
		//RowBounds를 활용 할때는 내장 기능이라 parameter로 limit offset 넘겨줄 필요없이
		//select 쿼리만 넘겨주면 알아서 limit offset 적용해줌
		List<RestBoardDTO> result = bMapper.getListWithPagination(rowBounds);
		//writer 세팅해주기
		result.stream().map((e)->{	long mno = e.getMno();
									String writer = mRepo.findById(mno).get().getId();
											e.setWriter(writer);
											return e;})
						.collect(Collectors.toList());
		mv.addObject("list", result);
		//PagingUtils.create(int page, int limit, int rowCount, int RANGE) 를 활용한  페이지 데이터 생성
		mv.addObject("pu", PagingUtils.create(page, limit,bMapper.countAll() , 10));
		return mv;
	}

	@Override
	public ModelAndView getListWithPagination(BoardSearchDTO dto) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("board/rest-list");
		int page = dto.getPage();	
		int limit = 10;
		int offset = (page-1)*limit;
		RowBounds rowBounds=new RowBounds(offset, limit);
		
		List<RestBoardDTO> result = bMapper.findAllBySearch(dto,rowBounds);
		//writer 세팅해주기
				result.stream().map((e)->{	long mno = e.getMno();
											String writer = mRepo.findById(mno).get().getId();
													e.setWriter(writer);
													return e;})
								.collect(Collectors.toList());
		mv.addObject("list", result);

		mv.addObject("pu", PagingUtils.create(dto.getPage(), limit, bMapper.coutAllBySearch(dto), 10));
		return mv;
	}
	
	//redis를 사용해서 read count 를 조작할거임
	//ip를 확인함
	//bno+ip 를 key로 하고 ip를 value로 함
	@Transactional
	@Override
	public void controlReadCount(String ip, long bno) {
		String key = Long.toString(bno)+ip;
		String value = ip;
		//인증 토큰 만료 시간 1일
		long day = 1L;
		Duration duration = Duration.ofDays(day);
		//검색을 위한 redis 객체
		ValueOperations<String, String> ops = redisTemplate.opsForValue();
		//이미 존재하는 key이면 조회수 증가 x
		if(redisTemplate.hasKey(key)) {
			
		}else {
			//존재 하지 않으면 조회수 증가
			//저장을 위한 redis객체
			ValueOperations<String, String> ops2 = redisTemplate.opsForValue();
			ops2.set(key,value,duration);
			//조회수 증가
			BoardEntity entity =  bRepo.findByBno(bno);
			entity.increaseReadCount();
			bRepo.save(entity);
		}
	}
	
	//글 신고하기 (누적시 admin이상에서 삭제 가능)
	@Transactional
	@Override
	public Map<String, Object> postReport(long bno,String ip) {
		Map<String, Object> response = new HashMap<>();
		//글번호
		String key = Long.toString(bno);
		//기본으로 설정할 누적 신고수 및 신고횟수 1
		String value = Long.toString(1L);
		long day = 1L;
		Duration duration = Duration.ofDays(day);		
		//조회수에서 bno+ip를 사용
		//신고수에서는 ip+report사용
		String reportCount = ip+"report";		
		//사용자의 일일 신고횟수 처리
		try {
		if(redisTemplate.hasKey(reportCount)) {
			//신고 한 사람의 일일 신고 수 기록할 ops
			ValueOperations<String, String> ops = redisTemplate.opsForValue();
			System.out.println(Integer.parseInt(ops.get(reportCount))<=8);
			//신고 횟수는 일일 9개까지
			if(Integer.parseInt(ops.get(reportCount))<=8) {
				ops.increment(reportCount);
				//게시글 신고 수 처리
				if(bRepo.findByBno(bno) != null) {
					BoardEntity board =  bRepo.findByBno(bno);
					board.increaseReportCount();
					int reportNum = board.getReportCount();
					if(reportNum>20) {
						board.setIsShowFalse();
						bRepo.save(board);
					}else {
						bRepo.save(board);
					}
					response.put("status", "성공하였습니다.");
			        response.put("message", "게시글을 신고하였습니다. 누적 신고수 :"+board.getReportCount());
					}
				return response;
			}else {
		        throw  new IllegalStateException("일일 신고 가능횟수는 9회 입니다.");
			}
		}else {
			//일일 신고 횟수가 0(없는경우) redis에 저장
			ValueOperations<String, String> ops = redisTemplate.opsForValue();
			ops.set(reportCount, "9" ,duration);
			//게시글 신고 수 처리
			if(bRepo.findByBno(bno) != null) {
				BoardEntity board =  bRepo.findByBno(bno);
				board.increaseReportCount();
				int reportNum = board.getReportCount();
				if(reportNum>20) {
					board.setIsShowFalse();
					bRepo.save(board);
				}else {
					bRepo.save(board);
				}
				response.put("status", "성공하였습니다.");
		        response.put("message", "게시글을 신고하였습니다. 누적 신고수 :"+board.getReportCount());
				}
			return response;
		}
		}catch(IllegalStateException e) {
			throw e;
		}
	}
}
