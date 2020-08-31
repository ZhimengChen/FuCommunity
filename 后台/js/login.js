$(function(){
	$inputs=$('input');
	$btn=$('button');
	$userName=$(':text');
	$pwd=$(':password');
	
	$inputs.bind("mouseleave",openBtn);
	$btn.bind("click",login);
	
	function openBtn(){
		let flag=false;
		
		$inputs.each(function(index,element){
			let value=element.value.trim();
			if(value===""){
				flag=true;
				return false;
			}
		});
		
		$btn.attr("disabled",flag);
	}
	
	function login(){	
		$.ajax({
			type: "POST",
			url: "http://www.chenzhimeng.top/fu-community/admin",
			data: {
				adminName:$userName.val(),
				password:hex_md5($pwd.val())
			},
			dataType: "JSON",
			success : (data)=>{
				if(data.result){
					localStorage.setItem("accessToken",data.accessToken);
					location.href="index.html"
				}else{
					alert(data.msg);
					$inputs.val('');
				}
			}
		});
	}
	
});