// 현재 페이지의 URL을 가져옵니다.
const currentUrl = window.location.href;
// URL에서 숫자 값을 추출합니다.
const bno = currentUrl.substring(currentUrl.lastIndexOf('/') + 1); 
//같은 페이지 내에서 js를 로딩하여 details.js에서도 사용 가능함
$(function(){
	//페이지 로딩후 ajax로 댓글 가져옴
	getComments();
	$(".comments-pagination-btn").hide();
	$("#comment-submit-btn").on("click",commentSave);
	$("#close-comments").on("click",function(){
		$("#comments-div").hide();
		$("#comments-page-div").hide();
		$("#open-comments").css("display","inline-block");
		$("#close-comments").css("display","none");
	});
	
	$("#open-comments").on("click",function(){
		$("#comments-div").show();
		$("#comments-page-div").show();
		$("#close-comments").css("display","inline-block");
		$("#open-comments").css("display","none");
	});
	
	$("#open-comments").css("display","none");
	//댓글 마지막으로 이동
	$("#comments-page-to-last").on("click",function(){
		var commentsTotal = $("#commentsNum").text();
		var pageTotal;
		if(commentsTotal%5 != 0){
			pageTotal = (commentsTotal-(commentsTotal%5))/5+1;
		}else{
			pageTotal = commentsTotal/5;
		}
		//기존에 가져왔던 댓글들 지움
		$(".comments").remove();
		//기존 댓글 페이지 네이션 정보 지움
		$(".commentsPageSpan").remove();
		getComments(pageTotal);
	});
	//댓글 처음으로 이동
	$("#comments-page-to-first").on("click",function(){
		//기존에 가져왔던 댓글들 지움
		$(".comments").remove();
		//기존 댓글 페이지 네이션 정보 지움
		$(".commentsPageSpan").remove();
		getComments();
	});
	//뒤로 block칸
	$("#comments-page-next").on("click",function(){
		var currentPage = $(".commentsPage").eq(0).val();
		var block = 10;
		var commentsTotal = $("#commentsNum").text();
		var pageTotal;
		if(commentsTotal%5 != 0){
			pageTotal = (commentsTotal-(commentsTotal%5))/5+1;
		}else{
			pageTotal = commentsTotal/5;
		}
		var targetPage = parseInt(currentPage) + parseInt(block);
		//기존에 가져왔던 댓글들 지움
		$(".comments").remove();
		//기존 댓글 페이지 네이션 정보 지움
		$(".commentsPageSpan").remove();
		if(targetPage > pageTotal){
			getComments(pageTotal);
		}else if(targetPage<=pageTotal){
			getComments(targetPage);
		}
	});
	
	//앞으로 N칸
	$("#comments-page-prev").on("click",function(){
		//한번에 표시되는 페이지 네이션 갯수
		var paginationLength = $(".commentsPage").length;
		var currentPage = $(".commentsPage").eq(paginationLength-1).val();
		var block = 10;
		var commentsTotal = $("#commentsNum").text();
		var pageTotal;
		if(commentsTotal%5 != 0){
			pageTotal = (commentsTotal-(commentsTotal%5))/5+1;
		}else{
			pageTotal = commentsTotal/5;
		}
		var targetPage = parseInt(currentPage) - parseInt(block);
		//기존에 가져왔던 댓글들 지움
		$(".comments").remove();
		//기존 댓글 페이지 네이션 정보 지움
		$(".commentsPageSpan").remove();
		if(targetPage >=1){
			getComments(targetPage);
		}else if(targetPage<1){
			getComments();
		}
	});
});

//댓글 삭제버튼 누르면 나오는 비밀번호 입력 창 
function commentDelete(e){
		$(".delpw_box").css("display","none");
		const target = e.parentNode;
		const cno = e.parentNode.parentNode.getAttribute("value");
		console.log(cno);
		target.insertAdjacentHTML('afterend', delpwBox(cno));
}

//비밀번호 입력 받는 창 띄우기
//cno조회 편의를 위해서 창의 value에 cno넣어줌
function delpwBox(e){
	return`<div class="delpw_box" value="${e}">
				<input type="password" id="delpw_input" placeholder="비밀번호">
				<button type="button" id="delpw_btn" onclick="delpwBtnClicked(this)"}>확인</button>
				<button type="button" id="delpw_close_btn" onclick="delpwCloseBtnClicked(this)">X</button>
			</div>`;
}
//댓글 지우기 비밀번호 입령창 닫기
function delpwCloseBtnClicked(){
	$(".delpw_box").css("display","none");
}

//비밀 번호 입력 받아서 일치하면 지우기
function delpwBtnClicked(e){
	const page = $("#comments-page-num").val();
	console.log(page);
	const cno = e.parentNode.getAttribute("value");
	console.log(cno);
	const pw = $("#delpw_input").val();
	console.log(pw);
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
		xhr.setRequestHeader(header, token);
	});	
	$.ajax({
			url:"/board/delete",
			method:"DELETE",
			data:{pw : pw, bno: bno, cno:cno},
			success: function(e){
				console.log("결과:"+e.result);
				if(!e.result){
					Swal.fire({
						title: '비밀번호가 다릅니다.',
						text:'삭제실패',
						icon: 'error'
					}
					)
				}else{
					Swal.fire({
						title: '댓글이 삭제 되었습니다.',
						icon: 'success'
					}
					)
					//현재 눌린 버튼의 value 값 가져옴
					var i = page;
					$("#comments-page-num").val(i);
					i++;
					//기존 댓글 지움
					$(".comments").remove();
					//기존 댓글 페이지 네이션 정보 지움
					$(".commentsPageSpan").remove();
					//댓글 가져와서 뿌리기 
					getComments(i);
					
				}
			}		
	});
}
//페이지 넘버링 div에 들어갈 내용 생성해서 붙히기
function addPageNumber(i){
		var pNum = i+1;
		return `
			<li class="page-item commentsPageSpan">
      			<button class="page-link commentsPage" value="${i}" onclick="commentsBtnClick(this)">
        			${pNum}
      			</button>
   			 </li>
			`;
}

