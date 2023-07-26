/**
 * 
 */

 $(function(){
	 //회원가입 버튼 눌렀을때 작동하는 함수
	 $("#login-signup-btn").click(function loginSignUpClicked() {
		 console.log(":123");
	Swal.fire({
      title: '회원가입을 위해 페이지를 이동 하시겠습니까??',
      text: "더 많은 혜택을 위해 회원 가입을 해보세요",
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '회원 가입 하러가기',
      cancelButtonText: '취소',
      reverseButtons: true, // 버튼 순서 거꾸로
    }).then((result)=>{
		if(result.isConfirmed){
		 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		 	 $.ajax({
			 url:"/emart/signup",
			 type:"post",
			 data: {data: true},
			 error:function(){
				 alert("에이잭스 실패");
			 },
			 success:function(result){
				//부모창에 회원가입 창뜨도록 설정
				window.opener.location.href = result;
				//현재 창 닫기
				window.close(); 
			 }
		 });
		}else{
			Swal.fire(
          '페이지 이동이 취소되었습니다',
          '회원 가입을 하시면 더 많은 혜택을 누리실 수 있습니다.'
        )}	
	 });
	  });
	  
	$("#login-login-btn").on("click",function loginFormClicked(){
				//ajax와 springSecurity를 사용하여 로그인 구현
		//$(".login-login-form").serialize() 를 사용하여 정보를 서버에 보냄
//		var formData = $(".login-login-form").serialize();
//		var token = $("meta[name='_csrf']").attr("content");
//		var header = $("meta[name='_csrf_header']").attr("content");
//		$(document).ajaxSend(function(e, xhr, options) {
//			xhr.setRequestHeader(header, token);
//		});	
//		$.ajax({
//			url: "/login",
//			type: "POST",
//			data: formData,
//			dataType: "json",
//			success: function(response) {
//				console.log(response.result)
//				if(response && response.result){
//					//success handler에서 응답
//					alert("성공");
//					loginSuccess();
//				}else{
//					alert("실패");
//					window.location.href=response.failureURL;
//				}
//				
//			},
//			error: function(xhr) {
//				// 오류 처리
//				console.log(xhr.responseText);
//			}
//		});
		var form = document.getElementsByClassName("login-login-form")[0];
		var formData = new FormData(form);
		fetch(form.action, {
			method: form.method,
			body: formData
		})
			.then(function(response) {
				if (response.ok) {
					return response.json();
				} else {
					throw new Error("Error: " + response.status);
				}
			})
			.then(function(data) {
				// Handle the login response data
				if(data.result =="true"){
					loginSuccess();
					// Redirect to a success page or perform further actions
				}else{
					Swal.fire({
						icon: 'error',
						title: '로그인실패',
						text: '유효한 아이디가 아니거나 비밀번호가 틀렸습니다.',
					}).then(response=>{
						window.location.href=data.faliureURL;
					});
					
				}
			})
			.catch(function(error) {
				// Handle any errors that occurred during the login process
				alert(error);
			});
});
/*		var formData = new FormData(document.querySelector('.login-login-form'));
		var token = document.querySelector("meta[name='_csrf']").getAttribute("content");
		var header = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

		fetch("/login", {
			method: "POST",
			headers: {
				'X-CSRF-TOKEN': token,
				'X-CSRF-HEADER': header,
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(Object.fromEntries(formData))
			}).then(response => {
				response.json();
			}).then(data => {
				// Handle the response data
				loginSuccess();
			}).catch(error => {
				// Handle the error
				console.log(error);
			});*/
			
}); //readyfunction 끝

//ajax성공이후 login성공처리 fetch async await 적용을 위해 비동기 함수로 변경
function loginSuccess() {
    window.opener.location.href = "/"; // 부모 창 새로고침
    window.close(); // 현재 창 닫기
}

 /*
 //로그인창에서 회원가입 버튼 눌렀을때 회원 가입창을 새로운 탭에 여는 기능
 //ajax를 사용하였으며 postMapping 방식으로 앞으로의 재사용성을 고려해
 // 받는 responseBody에서 태그의 text값을 받게 되는데 이게 회원가입<< 일 경우
 //회원가입 페이지를 연다.
 function loginSignUpClicked(){
	//아래 공백제거 함수를 통해 공백제거
	var data = spaceDelete($(this).text());

	 $.ajax({
			 url:"/emart/signup",
			 type:"post",
			 data: {data: data},
			 error:function(){
				 alert("에이잭스 실패");
			 },
			 success:function(result){
				//현재 창 닫기
				window.close(); 
				// 새로운 창에 result값으로 매핑  방식 지정안했으므로 get방식
				console.log(result);
				window.opener.location.href = "/login/signup";
			 }
		 });
}

//text양옆과 중간의 공백을 제거하는 정규식
function spaceDelete(str){
	str = str.replace(/^\s+|\s+$/g, '').replace(/\s+/g, '');
	return str;
}

//로그인 버튼이 눌렸을때 실행되는 함수
//login-err-div의 visibility나 text 컨트롤
function loginBtnClicked(){
	var id = $('input[name="login-id"]').val();
	var password = $('input[name="login-password"]').val();
	
	if(id.trim != "" && password.trim() != ""){
		$(".login-err-div").text("로그인 성공");
		$(".login-err-div").css("color","green");
		$(".login-err-div").css("visibility","visible");
	}else{
		$(".login-err-div").css("visibility","visible");
	}
	}
	
	*/