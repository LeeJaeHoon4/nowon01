/**
 * 
 */
$(function() {
	 $("#user-id").prop("disabled",true);
	//개인 회원가입 클릭
  $(".switch-wrap #saler-switch .switch-block").click(function() {
    $(".switch-wrap .tr-down").removeClass("tr-down").addClass("tap");
    $(this).addClass("tr-down").removeClass("tap");
    $("#consumer").hide();
    $("#saler").show();
    $("#age").hide();
   
  });
  
  	//사업자 회원가입 클릭
  $(".switch-wrap #consumer-switch .switch-block").click(function() {
    $(".switch-wrap .tr-down").removeClass("tr-down").addClass("tap");
    $(this).addClass("tr-down").removeClass("tap");
    $("#consumer").show();
    $("#saler").hide();
    $("#age").show();
  });
	  
	$("#consumer").on("submit",function(e){
		e.preventDefault();
		var email = $("#user-mail").val().trim();
		// 기입한 아이디 값 가져옴
		const username = $("#user-id").val().trim();
 		// 기입한 password값 가져옴
		const password = $("#user-pass").val().trim();
		// passwordC value 가져오기
		const passwordC = $("#user-passC").val().trim();
		//영문and숫자 조합하여 10자리 이상 15자이하인지 검증하는 정규식
		const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{10,15}$/; // 영문과 숫자 조합, 10자 이상 15자 이하
		//아이디 체크 벨류 가져오기
		const idCheckValue = $("#user-id-check-vlaue").val();
		 // 알파벳 대소문자, 숫자만 허용 하기 위한 정규식
		const usernameRegex = /^[a-zA-Z0-9]+$/;
		
		//개인회원 가입 창에서 id password 칸이 비었을경우
		if(!(password.length>0)
		||!(passwordC.length>0)
		||!(idCheckValue.length>0)
		) {
			Swal.fire({
				icon: 'error',
				title: '아이디 비밀번호는 필수 입력 사항입니다.',
			});
			return;
		}
		//개인회원 가입 창에서 비밀번호가 비밀번호 확인과 일치 하지 않은경우
		else if(password != passwordC){
			Swal.fire({
				icon: 'error',
				title: '비밀번호가 비밀번호 확인과 일치 하지 않습니다.',
			});	
			return;	
		}
		
		/*//개인회원 가입 창에서 id 중복검사를 하지 않았을 경우
		//$("#user-id-check-vlaue").val()가 "false"임
		if(!idCheckValue=="true"){
			alert("아이디 중복 체크를 해주세요");
			return;
		}*/
		
		 //정규식을 사용한 test기능을 이용하여 검증
		 if (!usernameRegex.test(username)) {
    			alert("유효한 사용자 이름을 입력하세요. 특수문자는 사용할 수 없습니다.");
    			//유효하지 않다면 함수 종료
    			return;
 			 }
		//정규식을 통한 password겁능
		if (!passwordRegex.test(password)) {
			Swal.fire({
				icon: 'error',
				title: '비밀 번호는 영문과 숫자 조합으로 10자 이상 15자 이하이어야 합니다.',
			});
			return;
		}
		//email이 중복인지 알아봐야함 
//		var isExist =  false//emailCheck(email);
//		if(isExist){
//			alert("유효하지 않은 이메일 입니다.");
//			return;
//		}
		//이메일 인증 완료 됐는지 알아보기
		if($("#consumer-check-value").attr("value")==="false"){
			Swal.fire({
				icon: 'error',
				title: '이메일 인증이 완료 되지 않았습니다.',
			});
			return;
		}else{
			//태그 프로퍼티가 disabled이면 값 못가져옴
			$("#user-mail").prop("disabled",false);
		}
		$("#user-id").prop("disabled",false);
		 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		 
		 var formData = $("#consumer").serialize(); 
		 var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $("#user-id").prop("disabled",false);
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		$.ajax({
			method:"POST",
			url:"/login/signup/consumer",
			data:formData,	
			success:function(response){
				console.log(response)
				window.location.href=response;
			}		
		});
		
	});//개인 회원 가입 로직 끝
	
	$("#saler").on("submit",function(e){
		// 기입한 아이디 값 가져옴
		const slaerName = $("#saler-id").val().trim();
 		// 기입한 password값 가져옴
		const slaerPassword = $("#saler-pass").val().trim();
		// passwordC value 가져오기
		const slaerPasswordC = $("#saler-passC").val().trim();
		//아이디 체크 벨류 가져오기
		const slaerIdCheckValue = $("#saler-id-check-vlaue").val();
		//판매자 가입 창에서 id password 칸이 비었을경우  
		if(!slaerPasswordC.length>0
		||!slaerPassword.length>0
		||!slaerName.length>0){
			e.preventDefault();
			alert("아이디 비밀번호는 필수 입력 항목입니다.");
		}
		//판매자 가입 창에서 id 중복검사를 하지 않았을 경우
		//$("#user-id-check-vlaue").val()가 "false"임
		if(!slaerIdCheckValue=="true"){
			e.preventDefault();
			alert("아이디 중복 체크를 해주세요");
		}
		//판매자 가입 창에서 비밀번호가 비밀번호 확인과 일치 하지 않은경우
		else if(slaerPassword!=slaerPasswordC){
			e.preventDefault();
			alert("비밀번호와 비밀번호 확인이 일치 하지 않습니다.");
		}
		//정규식을 사용한 test기능을 이용하여 검증
		 if (!usernameRegex.test(slaerName)) {
				e.preventDefault();
    			alert("유효한 사용자 이름을 입력하세요. 특수문자는 사용할 수 없습니다.");
    			//유효하지 않다면 함수 종료
    			return;
 			 }
		//정규식을 통한 password겁능
		if (!passwordRegex.test(slaerPassword)) {
			e.preventDefault();
			alert("유효한 비밀번호를 입력하세요. 영문과 숫자 조합으로 10자 이상 15자 이하이어야 합니다.");
			//유효하지 않다면 함수 종료
			return;
		}
	});//판매자 아이디 가입 로직 끝
	
	
	//개인 회원 아이디 중복 체크 버튼 눌렀을때 수행 되는 ajax로직
	$("#user-id-check-btn").on("click", function(){	
		
		Swal.fire({
			title: '사용하실 아이디를 입력해주세요',
			input: 'text',
			inputAttributes: {
				autocapitalize: 'off'
			},
			showCancelButton: true,
			confirmButtonText: '아이디 중복 검사',
			cancelButtonText:'취소',
			showLoaderOnConfirm: true,
			backdrop:true,
  			allowOutsideClick:()=>!Swal.isLoading()
}).then((result) => {
	if(result.isConfirmed){
		var regex = /^[a-zA-Z0-9]{6,10}$/;
		var userid = result.value;
		var flag = regex.test(userid);
		if(flag){
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		$.ajax({
			type: "POST",
			url: "/idCheck",
			data: {id : userid},
			//성공시 동작함수
			success: function(response) {
				if (response) {
					Swal.fire({
						title: result.value+'는 사용 가능합니다.',
						text: "가입 후에는 아이디를 바꿀수 없습니다. 사용 하시겠습니까?",
						icon: 'info',
						showCancelButton: true,
						confirmButtonColor: '#3085d6',
						cancelButtonColor: '#d33',
						confirmButtonText: '사용하기',
						cancelButtonText: '취소',
						reverseButtons: true, // 버튼 순서 거꾸로
					}).then((result) => {
						if (result.isConfirmed) {
							const Toast = Swal.mixin({
								toast: true,
								position: 'center-center',
								showConfirmButton: false,
								timer: 2000,
								timerProgressBar: true,
							})
							Toast.fire({
								icon: 'info',
								title: '사용하신 아이디는 중복검사 버튼을 통해 바꿀수 있습니다.',
							})
							$("#user-id").prop("disabled",false);
							$("#user-id").val(userid);
							$("#user-id").prop("disabled",true);
						}
						
					})
				} else {
					Swal.fire({
						icon: 'error',
						title: '이미 회원 가입이 완료되거나 탈퇴한 회원 입니다.'
					}).then((result)=>{
						$("#user-id-check-btn").click();
					})
				}
			}
		});
		}else{
			Swal.fire({
				icon: 'error',
				title: '양식에 맞지 않는 아이디 입니다.',
			}).then((result)=>{
						$("#user-id-check-btn").click();
					});			
		}
	}
})

/*		if (regex.test(username)){
		var data = $("#user-id").val();
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		$.ajax({
			type: "POST",
			url: "/idCheck",
			data: {id : data},
			//성공시 동작함수
			success: function(result) {
				if (result) {
					//중복입니다 메세지 제거 이후 사용가능 메세지 
					$("#user-id-not").css("display", "none");
					$("#user-id-not-regex").css("display", "none");
					$("#user-id-ok").css("display", "inline-block");
					$("#user-id-check-value").val("true");
				} else {
					//사용가능 메세지 제거 이후 중복입니다 메세지
					$("#user-id-ok").css("display", "none");
					$("#user-id-not-regex").css("display", "none");
					$("#user-id-not").css("display", "inline-block");
				}
			}
		});
		}else{
			Swal.fire({
				icon: 'error',
				title: '아이디가 유효하지 않습니다.',
				text: '아이디는 6글자이상 10글자 이상 영어 + 숫자로 작성해야합니다.',
			});
		}*/
		
	});//개인회원 아이디 중복체크 끝
	
//판매자 아이디 중복 체크 버튼 눌렀을때 수행 되는 ajax로직
	$("#saler-id-check-btn").on("click", function(){
		console.log(username);
		var regex = /^[a-zA-Z0-9]{6,10}$/;
		if (regex.test(username)){
		var data = $("#user-id").val();
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
		$.ajax({
			type: "POST",
			url: "/idCheck",
			data: {id : data},
			//성공시 동작함수
			success: function(result) {
				if (result) {
					//중복입니다 메세지 제거 이후 사용가능 메세지 
					$("#user-id-not-regex").css("display", "none");
					$("#slaer-id-not").css("display", "none");
					$("#saler-id-ok").css("display", "inline-block");					
					$("#saler-id-check-value").val("true");
				} else {
					//사용가능 메세지 제거 이후 중복입니다 메세지
					$("#user-id-not-regex").css("display", "none");
					$("#saler-id-ok").css("display", "none");	
					$("#slaer-id-not").css("display", "inline-block");
				}
			}
		});
		}		
	});//판매자 아이디 중복 체크 끝
	
	//사용자 회원가입 인증받기 클릭
	//$("#consumer-send").on("click",consumerSend);
	$("#consumer-send").on("click",makeQrImg);
	//인증번호 확인
	$("#consumer-check").on("click",consumerCheck);
});
//$(function(){})끝

