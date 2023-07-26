/**
 * 
 */

let cNo = -1;//카테고리 번호/-1은 전체/ + 검색어도 추가
let count = 0;//리스트 수

 $(function() {
	 /* 1. 카테고리 */
	cataLoding();
	/* 가끔 없어지지 않는 카테고리 있다. 어디든 클릭하면 사라짐 */
	$("html").click(function(){
		$("#category .sub").hide();
	});
	
	/* 2. 상품리스트 */
	getProduct(cNo, count);
	
	/* 3. 검색 비동기 임시 리스트 출력 (맨아래) */
	/* 다른 곳 클릭하면 임시 리스트 닫기 */
	$("html").not(".search-wrap").click(function(){
		$("#temp-product-wrap .temp-product-list").remove();
		$("#temp-product-wrap").hide();
	});
})

/////* 1. 카테고리 관련 *////////////////////////////////////
/* 대분류 카테고리 로딩 */
function cataLoding() {
	$.ajax({
		url: "/categories",
		success: function(result) {
			var cateList = $(".cate");
			cateList.each(function(i, e) {
				$(e).find("a").text(result[i].name)
					.attr("value", result[i].cno);
			});
		}
	});
}

/* 대분류에 마우스 올릴 시 : 서브 카테고리 블록 생성 */
function subCategories(atag) {
	
	var size = $(atag).next().find("ol").length;
	// 부모의 pk값
	var parentCno = $(atag).attr("value");
	
	//이미 불렀으면
	if (size > 0) {
		$(atag).next().show();
		
	//처음 불렀으면	
	} else {
		$.get(`/categories/${parentCno}`, function(data) {
			$(atag).next().html(data);
			
			$(atag).parent().mouseleave(function() {
				$(this).children('.sub').hide();
			});
		});
	}
}

/////* 2.상품리스트 관련 *////////////////////////////////////

/* 카테고리 클릭 */
function cateClicked(atag){
	
	//1.원래 있던 리스트들 지우기
	$(".p-li").remove();
	//1-1. '상품이 준비중입니다' 태그도 지우기
	$(".prepare-wrap").remove();
	
	//2.리스트 호출
	//카테고리 번호
	cNo = $(atag).attr("value");
	//리스트 수 : 맨 처음 조회니까 0
	count = 0;
	
	//2.리스트 호출 함수
	getProduct(cNo, count);
}


/* 더보기 클릭 */
function moreButton(btn){
	//리스트 호출만 하면 된다.
	
	//카테고리 번호
	cNo = $(btn).attr("data-cNo");
	//리스트 수
	count = $(".p-li").length;
	
	//리스트 호출 함수
	getProduct(cNo, count);
}


/* 공통 : 리스트 호출 함수  */
function getProduct(cNo, count){
	
	$.ajax({
		url : "/product/getList",
		data : {cNo:cNo,count:count},
		success : function(result){
			
			//1.리스트 가져오기
			$("#p-list .p-wrap").append(result);
			
			//2.더 보기 버튼 존속 여부// 0 : 有 // 1 : 無
			if($(".more-flag").attr("data-more")==0){
				$(".btn-wrap").css('display', 'flex');;
			}else{
				$(".btn-wrap").hide();
			};
			
			
			//3. more-flag의 cNo에 주고 more-flag는 삭제
			$(".more-btn").attr("data-cno", $(".more-flag").attr("data-cNo"));
			$(".more-flag").remove();
			
			//4. 리스트에 번호 메기기
			$(".p-num").each(function(index) {
			    $(this).text((index + 1));
			  });
		}
	});
}



/////////////* 3. 검색 비동기 임시 리스트 출력 *//////////////

function myFunction() {
  // 이벤트 발생 시 실행할 동작을 작성합니다.
  var data = $("#search-box").val();
  data = data.trim();
  
  if(data==""){
	  $("#temp-product-wrap").hide();
	  return;
	 }
  
  console.log("data : "+data);
	$.ajax({
		url: "/product/search",
		data: {data:data},
		success: function(result) {//result : temp-search-list.html
			$("#temp-product-wrap").show();
			$("#temp-product-wrap .temp-product-list").remove();
			$("#temp-product-wrap .temp-product-ul").append(result);
		}
	});
}


/* 3 --> 2 검색 버튼 클릭시 범용 리스트 조회로 */
function searchProducts(){
	//검색어
	var searchKey = $("#search-box").val();
	
	//기능을 추후에 추가하다보니 이런 임시방편적인 방법을 쓰게 되었습니다.
	//카테고리 숫자가 아무리 커봐야 세자릿수 이상이 되기 힘든것을 이용하여
	//검색어 앞에 00000을 붙이고 자바에서 String으로 받아서
	//cNo의 길이가 5이상이면 검색어, 아니면 카테고리번호로 분류해서 리스트를 가져오게 만들었습니다.
	cNo = "00000"+searchKey.trim();
	count = 0;
	
	//1.원래 있던 리스트들 지우기
	$(".p-li").remove();
	//1-1. '상품이 준비중입니다' 태그도 지우기
	$(".prepare-wrap").remove();
	
	getProduct(cNo, count);
}
















