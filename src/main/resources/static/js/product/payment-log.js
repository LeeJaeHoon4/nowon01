/**
 * 
 */

 $(function(){
	 init();
 })
 
 var smallTotal=0;//소계
 var bigTotal=0;//총계
 
 function init(){
	 $.ajax({
		url:"/product/getLog",
		success: function(data){
			
			var liTags=''
			var ulTags =''
			
			var dtoList = $(data);
			for(var i = 0; i < dtoList.length; i++){
				var dto = dtoList[i];
				
				smallTotal= dto.price*dto.count;//소계
				bigTotal+=smallTotal//총계
				
				liTags+=`<li class="p-list">
								<div class="p-img" style="background-image: url('${dto.url}')"></div>
								<div class="info-wrap">
									<div class="small-wrap">
										<p>상품명: ${dto.title}</p>
										<p>개당가격: ${dto.price} 원</p>
										<p>구매개수: ${dto.count}</p>
									</div>
									<p class="small-total">계: ${smallTotal}원</p>
								</div>
							</li>`
				
				//리스트의 끝이면 : 1) 주문 단위 정보들 입력 2) 초기화
				if(i==dtoList.length-1 || dto.impno!=dtoList[i+1].impno){
					ulTags +=
					`<ul class="order-wrap">
							<div class="p-list-title">
								<p class="p-data">${dto.date}</p>
								<p class="p-num">주문번호 : ${dto.impno}</p>
							</div>							
							${liTags}
							<div class="total-wrap">
								<p class="big-total">총계 : ${bigTotal}원</p>
							</div>
					</ul>`;
					
					//다음 주문을 위해 초기화 
					liTags='';
					bigTotal=0;
				}
				
				//아직 총 결제 금액 안찍음
			}
			$(".ul-wrap").html(ulTags);
		}		 
	 })
 }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 