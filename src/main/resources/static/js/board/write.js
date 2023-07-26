/**
 * 
 */

$(function() {
	$("#board-form").on("submit", function(e) {
		e.preventDefault();
		var formData = $("#board-form").serialize();
		var token = $("meta[name='_csrf']").attr("content");
		var header = $("meta[name='_csrf_header']").attr("content");
		//입력된 제목
		var title = $("#title-input").val();
		//공백제거
		const titleWithoutSpaces = title.trim();
		//공백 제거후 제목이 2글자 이상인지 검사
		if (titleWithoutSpaces.length >= 2) {
		$(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});
		var url = "/board/save";
		$.ajax({
			url: url,
			type: "POST",
			data: formData,
			error: function(e) {
				console.log(e)
				Swal.fire({
				icon: 'error',
				title: '제목과 내용은 필수 요소입니다.(공백만은 사용하실수 없습니다.)',
			});
			},
			success: function(e) {
					Swal.fire({
				icon: 'success',
				title: '글이 저장되었습니다.',
			}).then((result)=>{
				window.location.href = e;
			});				
			}
		});
		} else {
			Swal.fire({
				icon: 'error',
				title: '제목은 2글자이상(공백제외)이어야 합니다.',
			});	
		}
	})
});