////인증 이메일 보내기
//function consumerSend(){
//	//입력된 이름 
//	var name =$("#user-name").val().trim();
//	//입력된 이메일
//	var address = $("#user-mail").val().trim();
//	
//	var emailRegex = /^[\w\.-]+@[\w-]+(\.\w{2,})+$/;
//	
//	var nameRegex = /^[가-힣]{2,4}$/;
//	
//	if(!emailRegex.test(address)){
//		alert("이메일 형식이 옳바르지 않습니다.")
//		return;
//	}else if(!nameRegex.test(name)){
//			alert("이름은 한글 2~4자만 가능합니다.");
//			return;
//		}
//	
//	var url = $("#consumer-send").attr("data-url");
//	var data = {name: name,address:address};
//	$.ajax({
//		url,
//		method:"POST",
//		data:data,
//		success:function(e){
//			$("#user-mail").prop("disabled",true);
//			//qr이미지를 변수에 담음
//			var qrImg= e.qrImg;
//			//QR코드를 표시해줄 li에 이미지와 텍스트 세팅
//			$("#QrImg").remove();
//			$("#QRCode-li").append(`
//			<img id="QrImg" src='data:image/png;base64,${qrImg}'>
//			`);
//			$("#QRCode-li").css("display","block");
//
//
//		},
//		error:function(){
//			alert("이미 회원가입이 완료된 이메일 이거나 시도 횟수를 초과했습니다. 잠시뒤 다시 시도해주세요.");
//		}
//	});
//}

