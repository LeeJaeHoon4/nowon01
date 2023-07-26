package com.green.nowon.product.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.websocket.Session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.green.nowon.domain.member.MemberEntity;
import com.green.nowon.domain.member.MemberEntityRepository;
import com.green.nowon.product.domain.BasketEntity;
import com.green.nowon.product.domain.BasketRepo;
import com.green.nowon.product.domain.CategoryEntity;
import com.green.nowon.product.domain.CategoryRepo;
import com.green.nowon.product.domain.ImgRepo;
import com.green.nowon.product.domain.OrderEntity;
import com.green.nowon.product.domain.OrderRepo;
import com.green.nowon.product.domain.ProductEntity;
import com.green.nowon.product.domain.ProductRepo;
import com.green.nowon.product.domain.dto.BasketDTO;
import com.green.nowon.product.domain.dto.CategoryDTO;
import com.green.nowon.product.domain.dto.CategoryWriteDTO;
import com.green.nowon.product.domain.dto.FindDetailDTO;
import com.green.nowon.product.domain.dto.FindNomalDTO;
import com.green.nowon.product.domain.dto.MoreDTO;
import com.green.nowon.product.domain.dto.PaymentLogDTO;
import com.green.nowon.product.domain.dto.ProductDTO;
import com.green.nowon.product.domain.dto.SearchDTO;
import com.green.nowon.product.domain.dto.TempDTO;
import com.green.nowon.product.domain.ImgEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductServiceProcess implements ProductService {
	
	private final AmazonS3Client client;
	private final ProductRepo productRepo;
	private final ImgRepo imgRepo;
	private final CategoryRepo cateRepo;
	private final BasketRepo basketRepo;
	private final MemberEntityRepository memberRepo;
	private final OrderRepo orderRepo;
	
	
	@Value("${cloud.aws.s3.file-upload-bucket}")
	private String bucket;
	
	@Value("${cloud.aws.s3.temp-path}")
	private String tempPath;
	
	@Value("${cloud.aws.s3.use-url}")
	private String userUrl;
	
	@Value("${cloud.aws.s3.product-path}")
	private String productPath;

/////////////임시파일 업로드/////////////
	@Override
	public Map<String, String> tempUpload(MultipartFile tempFile) {
		//1. MultipartFile 데이터를 S#에 업로드
		//2. 업로드된 경로를 페이지(AJAX의 success의 function의 result)로 리턴
		Map<String, String>  map = new HashMap<>();
		String tempKey = tempPath + newFileName(tempFile.getOriginalFilename());
		
		try {
			InputStream is = tempFile.getInputStream();
			
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, tempKey, is, objectMetadata(tempFile));
			client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead));
			
			map.put("s3TempUrl", client.getUrl(bucket, tempKey).toString().substring(6));
			map.put("orgName", tempFile.getOriginalFilename());
			map.put("tempKey", tempKey);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	//이름 설정 메서드. uuid.확장자
	private String newFileName(String orgName) {
		int idx = orgName.lastIndexOf(".");
		return UUID.randomUUID().toString()+orgName.substring(idx);
	}
	
	//ObjectMetadata 설정 메서드
	private ObjectMetadata objectMetadata(MultipartFile mf) {
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(mf.getContentType());
		objectMetadata.setContentLength(mf.getSize());
		
		return objectMetadata; 
	}


