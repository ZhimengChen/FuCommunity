$(function(){
	let typeCode=window.location.search.split('&')[0].split('=')[1];
	let type;
	let $header=$('.header');
	let $history=$('#history');
	let $page_history=$('#page_history');
	
	if(typeCode==='1'){
		type='个人认证';
	}else{
		type='组织审核';
	}
	$header.children().eq(1).text(type);
	
	$.ajax({
		url: "http://www.chenzhimeng.top/fu-community/admin/history/"+typeCode,
		dataType: "JSON",
	}).then((data)=>{
		$history.pagination({
		    dataSource: data,
		    callback: function(data, pagination) {
		        // template method of yourself
		        var html = template(data);
		        $page_history.html(html);
		    }
		});
	});
	
	function template(data){
		var html='';
		for(i in data){
			html+='<div class="row my-2">';
			
			if(data[i].hasCheck) {
				html+='<div class="offset-1 col-2 text-info">已通过';
			}else {
				html+='<div class="offset-1 col-2 text-danger">未通过';
			}
			
			html+=	  '</div>'+
					  '<div class="col-2">'+(data[i].auditor==null?"无":data[i].auditor.adminName)+'</div>'+
					  '<div class="col-1 ">'+new Date(data[i].auditTime).toLocaleDateString()+'</div>';
			
			if(typeCode==='1'){
				html+='<a href="user_detail.html?user_id='+data[i].userId+'">查看详情</a>';
			}else{
				html+='<a href="organization_detail.html?organization_id='+data[i].organizationId+'">查看详情</a>';
			}
					
					
			html+='</div>';
		}
		return html;
	}
});