function makeQrImg(){
	//입력된 이메일
	var address = $("#user-mail").val().trim();
	//메소드에 사용할 url
	var url = $("#consumer-send").attr("data-url");
	//입력된 이름 
	var name =$("#user-name").val().trim();
	// url에 address를 파라미터로 추가해줌
  	urlforQr = url + '?address=' + encodeURIComponent(address);
  	//fethch는 기본적으로getMapping을 사용함
  	//email 보내기 ajax는 postMapping을 사용할 예정
	fetch(urlforQr)
	.then(response => response.text())
	.then(qrCodeData =>{
		//jquery로 하면 안먹힘
		var qrCodeContainer = document.getElementById('QRCode-div');
		//qr코드 만들기전에 기존 qr코드 이미지 지우기(qr코드 데이터 15글자 error는 그거보다 김)
		if(qrCodeData.length<=15){
			qrCodeContainer.innerHTML = '';
		//qr코드 이미지를 qrcodjs라이브러리로 만들어서 붙힘
		var qrCodeImg =  new QRCode(qrCodeContainer,{
			text:qrCodeData,
			width:200,
			height:200
		});
		
			//QR코드 이미지 가운데 정렬
      		qrCodeContainer.style.display = 'flex';
     		qrCodeContainer.style.justifyContent = 'center';
    		qrCodeContainer.style.alignItems = 'center';
			$("#user-mail").prop("disabled",true);
			$("#QRCode-li").css("display","block");
			
			Swal.fire({
				icon: 'success',
				title: 'QR이미지를 촬영하거나 이메일을 통해 인증번호를 입력해주세요',
			});
			
			//qr코드를 성공적으로 만들었을때만 callback 함수로 이메일을 보내도록 설정했음
			afterQrSendEmail(url,address,name);
		} else {
			var Toast = Swal.mixin({
				toast: true,
				position: 'center-center',
				showConfirmButton: false,
				timer: 3000,
				timerProgressBar: true,
				didOpen: (toast) => {
					toast.addEventListener('mouseenter', Swal.stopTimer)
					toast.addEventListener('mouseleave', Swal.resumeTimer)
				}
			})

    Toast.fire({
      icon: 'error',
      title: '인증 횟수가 초과되었습니다.',
      text: '잠시뒤 다시 시도하시거나'+
      		'지막 인증 번호를 사용하시면 인증이 가능합니다.'
    })
			}
	});
}
function afterQrSendEmail(url,address,name){
	var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
	//이메일 처리할 ajax
	$.ajax({
		method:"POST",
		url:url,
		data:{address:address,name:name},
		success:function(){
			
		},
		error(e){
			var Toast = Swal.mixin({
				toast: true,
				position: 'center-center',
				showConfirmButton: false,
				timer: 3000,
				timerProgressBar: true,
				didOpen: (toast) => {
					toast.addEventListener('mouseenter', Swal.stopTimer)
					toast.addEventListener('mouseleave', Swal.resumeTimer)
				}
			})

    Toast.fire({
      icon: 'error',
      title: '인증 횟수가 초과되었습니다.',
      text: '잠시뒤 다시 시도하시거나'+
      		'지막 인증 번호를 사용하시면 인증이 가능합니다.'
    })
		}
	});	
}
//이메일 인증 처리 로직
function consumerCheck(){
	//입력한 인증번호
	var key = $("#user-phn").val().trim(); 
	//입력된 이메일주소
	var address = $("#user-mail").val().trim();
	console.log(key);
	console.log(address);
	var url = $("#consumer-check").attr("data-url");
	var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			 xhr.setRequestHeader(header, token);
		 });
	$.ajax({
		method:"POST",
		url:url,
		data:{key:key, address:address},
		success:function(flag){
			if(flag.result =="인증에 성공하였습니다."){
				Swal.fire({
				icon: 'success',
				title: '인증에 성공하였습니다.',
			}).then(after=>{
				$("#consumer-check-value").attr("value","true");
			})
			}else{
				Swal.fire({
				icon: 'error',
				title: '인증에 실패 하였습니다.'
			})
			}
			
		}
	});
}