/////////////진짜 파일 업로드/////////////
	@Override
	public void saveProduct(Authentication auth, ProductDTO dto) {
		//저장전 전처리
		List<String> newNames=
				fromTempToProduct(dto.getTempKey());
		List<String> orgNames=
				dto.getImgs().stream()
						.filter(org->!org.trim().equals(""))
						.collect(Collectors.toList());
		
		
		//저장 작업
		//상품 정보 저장
		ProductEntity productEntity = ProductEntity.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.price(dto.getPrice())
				.simple(dto.getSimple())
				.category(cateRepo.findById(dto.getSmallcate()).get())
				.member(memberRepo.findById(auth.getName()).get())
				.build();
		
		productRepo.save(productEntity);
		
		//이미지 정보 저장
		for(int i = 0; i < orgNames.size(); i++) {
			Boolean dfYN =false;
			if(i == 0)dfYN = true;
			imgRepo.save(ImgEntity.builder()
					.orgName(orgNames.get(i))
					.newName(newNames.get(i))
					.defYn(dfYN)
					.product(productEntity)
					.build());
		}
	}
	
	//이름설정 메서드. 앞의 'https:' 를 자른다. 하는김에 aws 필수 설정도 하나 한다.
	public List<String> fromTempToProduct(List<String> tempKey){
		return tempKey.stream().filter(key->!key.trim().equals("")) 
								.map(key->{
									int inx=key.lastIndexOf("/");
									//리턴을 위한 새로운 이름
									String newFileName=key.substring(inx+1);
									
									//아래 설정을 위한 변수들. 리턴하지는 않는다.
									String sourceBucketName = bucket;
									String sourceKey = key;
							        String destinationBucketName = bucket;
							        String destinationKey = productPath+newFileName;
							        
							        CopyObjectRequest copyObjectRequest = new CopyObjectRequest(sourceBucketName, sourceKey, destinationBucketName, destinationKey);
									client.copyObject(copyObjectRequest.withCannedAccessControlList(CannedAccessControlList.PublicRead));
									client.deleteObject(sourceBucketName,sourceKey);
								
									return newFileName;
								})
								.collect(Collectors.toList())
								;	
	}

	
	
	
/////////////상품 리스트 상세 조회/////////////
	//상품번호, 상품명, 가격, 상세설명 + 사진 전부
	@Override
	public String showDetail(String productNo, Model model) {
		
		//클릭한 상품 정보 가져오기
		Optional<ProductEntity> productOptional = productRepo.findById(Long.parseLong(productNo));
		if(productOptional.isPresent()) {
			ProductEntity pE = productOptional.get();
			
			//이미지 url 가져오기
			List<ImgEntity> iE = imgRepo.findByProduct(pE);
			List<String> imgUrl = new ArrayList<>();
			
			for(int i = 0; i < iE.size(); i++) {
				imgUrl.add(userUrl+iE.get(i).getNewName());
			}
			
			model.addAttribute("list", FindDetailDTO.builder()
											.no(pE.getPno())
											.title(pE.getTitle())
											.price(pE.getPrice())
											.detail(pE.getContent())
											.imgUrl(imgUrl)
											.build())
											;
			
			return "product/detail";
			
		} else {
			//요청한 상품이 없을때(재고 소진이 아니라 아예 상품을 내렸을 때)
			return "/product/sorry.html";
		}
		
	}


/////////////리스트 페이지 비동기 카테고리 목록 관련////////////
	
/* 대분류 */
@Override
public ResponseEntity<List<CategoryDTO>> listProcess() { 
	
	
	List<CategoryDTO> result = cateRepo.findByParentIsNull().stream()
													.map(CategoryDTO::new)
													.collect(Collectors.toList());
	
	return ResponseEntity.ok().body(result);
}

/* 소분류 */
@Override
public ModelAndView subCategoryProcess(long cno) {
	List<CategoryDTO> result = 	cateRepo.findByParentCno(cno).stream()
									.map(CategoryDTO::new)
									.collect(Collectors.toList())
								;
	
	ModelAndView mv = new ModelAndView();
	mv.setViewName("/product/piece/cate-list");
	mv.addObject("list", result);
	
	return mv;
}


/////////////장바구니 관련/////////////
//회원 장바구니 넣기
//상품 이름, 가격, 대표이미지
@Override
public void saveBasket(int count, Long no, String name) {
	
	Optional<ProductEntity> productEntity = productRepo.findById(no);
	ProductEntity pE = productEntity.get();
	
	ImgEntity iE = imgRepo.findByProductAndDefYn(pE,true);
	
	Optional<BasketEntity> basket =  basketRepo.findByProduct(pE);
	
	//1) 장바구니에 같은 상품이 존재할때 >> 상품 수량 추가
	if(basket.isPresent()) {
		
		BasketEntity bE = basket.get();
		bE.setCount(count+bE.getCount());
		
		basketRepo.save(bE);
		
	//2) 장바구니에 같은 상품이 존재하지 않을 때 >> 새로운 목록 삽입
	} else {

		basketRepo.save(
			BasketEntity.builder()
				.count(count)
				.product(pE)
				.member(memberRepo.findById(name).get())
				.build());
	}
}

