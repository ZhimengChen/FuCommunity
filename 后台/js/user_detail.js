$(function(){
	let userId=window.location.search.split('&')[0].split('=')[1];
	let $detail=$('#detail');
	let $passBtn=$('#passBtn');
	let $rejectBtn=$('#rejectBtn');
	
	$passBtn.bind('click',handle);
	$rejectBtn.bind('click',handle);
	
	$.ajax({
		url: 'http://www.chenzhimeng.top/fu-community/admin/user/'+userId,
		dataType: 'JSON'
	}).then((data)=>{
		$detail.children().eq(0).children().eq(1).text(data.studentName);
		$detail.children().eq(1).children().eq(1).text(data.studentNo);
		$detail.children().eq(2).children('img').attr('src',data.studentCard);
		$detail.children('div').eq(3).prop('hidden',data.hasCheck!==null);
		$detail.children('div').eq(4).prop('hidden',data.hasCheck==null);
		$detail.children('div').eq(4).children().eq(0).children().eq(1).text(data.hasCheck===true?"已通过":"未通过");
		$detail.children('div').eq(4).children().eq(1).children().eq(1).text(data.auditor.adminName);
		$detail.children('div').eq(4).children().eq(2).children().eq(1).text(new Date(data.auditTime).toLocaleDateString());
	});
	
	function handle(){
		$.ajax({
			url: 'http://www.chenzhimeng.top/fu-community/admin/user',
			type: 'PUT',
			data: {
				userId:userId,
				hasCheck: $(this).val()
			}
		}).then(location.reload());
	}
});
	