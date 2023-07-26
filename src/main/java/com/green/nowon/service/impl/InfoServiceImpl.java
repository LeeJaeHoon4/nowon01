package com.green.nowon.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.green.nowon.domain.board.BoardEntity;
import com.green.nowon.domain.board.BoardEntityRepository;
import com.green.nowon.domain.board.dao.BoardMapper;
import com.green.nowon.domain.board.dto.BoardControlDTO;
import com.green.nowon.domain.board.dto.BoardSearchDTO;
import com.green.nowon.domain.board.dto.RestBoardDTO;
import com.green.nowon.domain.board.dto.authControlDTO;
import com.green.nowon.domain.info.CateDTO;
import com.green.nowon.domain.info.InfoEntity;
import com.green.nowon.domain.info.InfoPageCategoryRepository;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.domain.member.MemberRole;
import com.green.nowon.domain.member.dao.MemberMapper;
import com.green.nowon.domain.member.dto.PassChangeDTO;
import com.green.nowon.service.InfoService;
import com.green.nowon.util.page.PagingUtils;
import com.green.nowon.util.passwordMatcher.PasswordMatchUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class InfoServiceImpl implements InfoService {
	
	private final MemberMapper mMapper;
	
	private final MemberEntityRepository mRepo;
	
	private final BoardEntityRepository bRepo;
	
	private final BoardMapper bMapper;
	
	private final InfoPageCategoryRepository iRepo;
	
	private final PasswordEncoder encoder;
	//관리자 부여를 위한 유효성 검사 
	//없는 이메일 -> nullpointException Throw
	//이미 관리자 -> alert띄우기
	@Transactional
	@Override
	public Map<String, String> authMailCheck(String email) {
		Map<String, String> result = new HashMap<>();
		Optional<MemberEntity> member = mRepo.findByEmail(email);
		if(!member.isPresent()) {
			 throw new NullPointerException("유효한 이메일이 아닙니다.  email: " + email);
		}else{
			MemberEntity entity = member.get();
			if(entity.getRole().contains(MemberRole.ADMIN)) {
				result.put("result", "이미 관리자인 회원입니다.");
				return result;
			}else {
				result.put("result","관리자 부여를 위해 list로 이동 시킵니다.");
				return result;
			}
		}
		
	}
	
	//관리자 권한 승인
	//role사용할거니까 일단 붙혀놓자
	@Transactional
	@Override
	public void adminGranted(authControlDTO dto) {
		for(int i =0; i< dto.getEmail().size();i++) {
		 Optional<MemberEntity> member = mRepo.findByEmail(dto.getEmail().get(i));
		 MemberEntity result = member.get();
		 result.addRole(MemberRole.ADMIN);
		}
	}
	
	//게시글 삭제 페이지 페이지네이션으로 가져가기
	@Override
	public ModelAndView getReportBoardWithPagination(BoardSearchDTO dto) {
		ModelAndView mv = new ModelAndView();
		//정보가 도착할 페이지
		mv.setViewName("/infopage/layout/master-admin-board-layout");
		
		int page= dto.getPage();
		int limit = 10;
		int offset = (page-1)*limit;
		//mybatis RowBounds쓸거임
		RowBounds rowBounds = new RowBounds(offset,limit);
		List<RestBoardDTO> result = bMapper.getListWithReportCount(dto,rowBounds);
		result.stream().map((e) -> {	
										long mno = e.getMno();
										String writer = mRepo.findById(mno).get().getId();
										e.setWriter(writer);
										return e;
									})
						.collect(Collectors.toList());
		mv.addObject("list", result);
		mv.addObject("pu", PagingUtils.create(page, limit, bMapper.countAllByReportCount(), 10));
		
		return mv;
	}
	
	//선택된 게시글 복원 처리
	@Transactional
	@Override
	public void boardReportControl(BoardControlDTO dto) {
		System.out.println(dto.toString());
		for(int i = 0; i < dto.getBno().size();i++) {
			long bno = Long.parseLong(dto.getBno().get(i));
			BoardEntity board = bRepo.findByBno(bno);
			board.setIsShowTrue();
			board.setReportCount(0);
			bRepo.save(board);
		}
	}
	
	//내정보관리 눌렀을때 나오는 탭 메뉴 가져오기
	@Override
	public ModelAndView findByPno(String cate) {
		ModelAndView mv = new ModelAndView();
		//최상위 메뉴 -> ino 가져오기 pno가 ino인거모두찾기 할예정
		Optional<InfoEntity> cateName = iRepo.findByCateName(cate);
		if(cateName.isPresent()) {
			List<InfoEntity> entity = iRepo.findListByPno(cateName.get());
			List<CateDTO> result = entity.stream().map((e)->{
				CateDTO dto = new CateDTO();
				dto.setCateName(e.getCateName());
				return dto;
			}).collect(Collectors.toList());
			mv.addObject("cate", result);
		}
		mv.setViewName("infopage/layout/cate");
		return mv;
	}
	
	//비번 체크
	@Transactional
	@Override
	public Map<String,String> welcomePassCheck(String password, Authentication authentication) {
		Map<String,String> model = new HashMap<>();

		String username = authentication.getName();
		Optional<MemberEntity> entity = mRepo.findById(username);
		if(entity.isPresent()) {
			String pass = entity.get().getPassword();
			Set<MemberRole> auth =entity.get().getRole();
			if(auth.contains(MemberRole.USER) && !auth.contains(MemberRole.ADMIN))  {
				model.put("auth", "user");
			}
			if(PasswordMatchUtils.verifyPassword(encoder, password, pass)) {
				model.put("flag", "true");
			}else {
				model.put("flag", "false");
			}
		}
		
		return model;
	}

	@Override
	public ModelAndView infoBoardSearchWithPagination(BoardSearchDTO dto) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("infopage/layout/info-board-list");
		int page = dto.getPage();	
		int limit = 10;
		int offset = (page-1)*limit;
		RowBounds rowBounds=new RowBounds(offset, limit);
		
		List<RestBoardDTO> result = bMapper.findAllBySearchInfoBoard(dto,rowBounds);
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
	
	//비밀번호 변경
	@Override
	public boolean userPassChange(PassChangeDTO dto, Authentication authentication) {
		boolean flag = false;
		String username = authentication.getName();
		Optional<MemberEntity> entity = mRepo.findById(username);
		if(entity.isPresent()) {
			String pass = entity.get().getPassword();	
			flag = PasswordMatchUtils.verifyPassword(encoder, dto.getPassword(),pass);
			MemberEntity member = entity.get().changePassword(encoder.encode(dto.getNewPassword()));
			mRepo.save(member);
		}
		return flag;
	}
	
	@Autowired
	HttpServletRequest request;
	@Autowired
	HttpServletResponse response;
	//회원 탈퇴
	@Override
	public void userDelete(Authentication authentication) {
		String username = authentication.getName();
		Optional<MemberEntity> entity = mRepo.findById(username);
		if(entity.isPresent()) {	
			MemberEntity member = entity.get();
			mRepo.delete(member);
		}
		
		 SecurityContextHolder.clearContext();
	        HttpSession session = request.getSession(false);
	        if (session != null) {
	            session.invalidate();
	        }
	}

}