//비회원 장바구니 넣기
@Override
public void saveBasket(String userId, int count, Long no, HttpServletResponse response, HttpServletRequest request) {
	
	HttpSession session = request.getSession();
	session.setMaxInactiveInterval(60*60);

	TempDTO dto = TempDTO.builder()
					.no(no)
					.count(count)
					.build();
	
	//쿠키 없으면(저장된 상품이 없다) 쿠키 저장, 상품 저장
	if(userId==null) {
		//쿠키 만들기
		String uuid = UUID.randomUUID().toString();
		Cookie cookie = new Cookie("userId",uuid);
		cookie.setDomain("localhost");
		cookie.setPath("/");
		cookie.setMaxAge(60*60);
		response.addCookie(cookie);
		
		List<TempDTO> dtoList = new ArrayList<>();
		dtoList.add(dto);
		
		session.setAttribute(uuid, dtoList);
	//쿠키가 있으면(상품이 하나라도 있으면)
	} else {
		//쿠키의 아이디 값 삽입
		List<TempDTO> dtoList = (List<TempDTO>)session.getAttribute(userId);
		boolean flag = true;
		
		for(int i = 0; i < dtoList.size(); i++) {
			//같은 상품이 있다면? >> count 그만큼 증가!
			if(dtoList.get(i).getNo()==dto.getNo()) {
				dtoList.get(i).setCount(dtoList.get(i).getCount()+dto.getCount());
				flag = false;
				break;
			}
		}
		
		if(flag) { //같은 상품이 하나도 없을시 통째로 추가 
			dtoList.add(dto);
		}
	}
}

//회원 장바구니 이동 - 장바구니 내역 조회
@Override
public void goBasker(String name, Model model) {
	
	Optional<MemberEntity> memberEntity = memberRepo.findById(name);
	
	if(memberEntity.isPresent()) {
		MemberEntity mE = memberEntity.get();
		Optional<List<BasketEntity>> basketEntity = basketRepo.findByMember(mE);
		
		List<BasketDTO> bDTO = new ArrayList<>();
		
		if(basketEntity.isPresent()) {
			List<BasketEntity> listBasket = basketEntity.get();
			
			for(BasketEntity bE : listBasket){
				
				ProductEntity pE = bE.getProduct();
				ImgEntity iE =imgRepo.findByProductAndDefYn(pE,true);//!!!!!!!!!!!!!!
				
				bDTO.add(BasketDTO.builder()
							.no(bE.getNo())
							.title(pE.getTitle())
							.price(pE.getPrice())
							.imgUrl(userUrl+iE.getNewName())
							.count(bE.getCount())
							.build());
			}
		}
		model.addAttribute("list", bDTO);
	}
}

//비회원 장바구니 이동 - 장바구니 내역 조회
@Override
public void goBasker(String userId, HttpServletResponse response, HttpServletRequest request, Model model) {
	
	//list에 넣을 DTO
	List<BasketDTO> bDTO = new ArrayList<>();
	
	//장바구니에 아무것도 없으면 null로
	if(userId!=null) {
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(60*60);
		
		List<TempDTO> dtoList = (List<TempDTO>)session.getAttribute(userId);
		
		for(TempDTO d : dtoList ) {
			
			ProductEntity pE = productRepo.findById(d.getNo()).get();
			ImgEntity iE = imgRepo.findByProductAndDefYn(pE, true);
			
			bDTO.add(BasketDTO.builder()
					.no(d.getNo())
					.title(pE.getTitle())
					.price(pE.getPrice())
					.imgUrl(userUrl+iE.getNewName())
					.count(d.getCount())
					.build());
		}
	}
	model.addAttribute("list", bDTO);
}


//회원 장바구니 항목 삭제
@Override
public void deleteBasket(Long no) {
	basketRepo.deleteById(no);
	}

//비회원 장바구니 항목 삭제
@Override
public void deleteTempBasket(long no , String userId, HttpServletRequest request) {
	
	HttpSession session = request.getSession();
	session.setMaxInactiveInterval(60*60);
	
	List<TempDTO> dtoList = (List<TempDTO>)session.getAttribute(userId);
	
	for(int i = 0; i < dtoList.size(); i++) {
		TempDTO dto = dtoList.get(i);
		if(dto.getNo()==no) {
			dtoList.remove(i);
			break;
		}
	}
}


