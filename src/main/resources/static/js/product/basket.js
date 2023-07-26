/**
 * 
 */
$(function() {
	calculateTotal();
	var list = $(".p-list").find(".vno").val();
	
});

//항목 화살표 누를 때
function count(element) {

	//1) 뷰에 반영
	var sum = parseInt($(element).text().trim());
	var target = $(element).parent().siblings(".p-count");
	var count = parseInt(target.text().trim());

	//증가인지 감소인지
	count += sum;
	//유효성검사(수량을 0으로 줄이면 안된다)
	if(count<=0){
		alert("유효한 수량을 설정해 주세요");
		return;
	}

	target.text(count);
	calculateTotal();

	//2) DB에 반영
	var bno = $(element).siblings(".vno").val();
	
	console.log(count);
	console.log(bno);
	
	//csrf
	let token = $("meta[name='_csrf']").attr("content");
	let header = $("meta[name='_csrf_header']").attr("content");
	
	$.ajax({
		beforeSend: function(xhr) { xhr.setRequestHeader(header, token); },
	    url: '/product/basket/change',
	    type: 'POST',
	    data: {count:count,bno:bno}
	});
}

//총 금액 계산.
function calculateTotal() {
	var total = 0;
	$('.list-cal').each(function() {

		var count = parseInt($(this).find('.p-count').text().trim());
		var price = parseInt($(this).find('.p-sprice').text().trim());

		var subTotal = count * price;
		$(this).find('.p-wprice').text(subTotal + ' 원');

		total += subTotal;
	});
	$('.p-total').text('총계: ' + total + ' 원');
	$('.total-pay').val(total);
}

