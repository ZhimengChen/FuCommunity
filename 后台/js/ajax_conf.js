$(function(){
	$.ajaxSetup({
		error:(xhr)=>{
			let status=xhr.status;
			if(status===2384){
				alert('请先登录');
				top.location.href='login.html';
			}else if(status===2385){
				alert('登录状态过期，请重新登录');
				top.location.href='login.html';
			}else if(status===2386){
				alert('账号已在别处登录');
				top.location.href='login.html';
			}else{
				alert('服务器繁忙，请稍后再试，或联系开发者');
			}
		},
		headers:{
			'accessToken': localStorage.getItem("accessToken")
		}
	});
});