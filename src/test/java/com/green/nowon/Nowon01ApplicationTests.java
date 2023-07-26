package com.green.nowon;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.annotations.Comment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import com.green.nowon.domain.board.BoardEntity;
import com.green.nowon.domain.board.BoardEntityRepository;
import com.green.nowon.domain.board.CommentEntity;
import com.green.nowon.domain.board.CommentEntityRepository;
import com.green.nowon.domain.info.InfoEntity;
import com.green.nowon.domain.info.InfoPageCategoryRepository;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.domain.member.MemberRole;
import com.green.nowon.domain.member.dto.MemberDTO;
import com.green.nowon.product.domain.CategoryEntity;
import com.green.nowon.product.domain.CategoryRepo;


@SpringBootTest
class Nowon01ApplicationTests {
	
	@Autowired
	MemberEntityRepository repo;
	@Autowired
	PasswordEncoder encoder;
	@Autowired
	private CategoryRepo cateRepo;

	@Autowired
	CommentEntityRepository cRepo;
	@Autowired
	BoardEntityRepository bRepo;
	
	@Autowired
	InfoPageCategoryRepository iRepo;
//	@Transactional
//	@Commit
//	@Test
	void 카테고리넣기() {
//		
//		비밀번호수정 
//		내 게시글 보기
		iRepo.save(InfoEntity.builder()
					.cateName("회원탈퇴")
					.pno(iRepo.findById(2L).get())
					.build());
		
		iRepo.save(InfoEntity.builder()
				.cateName("비밀번호수정").pno(iRepo.findById(2L).get()).build());
		iRepo.save(InfoEntity.builder()
				.cateName("내게시글보기").pno(iRepo.findById(2L).get()).build());
	}
//	@Transactional
//	@Test
	void 리스트테스트() {
		List<MemberEntity> member = repo.findAll();
		List<MemberDTO> result = member.stream()
				.filter( t-> !t.getRole().contains(MemberRole.ADMIN))
				.map(MemberDTO::new)
				.collect(Collectors.toList());
		System.out.println(result.toString());
	}
		
//	@Transactional
//	@Test
	void role가져오기() {
		MemberEntity member = repo.findById("1").get();
		System.out.println(member.getRole());
	}
//	@Transactional
//	@Commit
//	@Test
	void 글쓰기() {
		for(int i = 1 ; i < 100;i++) {
			bRepo.save(BoardEntity.builder()
					.content(i+"번쨰 글 내용")
					.no(repo.findById(1L).get())
					.subject(Integer.toString(i))
					.isShow(true)
					.build());
		}
		
	}
	//@Transactional
	//@Commit
	//@Test
	void 댓글달기() {
		for(int i = 1 ; i < 100;i++) {
			cRepo.save(CommentEntity.builder()
					.bno(bRepo.findByBno(741L))
					.no(repo.findById(1L).get())
					.content(String.valueOf(i)+"번째 댓글")
					.writer("1")
					.build());
		}
		
	}
	
//	@Transactional
//	@Commit
//	@Test
	void 회원가입() {
		for(int i = 1 ; i < 100;i++) {
			repo.save(MemberEntity.builder()
					.id("test"+i)
					.password(encoder.encode("3"))
					.email("test"+i)
					.name("test")
					.build()
					.addRole(MemberRole.USER)
					);
		}
		
	}
	
	//@Test
	void 판매자회원가입() {
		repo.save(MemberEntity.builder()
				.id("saler123")
				.password(encoder.encode("123"))
				.build()
				.addRole(MemberRole.SALER)
				.addRole(MemberRole.USER)
				);
	}

//////////6.5변경. 카테고리 관련 메서드 두개//////////////////
	//@Test
	void 최상위_카테고리_등록() {
		String[] cate = {"식품","가전","가구","남성패션","여성패션"};
		for(String name : cate) {
			//부모가 없는 카테고리
			cateRepo.save(CategoryEntity.builder()
									.name(name)//카테고리 이름
									.build());
		}
	}
	
	//@Test
	void 하위카테고리_생성() {
		String[] parents = {"식품","가전","가구","남성패션","여성패션"};
		
		String[][] cate =  {{"과일","채소","축산물","견과류","커피"},
							{"TV","청소기","냉장고","세탁기","주방가전","생활가전"},
							{"쇼파","침구류","의자","책상","식탁"},
							{"시계","남성의류","남성신발","남성가방","주얼리"},
							{"시계","여성의류","여성신발","여성가방","주얼리"}
							};
		
		for(int j = 0; j < parents.length; j ++) {
			
			CategoryEntity parent = cateRepo.findByName(parents[j]).orElseThrow();
			
			for(int i = 0; i < cate[j].length; i++) {
				cateRepo.save(CategoryEntity.builder()
						.name(cate[j][i])
						.parent(parent)
						.build());
				}
		}
	}
	
	//@Test
	void 세션지우기() {
		MockHttpSession m = new MockHttpSession();
		m.clearAttributes();
	}
	
	//@Test
	void 세션확인() {
		MockHttpSession m = new MockHttpSession();
		System.out.println((String)m.getAttribute("7b2ddf58-8d15-40d2-ab50-f8753d06834f"));
	}
	
	
}





