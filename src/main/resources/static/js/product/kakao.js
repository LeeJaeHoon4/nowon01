/**
 * 
 */
/* 기본 가이드 https://developers.portone.io/docs/ko/auth/guide/3 */
/* 결제요청/응답 파라미터 https://developers.portone.io/docs/ko/sdk/javascript-sdk/payrt */

var vnos = [];
var IMP = window.IMP; 

$(function(){
	IMP.init("imp51256644"); //imp67011510
	
	$(".list-cal").each(function() {
//	  vnos = $(this).find(".vno").val();
	  vnos.push($(this).find(".vno").val());
	});
	console.log(vnos[2]);
})


var today = new Date();   
var hours = today.getHours();// 시
var minutes = today.getMinutes(); // 분
var seconds = today.getSeconds(); // 초
var milliseconds = today.getMilliseconds();
var makeMerchantUid = hours +  minutes + seconds + milliseconds;
    

function requestPay() {
	
	//실제 결제하는 정보
    IMP.request_pay({
//        pg : 'kcp',
        pg : 'kakaopay',
        pay_method : 'card',
        merchant_uid: "IMP"+makeMerchantUid,//주문 번호. 중복되면 결제 불가
        
        //아래의 정보들은 장바구니 전체에 대한정보. 각각에 대한 정보는 따로 담자
        name : $(".total-pay").val(),//상품명 : '맨처음상품이름'외 N개
        amount :  $(".total-pay").val(), //가격
        
        //구매자 정보 샘플
        buyer_email : 'Iamport@chai.finance',
        buyer_name : '아임포트 기술지원팀',
        buyer_tel : '010-1234-5678',
        buyer_addr : '서울특별시 강남구 삼성동',
        buyer_postcode : '123-456',


        //결제시도 후 반환됨. 결제 성공여부에 따라 로직을 달리해야한다 
    }, function (rsp) { // callback
    	
    //1.결제 성공.
    	//1.1 : 정보를 서버로 옮기고 정보를 처리한 뒤 DB까지 정리(실전에서는 결제정보 검증까지 해야함)
    	//1.2 : 페이지 이동. 가져올 정보는 가져와서 화면에 띄울 수 있음
    	 if (rsp.success) {
    		console.log("여기까지옴");

			//csrf
			let token = $("meta[name='_csrf']").attr("content");
			let header = $("meta[name='_csrf_header']").attr("content");
    		 
    	      // axios로 HTTP 요청
    	      axios({
				//이 부분은 결제 정보를 확인하는 곳이다.
				//소비자 입장에서 결제는 성공적을 했으나 
				//들어온 결제정보가 맞는지(해킹이나 오류가 없었는지) 서버 입장에서 정보를 확인하는것이다(아마도)
    	        beforeSend: function(xhr) { xhr.setRequestHeader(header, token); },
    	        url: "/product/none",
    	        method: "post",
    	        headers: { "Content-Type": "application/json" },
    	        data: {
    	          imp_uid: rsp.imp_uid, //결제번호
    	          merchant_uid: rsp.merchant_uid
    	        }
    	      }).then((data) => {
    	        // 서버 결제 API 성공시 로직
    	        // rsp.imp_uid, rsp.merchant_uid를 /product/kakao으로 get요청
    	        var url = "/product/kakao?imp_uid=" + rsp.imp_uid + "&merchant_uid=" + rsp.merchant_uid;
 				window.location.href = url;
    	       
    	      })
    //2.결제 실패 : 페이지 이동. 가져올 정보는 가져와서 화면에 띄울 수 있음
    	    } else {
    	      alert(`결제에 실패하였습니다. 에러 내용: ${rsp.error_msg}`);
    	      //장바구니로 새로고침
    	      window.location.href = "/product/basket";
    	    }
    	  });
}
