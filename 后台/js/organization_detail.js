$(function(){
	let organizationId=window.location.search.split('&')[0].split('=')[1];
	let $detail=$('#detail');
	let $passBtn=$('#passBtn');
	let $rejectBtn=$('#rejectBtn');
	
	$passBtn.bind('click',handle);
	$rejectBtn.bind('click',handle);
	
	$.ajax({
		url: 'http://www.chenzhimeng.top/fu-community/admin/organization/'+organizationId,
		dataType: 'JSON'
	}).then((data)=>{
		$detail.children().eq(0).children().eq(1).text(data.organizationName);
		$detail.children().eq(1).children('img').attr('src',data.logo);
		$detail.children().eq(2).children().eq(1).text(data.slogan);
		$detail.children().eq(3).children().eq(1).text(data.intro);
		$detail.children().eq(4).children().eq(1).text(data.contact);
		$detail.children('div').eq(5).prop('hidden',data.hasCheck!==null);
	});
	
	function handle(){
		$.ajax({
			url: 'http://www.chenzhimeng.top/fu-community/admin/organization/'+organizationId,
			type: 'PUT',
			data: {
				hasCheck: $(this).val()
			}
		}).then(location.reload());
	}
});
	