//비회원 장바구니 -> 진짜 장바구니 옮기기
@Override
public void moveBasket(List<TempDTO> dtoList, String name) {
	
	//받아온 정보
	//상품 번호+ 수량 리스트, 사용자 아이디
	
	for(TempDTO dto : dtoList) {
		ProductEntity pE = productRepo.findById(dto.getNo()).get();
		Optional<BasketEntity> basket =  basketRepo.findByProduct(pE);
		
		//장바구니에 같은 상품이 존재할때 >> 상품 수량 추가
		if(basket.isPresent()) {
			
			BasketEntity bE = basket.get();
			bE.setCount(dto.getCount()+bE.getCount());
			
			basketRepo.save(bE);
			
		//장바구니에 같은 상품이 존재하지 않을 때 >> 새로운 목록 삽입
		} else {
			
			//1) product 엔티티 : pE
			//2) member 엔티티
			MemberEntity mE = memberRepo.findById(name).get();
			
			basketRepo.save(BasketEntity.builder()
									.count(dto.getCount())
									.product(pE)
									.member(mE)
									.build());
			}
		}
	}


//회원 장바구니 수량변경
@Override
@Transactional
public void changeBasket(long count, long bno) {
	BasketEntity bE = basketRepo.findById(bno).get();
	bE.setCount(count);
}

//비회원 장바구니 수량변경
@Override
public void changeBasket(long count, long bno, List<TempDTO> dtoList, HttpSession session, String userId) {
	
	for(TempDTO dto : dtoList) {
		if(dto.getNo()==bno) {
			dto.setCount((int)count);
		}
	}
	session.setAttribute(userId, dtoList);
	}


//////////추가 서비스///////////////

//write 페이지 select 카테고리 태그
@Override
public List<CategoryWriteDTO> cateWrite() {
	
	List<CategoryEntity> cEList = cateRepo.findAll();
	List<CategoryWriteDTO> cDTOList = new ArrayList<>();

	for (CategoryEntity cE : cEList) {
		CategoryWriteDTO cDTO = new CategoryWriteDTO();
		cDTO.setCno(cE.getCno());
		cDTO.setName(cE.getName());

		if (cE.getParent() != null) {
			cDTO.setParent(cE.getParent());
		}
		cDTOList.add(cDTO);
	}
	return cDTOList;
}

//list페이지 조회 통합 메서드(전체, 카테고리별, 더보기 버튼)
@Override
public ModelAndView getProduct(String cNo, int count, Pageable pageable) {
	//count : 마지막 리스트 번호 = 꺼내진 리스트의 총 개수
		
	List<FindNomalDTO> result = new ArrayList<>();
	
	int more = 0;//0 : 더보기 버튼 있음 // 1: 더보기 버튼 없음
	int page = count/10; // 페이지 번호. 0번부터 센다
	int size = 10; // 페이지 크기
	Sort sort = Sort.by(Sort.Direction.ASC, "pno");//정렬, entity객체 기준 변수명
	
	pageable = PageRequest.of(page, size, sort);
	Page<ProductEntity> pageResult = null;
	
	if(cNo.length()>=5) {
		String realCNo = cNo.substring(5);
		System.out.println(realCNo);
		pageResult = productRepo.findByTitleLike("%"+realCNo+"%",pageable);
		
	} else {
	long longCNo=Integer.parseInt(cNo);
		
	//1)전체 조회
	if(longCNo==-1) {
		pageResult = productRepo.findAll(pageable);
	} else {
		//2-1)대분류 카테고리
		if(cateRepo.findById(longCNo).get().getParent()==null) {
			List<CategoryEntity> cEList = cateRepo.findByParentCno(longCNo);
			pageResult = productRepo.findByCategoryIn(cEList, pageable);
		//2-2)소분류 카테고리	
		}else {
			pageResult = productRepo.findByCategory(cateRepo.findById(longCNo).get(), pageable);
		}
	}
	}
	
	//조건에 따라 ProductEntity리스트를 구했고 뿌리기만 하면 된다
	List<ProductEntity> pEList = pageResult.getContent();
	for(ProductEntity pE : pEList) {
		
		ImgEntity img = imgRepo.findByProductAndDefYn(pE,true);//!!!!!!!!!!!!
		result.add(FindNomalDTO.builder()
						.no(pE.getPno())
						.title(pE.getTitle())
						.simple(pE.getSimple())
						.price(pE.getPrice())
						.imgUrl(userUrl+img.getNewName())
						.build());
	}
	
	//더보기 버튼 관련 : 다음 페이지 유무
	if(!pageResult.hasNext())more = 1;
	
	ModelAndView mv = new ModelAndView();
	mv.setViewName("/product/piece/product-list");
	mv.addObject("list", result);
	mv.addObject("more", MoreDTO.builder()
						.cNo(cNo)
						.more(more)
						.build());//더보기 버튼에 갈 정보. 카테고리 번호, 더 볼 상품의 유무 
	
	return mv;
}