//이메일 중복인지 알아보기
//promise 객체를 써서 응담을 기다리게함
// ajax부분에 async:false써도되긴함 그러면 동기처럼 진행후 결과값 리턴받고 아래부분 진행
/*function emailCheck(e){
	var email = e;
	return new Promise(function(resolve, reject){
		$.ajax({
			url:"/login/signup/email-check",
			data:{email : email},
			success: function(result){
				resolve(result);
			},
			error: function(result){
				reject(result);
			}
		});
	});
}*/
 //emailSender로 보낼 랜덤 10자리 (숫자+영어) 생성하는 함수
//사용 하지 않음 java 서비스 단에서 생성하도록 하였음
//function generateRandomString() {
//  var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
//  var randomString = "";
//
//  for (var i = 0; i < 10; i++) {
//    var randomIndex = Math.floor(Math.random() * chars.length);
//    randomString += chars.charAt(randomIndex);
//  }
//
//  return randomString;
//}

 
 //			//qr코드를 팝업창으로 띄우려 했으나 ux를 고려하여 인터넷 창에 바로 띄우기로함
//			var qrWindow  = window.open('',"_blank","width=595px, height=580px");
//			
//			//HTML컨텐츠 작성
//			qrWindow.document.open();
//			qrWindow.document.write(`
//  <html>
//    <head>
//      <title>QR Code Image</title>
//      <style>
//        body {
//          font-family: Arial, sans-serif;
//          text-align: center;
//          margin: 0;
//          padding: 20px;
//          background-color: #ffe812;
//        }
//        
//        h2 {
//          color: #333333;
//        }
//        
//        img {
//          max-width: 100%;
//          height: auto;
//          margin-top: 20px;
//          background-color: #ffffff;
//          border: 1px solid #000000;
//        }
//      </style>
//    </head>
//    <body>
//      <h2>QR코드를 촬영 하시면 인증 번호가 나타납니다.</h2>
//      <img src="data:image/png;base64,${qrImg}">
//    </body>
//  </html>
//`);