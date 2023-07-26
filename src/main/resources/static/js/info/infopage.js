/**
 * 
 */

$(function() {
	$(".infopage-menu").prop("disabled",true);
	$("#sub").hide();
	$("#infoControl-btn").trigger("click");
	$("#side-menu ul>li").mouseover(function() {
		$("#sub").show();
	});
	$("#side-menu ul>li").mouseleave(function() {
		$("#sub").hide();
	});
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	$(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
	$.ajax({
		url:"/infopage/welcome",
		method:"PATCH",
		success:function(e){
		 $("#target").html(e);
		}		});
	//게시글 검색 
	$("#info-board-search-btn").on("click",infoBoardSearch);
});
//회원 탈퇴
function userDeleteBtnClicked(){	
	var isChecked1 = $('#flexCheckDefault').attr("value");
	var isChecked2 = $('#flexCheckChecked').attr("value");
	if(isChecked1=="true" && isChecked2=="true"){
		Swal.fire({
			title: '정말로 탈퇴 하시겠습니까?',
			showCancelButton: true,
			confirmButtonText: '탈퇴하기',
			cancelButtonText:'취소',
			showLoaderOnConfirm: true,
			backdrop:true,
  			allowOutsideClick:()=>!Swal.isLoading()
}).then((result)=>{
	if(result.isConfirmed){
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});
		$.ajax({
		method:"DELETE",
		url:"/infopage/userDelete",
		success:function(response){
			Swal.fire({
						title: '회원 탈퇴가 완료되었습니다.',
						text: "메인 페이지로 이동합니다.",
						icon: 'info',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						confirmButtonText: '확인',
						reverseButtons: true, // 버튼 순서 거꾸로
					}).then((result)=>{
						window.location.href="/";
					})
			},
		error:function(error){
			console.log(error);
		}
	});
	}
})
	}else{
		Swal.fire({
						icon: 'error',
						title: '모든 항목에 체크하여 동의해 주세요'
					})
	}
}
//비밀 번호 변경하기
function ChangeBtnClicked(){
	var password = $("#password").val().trim();
	var newPassword = $("#newPassword").val().trim();
	var newPasswordC = $("#newPasswordC").val().trim();
	if(password == newPassword || password == newPasswordC){
		Swal.fire({
						icon: 'error',
						title: '변경하려는 비밀번호가 유효하지 않거나 현재 비밀번호와 같습니다.'
					})
		return;
	}
	var passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{10,15}$/; // 영문과 숫자 조합, 10자 이상 15자 이하
	if(passwordRegex.test(newPassword) && newPassword == newPasswordC ){
		Swal.fire({
			title: '현재 입력하신 새로운 비밀번호로 변경 하시겠습니까?',
			showCancelButton: true,
			confirmButtonText: '변경하기',
			cancelButtonText:'취소',
			showLoaderOnConfirm: true,
			backdrop:true,
  			allowOutsideClick:()=>!Swal.isLoading()
}).then((result)=>{
	if(result.isConfirmed){
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});
		$.ajax({
		method:"POST",
		url:"/infopage/passChange",
		data: {password:password,newPassword : newPassword, newPasswordC:newPasswordC},
		success:function(response){
			Swal.fire({
						title: '비밀번호가 변경 되었습니다.',
						text: "메인 페이지로 이동합니다.",
						icon: 'info',
						showCancelButton: false,
						confirmButtonColor: '#3085d6',
						confirmButtonText: '확인',
						reverseButtons: true, // 버튼 순서 거꾸로
					}).then((result)=>{
						if(result.isConfirmed){
							window.location.href="/";
						}
					})
			},
		error:function(error){
			console.log(error);
		}
	});
	}
})
	}else{
		Swal.fire({
						icon: 'error',
						title: '비밀 번호가 양식에 맞지 않습니다.(6~10 영문 + 숫자) 또는 새로 변경할 비밀번호가 일치 하지 않습니다.'
					})
	}
	
}
//게시글 검색 
function infoBoardSearch(){
	console.log("검색버튼")
		var page = 1
		var data=$("#form-search").serialize()+"&page="+page;
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
				$.ajax({
					url:"/infopage/boardSearch",
					type: "PATCH",
					data:data,
					success:function(result){
						$("#info-board-list-target").html(result);
					}
				});
}