//결제정보 정리
@Override
public void kakao(String imp_uid, String merchant_uid, Principal principal) {
	
	
    String email = principal.getName();
    
    //주문자
    MemberEntity mE = memberRepo.findById(email).get();
    
    //해야할일
    //0. 가져갈 데이터 없음
    //1. OrderEntitiy에 데이터 넣기
    //2. 해당 장바구니 비우기
    
    //1. OrderEntitiy에 데이터 넣기
    List<BasketEntity> bEList = basketRepo.findByMember(mE).get();
    
    for(BasketEntity bE : bEList) {
    	orderRepo.save(OrderEntity.builder()
	    			.count(bE.getCount())
	    			.impno(imp_uid)
	    			.member(bE.getMember())
	    			.product(bE.getProduct())
	    			.build());
    }
    
    //2. 장바구니 지우기
    for(BasketEntity bE : basketRepo.findByMember(mE).get()) {
    	basketRepo.delete(bE);
    }
    //delete : id나 해당 엔티티로 밖에 못한다
    
	}

//주문정보 페이지 조회
@Override
public List<PaymentLogDTO> getLog(Principal principal) {
	
	//날자가 빠른 순으로 가져오기
	List<OrderEntity> oEList=
	orderRepo.findByMemberIdOrderByCreatedDateDesc(principal.getName());
	
	List<PaymentLogDTO> dtoList = new ArrayList<>();
	StringBuilder sb = new StringBuilder();
	
	for(OrderEntity oE : oEList) {
		ProductEntity pE = oE.getProduct();
		char[] cArr = oE.getCreatedDate().toString().toCharArray();
		for(int i = 0; i < 10; i++) {
			sb.append(cArr[i]);
		}
		
		dtoList.add(PaymentLogDTO.builder()
							.pno(pE.getPno())
							.url(userUrl+imgRepo.findByProductAndDefYn(pE,true).getNewName())
							.title(pE.getTitle())
							.price(pE.getPrice())
							.count(oE.getCount())
							.date(sb.toString())
							.impno(oE.getImpno())
							.build());
		sb.setLength(0);
	}
	
	return dtoList;
}

//상품 임시 검색
@Override
public ModelAndView searchProduct(String data) {
	//SearchDTO : 사진, 상품명, pno
	
	//한글 쪼개기
	String slicedData = HangulToJaso.hangulToJaso(data);
	List<SearchDTO> dtoList = new ArrayList<>();
	List<ProductEntity> pEList = productRepo.findAll();
	
	for(ProductEntity pE: pEList) {
		//타이핑 값이 상품에 포함 되어 있으면
		if(HangulToJaso.hangulToJaso(pE.getTitle()).contains(slicedData)) {
			dtoList.add(SearchDTO.builder()
					.pno(pE.getPno())
					.title(pE.getTitle())
					.price(pE.getPrice())
					.imgUrl(userUrl+imgRepo.findByProductAndDefYn(pE,true).getNewName())
					.build());
		//최대 5개만 출력
		if(dtoList.size()>=7)break;
		}
	}
	
	ModelAndView mv = new ModelAndView();
	mv.setViewName("/product/piece/temp-search-list");
	mv.addObject("list", dtoList);
	
	return mv;
	}
}
















