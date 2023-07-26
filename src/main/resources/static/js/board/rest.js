/**
 * 
 */
$(function(){
	$("#form-search").submit(function(event) {
		event.preventDefault();
	});

		$("#btn-search").click(function() {
		btnSearchClicked(1);
	});
	getBoardList(1);
 });
 
 //처음 페이지 가져올때
 function getBoardList(page){
	     var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
		
	 $.ajax({
		 method:"PATCH",
		 url:"/board/rest-list",
		 data:{page:page},
		 success: function(e){
			 $("#board-list").html(e);
		 }
	 });
 }
 
 function btnSearchClicked(page){
		var data=$("#form-search").serialize()+"&page="+page;
		var token = $("meta[name='_csrf']").attr("content");
		 var header = $("meta[name='_csrf_header']").attr("content");
		 $(document).ajaxSend(function(e, xhr, options) {
			xhr.setRequestHeader(header, token);
		});	
				$.ajax({
					url:"/rest-boards/search",
					type: "PATCH",
					data:data,
					success:function(result){
						$("#board-list").html(result);
					}
				});
			}