//비밀번호 체크
 function welcomePassClicked(){
	Swal.fire({
      title: '회원님 환영합니다',
      text: '비밀번호를 입력해주세요',
      input: 'password',
      inputAttributes: {
        autocapitalize: 'off'
      },
      showCancelButton: true,
      confirmButtonText: '확인',
      cancelButtonText:'취소',
      showLoaderOnConfirm: true,
      backdrop:true,
      allowOutsideClick:()=>!Swal.isLoading()
 }).then((result)=>{
	 if(result.isConfirmed){
		 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 var password = result.value;
		 fetch("/infopage/welcome/passCheck",{
			 method: "POST",
			 headers:{
				'Content-Type':'application/json',
				'X-CSRF-TOKEN': token,
    			'X-CSRF-HEADER': header
			 },
			 body:JSON.stringify({password:password})
		 }).then(response=> response.json())
		 .then(data => {
			 if(data.flag =="true"){
				 const Toast = Swal.mixin({
					 toast: true,
					 position: 'center-center',
					 showConfirmButton: false,
					 timer: 1000,
					 timerProgressBar: true
				 })
				 Toast.fire({
					 icon: 'success',
					 title: '인증이 완료되었습니다.',
				 }).then((result)=>{
					 if(data.auth != "user"){
						 boardControl(1);
					 }else{
						 infoControl();
					 } 
				 })
				 $(".infopage-menu").prop("disabled",false);
			 }else{
				 Swal.fire({
					 icon: 'error',
					 title: '비밀번호가 유효하지 않습니다.',
					 text: '비밀번호 찾기를 통해 비밀번호를 찾거나 다시시도해 주십시오'
				 }).then((result)=>{
					 welcomePassClicked();
				 } );
			 }
		 });
	 }
 })
 }//비번 sweetAlert2
 
 //공백모두제거하기
 function replaceSpace(str){
	 var result = str.replace(/\s/g, "");
	 return result;
 }
 //관리자 권한 부여 하기
 function authControllPage(page){
	  var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
	 $.ajax({
		 method:"PATCH",
		 url:"/info/auth/authControll",
		 success:function(e){
			 $("#target").html(e);
			 $("#infopage-title").html("<h2>관리자 권한 부여 페이지입니다</h2>");
		 }		 
	 });
 }
 //신고 누적 관리물 페이지 
 function boardControl(page){
	  var token = $("meta[name='_csrf']").attr("content");
	  var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
	 $.ajax({
		 method:"PATCH",
		 url:"/info/board/boardControl",
		 data:{page : page},
		 success:function(e){
			 $("#target").html(e);
			 $("#infopage-title").html("<h2>관리자 권한 부여 페이지입니다</h2>");
		 }
	 });
 }
 
  //게시글 복구
 function authBoardBtnClicked(){
	var checkBox = document.getElementsByName("options");
	var selectedCheckBox = [];
	console.log(checkBox.length);
	for(var i = 0; i < checkBox.length;i++){
		if(checkBox[i].checked)
		selectedCheckBox.push(checkBox[i].value);
	}
	var data = { bno :selectedCheckBox };
	console.log(selectedCheckBox);
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	fetch("/info/auth/boardRePost",{
		method:"POST",
		headers:{
		'content-type':'application/json',
		'X-CSRF-HEADER': header,
		'X-CSRF-TOKEN': token
		},
		body:JSON.stringify(data)
	})
	.then(response=>{
			 $("#target").html(response);
			 boardControl(1);
	}
	);
}
//회원정보 수정을 위한 페이지
function infoControl(){
	
//	//button의 텍스트 가져오기
//	if(e != null){
//		var cate = e.innerHTML;
//		//db에 저장된 이름과 일치하도록 공백 모두제거
//		cate =replaceSpace(cate);
//	}
	
	 $.ajax({
		 method:"PATCH",
		 url:"/info/board/infoControl",
		 success:function(e){
			 $("#target").html(e);
			 $("#infopage-title").html("<h2>회원 정보</h2>");
//			 //정보관리 페이지 넘어오면 하위 카테고리 가지고와서 세팅
//			 $.ajax({
//				 method:"PATCH",
//				 url:"/infopage/cate",
//				 data:{cate:cate},
//				 success:function(response){
//					 $("#sub").html(response);
//				 }
//			 });
		 },
		 error:function(error){
			 console.log(error);
		 }
	 });
}

 
