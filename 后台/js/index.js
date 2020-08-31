$(function(){
	$.get("http://www.chenzhimeng.top/fu-community/admin/check").then((data)=>{
		$("#adminName").text(data);
	});
	
	$('#refresh').bind('click',()=>{
		document.getElementById('myFrame').contentWindow.location.reload();
	});
});