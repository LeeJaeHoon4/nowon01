$(function(){
	const content = $("#summernote").text();
	console.log(content);
	var a = $('<div></div>');
	a.html;
	a.html(content);
	console.log(content);
	$('#summernote').html(a.text());
	//ajax 에러 났을시 처리하기위한 범용 메소드 로딩
	// 이후에 발생하는 aja요청등은 이 error처리 메소드를 상속해서 사용함
	$.ajaxSetup({
		error: function(xhr, status, error) {
			console.error('AJAX Request Error:', error);
			// 오류를 중앙에서 처리하는 로직 
			widnow.href="/error/page";
		}
	});
	//수정하기 버튼 클릭시 처리
	$("#change-post-btn").on("click",changePostBtnClicked);
	//수정하기 버튼 클릭시 나오는 창에서 수정 취소 버튼 클릭
	$("#post-change-cancel").on("click",postChangeCancleClicked);
	//글 수정 완료후 저장버튼 누를시 처리
	$("#post-change-save").on("click",postChangeSaveClicked);
	//글 삭제 
	$("#post-delete-btn").on("click",postDeleteBtnClicked);
	//글 신고
	$("#report-btn").on("click",postReportClicked);
	
});
//글 신고 하기
function postReportClicked(){
	var url = window.location.href;
	var parts = url.split("/");
	var bno = parts[parts.length - 1];	
	console.log(url);
	$.ajax({
		method:"get",
		url:"/board/"+bno+"/report",
		success:function(response){
			if(response.status == "성공하였습니다."){
				Swal.fire({
						title: '게시글을 신고시겠습니까?',
						text: "계정당 일일 신고 횟수는 9회 입니다.",
						icon: 'info',
						showCancelButton: true,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: '신고하기',
						cancelButtonText: '취소',
						reverseButtons: true, // 버튼 순서 거꾸로
					}).then((result) => {
						if (result.isConfirmed) {
							Swal.fire({
						title: '게시글을 신고했습니다.',
						text: "누적 신고 및 관리자의 재량에 따라 게시글이 삭제됩니다.",
						icon: 'success',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						confirmButtonText: '확인',
					}).then((result)=>{
						window.location.href="/board/rest-main";
					});
						}
						
					})
			}
		},
		error:function(response){
			Swal.fire({
						title: response.message,
						text: "계정당 일일 신고 횟수는 9회 입니다.",
						icon: 'error',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: '확인',
					})
		}
	});
}
//수정하기 버튼 클릭시 처리
function changePostBtnClicked(){
	$("#summernote-li").css("display","none");
	$("#summernote2-li").css("display","block");
	$("#subject").css("display","none");
}

//수정하기 버튼 클릭시 나오는 창에서 수정 취소 버튼 클릭
function postChangeCancleClicked(){
	$("#summernote-li").css("display","block");
	$("#summernote2-li").css("display","none");
	$("#subjcet").css("display","block");
}

//글 수정 완료후 저장버튼 누를시 처리
function postChangeSaveClicked(){
	//java컨트롤러에 넘길 수정된 컨텐츠
	var data = $("#summernote2").val();
	//수정된 제목
	var subject = $("#changeSubject").val();
	//수정하지 않았으면 원제목 그대로
	if(subject == ""){
		subject = $("#subject").text();
	}
	$.ajax({
			method:"PUT",
			url:"/board/postChange/"+bno,
			data:{content:data,subject:subject},
			success:function(e){
				if(e.flag){
					$('#summernote').summernote('code', data);
					$("#summernote-li").css("display","block");
					$("#summernote2-li").css("display","none");
					$("#subject").text(e.subject);
				}
			}
	});
}

//삭제처리
function postDeleteBtnClicked(){
	$.ajax({
		method:"DELETE",
		url:"/board/delete/"+bno,
		success:function(e){
					Swal.fire({
						title: '게시글을 삭제합니다.',
						text: "삭제 후에는 관리자를 통해서만 복구가 가능합니다.",
						icon: 'info',
						showCancelButton: true,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: '삭제하기',
						cancelButtonText: '취소',
						reverseButtons: true, // 버튼 순서 거꾸로
					}).then((result) => {
						if (result.isConfirmed) {
							Swal.fire({
						title: '게시글을 삭제했습니다.',
						text: "삭제 후에는 관리자를 통해서만 복구가 가능합니다.",
						icon: 'success',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						confirmButtonText: '확인',
					}).then((response)=>{
						window.location.href=e;
					});
						}
						
					})
		}	
	});
}