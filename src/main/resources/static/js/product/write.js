/**
 * 
 */
$(function(){
	  $('#summernote').summernote();
	
	/*2.카테고리 select 태그 관련*/
	//2-1. 처음
	  init();
	//2-2. select 클릭시
	  $(document).on('change', 'select[name=bigcate]', function() {
	    const classVal = $(this).val();
	    $('select[name=smallcate] option').each(function(idx, item) {
	      if ($(this).data('class') == classVal || $(this).val() == '') {
	        $(this).show();
	      } else {
	        $(this).hide();
	      }
	    });
	    $('select[name=smallcate]').val('');
	  })
	  
	});
	
	/* 1. 파일 임시 업로드 */        	
	function tempUpload(el){ 
		var target = $(el);
		
		var formData = new FormData();
		var file = target[0].files[0]; 
		formData.append("tempFile",file);
		formData.append("tempKey",target.siblings(".temp-key").val());
		
		//csrf
		let token = $("meta[name='_csrf']").attr("content");
		let header = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({
			beforeSend: function(xhr) { xhr.setRequestHeader(header, token); },
			url:"/product/temp-upload", 
			type:"Post", 
			data: formData,
			contentType: false,
			processData: false,
			
			success:function(map){
				
			var url = map.s3TempUrl;
			target.siblings("label").css("background-image", `url("${url}")`);
			target.siblings(".temp-key").val(map.tempKey);
			addTag(target);
			}
		})
	}
	/* 1. 파일 임시 업로드 : 추가 태그 생성 */  
	function addTag(target){
		var i = $("#temp-imgs-wrap :file").length;
		if(i==5 || target.parent().index()<i-1){return;}
		var spanTag=`<span>
						<label for="img-${i}" class="img-area">+</label>
						<input type="file" id="img-${i}" name="imgs" onchange="tempUpload(this)">
						<input type="hidden" class="temp-key" name="tempKey">
					</span>`;
		
		$("#temp-imgs-wrap>span").append(spanTag);
	}
	
	/* 유효성검사 */
	function checkOk(){
		
		var flag = $("#title").val().trim();

		var msg;
		if(flag == ""){msg="상품명을 입력해주세요";$("#msg").text(msg); return false;}
		flag = $("#price").val();
		if(flag.trim() == "" || flag.trim()<=0){msg="유효한 가격을 입력해주세요";$("#msg").text(msg);return false;}
		flag = $("#simple").val();
		if(flag.trim() == ""){msg="간단한 설명을 입력해주세요";$("#msg").text(msg);return false;}
		flag=$(".s-cate").val();
		if(flag==-1){msg="카테고리를 설정해주세요.";$("#msg").text(msg);return false;}
		flag = $("#summernote").val();
		if(flag.trim() == ""){msg="상품 설명을 입력해주세요";$("#msg").text(msg);return false;}
		var fileLength = $("#temp-imgs-wrap :file")[0].files.length;
		if(fileLength==0){msg="파일은 최소 1개는 등록되어야합니다.";$("#msg").text(msg);return false;}

		return true;
	}
 
 
// select 태그 항목 생성
function init() {

	$.ajax({
		url: "/categories/select",
		success: function(result) {

			let dtoList = $(result);

			let superCate = '<option value="-1">선택하세요.</option>';
			let subCate = '<option value="-1">선택하세요.</option>';
			
			console.log(dtoList[0].cno);
			
			
			for (var i = 0; i < dtoList.length; i++) {
				
				console.log(dtoList[i].cno);
				
				if (dtoList[i].parent == null) {//부모 카테고리면
					superCate += `<option value="${dtoList[i].cno}">${dtoList[i].name}</option>`;
				} else {//자식 카테고리면
					subCate += `<option value="${dtoList[i].cno}" data-class="${dtoList[i].parent.cno}">${dtoList[i].name}</option>`;
				}
			}
			
			$('select[name=bigcate]').html(superCate);
			$('select[name=smallcate]').html(subCate);

			$('select[name=smallcate] option').each(function(idx, item) {
				if ($(this).val() == '') return true;
				$(this).hide();
			});

		}
	})
}    	