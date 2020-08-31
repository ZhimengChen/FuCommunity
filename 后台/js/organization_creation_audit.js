$(function(){
	let $organizations=$('#organizations');
	let $page_organizations=$('#page_organizations');
	
	$.ajax({
		url: "http://www.chenzhimeng.top/fu-community/admin/organization",
		dataType: "JSON",
	}).then((data)=>{
		$organizations.pagination({
		    dataSource: data,
		    callback: function(data, pagination) {
		        // template method of yourself
		        var html = template(data);
		        $page_organizations.html(html);
		    }
		});
	});
	
	function template(data){
		var html='';
		for(i in data){
			html+='<div class="row my-2">'+
					'<div class="offset-1 col-2">'+data[i].organizationName+'</div>'+
					'<div class="col-1">'+data[i].founder.studentName+'</div>'+
					'<div class="col-2">'+data[i].contact+'</div>';
			if(data[i].hasCheck===true){
				html+='<div class="col-2 text-info">已通过';
			}else if(data[i].hasCheck===false){
				html+='<div class="col-2 text-danger">未通过';
			}else{
				html+='<div class="col-2">未审核';
			}
			html+=	'</div>'+
					'<div class="col-1">'+(data[i].auditor==null?'暂无':data[i].auditor.adminName)+'</div>'+
					'<div class="col-1 ">'+(data[i].auditTime==null?'未审核':new Date(data[i].auditTime).toLocaleDateString())+'</div>'+
					'<a href="organization_detail.html?organization_id='+data[i].organizationId+'">'+(data[i].hasCheck==null?'审核':'查看详情')+'</a>'+
				  '</div>';
		}
		return html;
	}
});