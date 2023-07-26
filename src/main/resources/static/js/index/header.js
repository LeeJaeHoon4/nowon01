/**
 * 
 */

 $(function(){
	 console.log("로딩완료");
	 $(".header-nav-login").click(headerNavLoginClicked);
	 
	 $("#header-logout-btn").on("click",function(e){
		 e.preventDefault();
	Swal.fire({
      title: '정말로 로그아웃 하시겠습니까?',
      text: "소셜 회원인 경우 장바구니에 담은 내용이 없어질 수 있습니다.",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: '로그아웃',
      cancelButtonText: '취소',
      reverseButtons: true, // 버튼 순서 거꾸로
      
    }).then((result) => {
      if (result.isConfirmed) {
      var Toast = Swal.mixin({
      toast: true,
      position: 'center',
      showConfirmButton: false,
      timer: 2000,
      timerProgressBar: true,
      didOpen: (toast) => {
        toast.addEventListener('mouseenter', Swal.stopTimer)
        toast.addEventListener('mouseleave', Swal.resumeTimer)
      }
    })

    Toast.fire({
      icon: 'success',
      title: '잠시뒤 로그아웃이 됩니다.'
    }).then((response)=>{
		console.log(response);
		 $("#header-logout-form").submit();
	})	
			 }
    })
	 });
 });
 
 
 //로그인 버튼 눌렀을때 새 팝업 창으로 로그인창을 띄움
 function headerNavLoginClicked(e){
	//기본동작을 막는함수
	e.preventDefault();
	var url = $(this).data("url");
	window.open(url,"_blank","width=595px, height=580px");
 }
 