//댓글 페이지 이동
function commentsBtnClick(button){
		//현재 눌린 버튼의 value 값 가져옴
		var i = button.value;
		$("#comments-page-num").val(i);
		i++;
		//기존 댓글 지움
		$(".comments").remove();
		//기존 댓글 페이지 네이션 정보 지움
		$(".commentsPageSpan").remove();
		//댓글 가져와서 뿌리기 
		getComments(i);
}

//댓글 가져와서 뿌리기
function getComments(e){
	 var pageNumber=e;
	 var data = {bno : bno ,page: pageNumber};
	 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
	$.ajax({
		type:"POST",
		url:"/board/getComments",
		data: data,
		success:function(e){
			var count = 0;
			for(var i = 0; i < e.comments.length;i++){
				var target = $("#comments-div");
				target.after().append(addNewComment(e.comments[i],count,e.auth));
				count++;
			}
			//댓글 갯수표시  
			$("#commentsNum").text(e.commentsNum);
			
			//총 페이지수 2보다 작으면 페이징 실행 x
			if(e.pageTotal<2){
				return;
			}
			
			//comment paging 표시 단위 
			var block = 10; 
			//comment paging 시작 넘버
			var from = 0; 
			//pageNumber <= 누른 댓글 페이지 번호
			if(pageNumber != null){
				// 6번 이후 즉 7번 누르면 2~11까지 표시되게 하고싶음
				from = pageNumber<=(block/2)? 0 : (from+pageNumber-(block/2));
			}
			//comment paging 마지막 넘버 
			var to = from + block;
			//마지막 표시 페이지 넘버를  총 페이지 넘버로 고정
			//표시 시작 넘버를 총 페이지 넘버 0으로 고정 (0이어야 1부터 시작함)
			if(e.pageTotal<=10){
				to = e.pageTotal;
				from = 0;
			}else if(to>=e.pageTotal){
				from =e.pageTotal-10;
				to = e.pageTotal;
			}
			
			var pageTarget = $("#comments-page-prev");
			if(from == 0 || to == e.pageTotal ){
				for(var i = to-1; i >=from ; i--){
				pageTarget.after(addPageNumber(i));
				}
			}
			else{
				for(var i = to-1; i >=from ; i--){
				pageTarget.after(addPageNumber(i));
				}
			}	
				
				if(from == 0 && to != e.pageTotal){
					$(".prev-btn").hide();
					$(".next-btn").show();

				}else if(from >0 && to<e.pageTotal){
					$(".next-btn").show();
					$(".prev-btn").show();
				}else if( to == e.pageTotal  && from != 0){
					$(".prev-btn").show();
					$(".next-btn").hide();
				}
			
		}
		
	});

}





//댓글 저장하고 다시 불러오는 기능
function commentSave(){
	//입력한 댓글
	const comment = $("#comment-input").val().trim();
	 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
	$.ajax({
		type:"POST",
		url: "/board/comment",
		data:{content: comment, bno : bno},
		success: function(result){
			//댓글 입력창 비우기
			$("#comment-input").val("");
			if(result){
				//기존에 가져왔던 댓글들 지움
				$(".comments").remove();
				//기존 댓글 페이지 네이션 정보 지움
				$(".commentsPageSpan").remove();
				//새로 댓글을 가져와서 뿌림
				getComments();
			}else{
				alret("ajax 실패");
			}
		}
	});	
}

//댓글 가져와서 동적 태그 생성해서 넣기
function addNewComment(e,count,auth){
	        //현재 로그인 해있는 사람 아이디
	        const authName = auth;
			//오늘 날짜 가져옴
			var date = new Date(); 
			//댓글 작성 날짜 가져옴
			var cDate = new Date(e.createdDate);
			//2개를 비교
			if(date.getFullYear() == cDate.getFullYear() 
				&& date.getMonth() == cDate.getMonth()
				&& date.getDate() == cDate.getDate()){
				//글 작성일과 댓글 작성일이 같다면 시간만 표시
				//toLocalDate()등의 기능을 사용 할수 없음
				var hours = cDate.getHours();
 				var minutes = cDate.getMinutes();
  				formattedDate= (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes);
			}else{
				//글 작성일과 댓글 작성일이 다르면 날짜만 표시
				var month = cDate.getMonth()+1;
 				var day = cDate.getDate();
				formattedDate =(month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
			}
	return `
					<ul class="flex between comments" value="${e.cno}" id="${count+1}">
							<li>${e.writer}</li>
							<li>${e.content}</li>
							<li>
								${formattedDate}
								${e.writer === authName ?'<button type="button" class="comment-delete" onclick="commentDelete(this)">X</button>' : ''}
							</li> 
					</ul>
			`;
}

