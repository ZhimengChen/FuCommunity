$(function(){
	let $users=$('#users');
	let $page_users=$('#page_users');
	
	$users.pagination({
	    dataSource: 'http://www.chenzhimeng.top/fu-community/admin/user',
	    locator: 'users',
	    totalNumberLocator: function(response) {
	        return response.totalNum;
	    },
	    pageSize: 10,
	    ajax: {
	        beforeSend: function() {
	            $page_users.html('<center class="mt-5"><h5>正在拼命加载数据...</h5></center>');
	        }
	    },
	    callback: function(data, pagination) {
	        // template method of yourself
	        var html = template(data);
	        $page_users.html(html);
	    }
	})
	
	function template(data){
		var html='';
		for(i in data){
			html+='<div class="row my-2">'+
					'<div class="offset-1 col-2">'+data[i].studentName+'</div>'+
					'<div class="col-1">'+data[i].studentNo+'</div>'+
					'<div class="col-2"><a target="_blank" href="'+data[i].studentCard+'">预览</a></div>';
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
					'<a href="user_detail.html?user_id='+data[i].userId+'">'+(data[i].hasCheck==null?'审核':'查看详情')+'</a>'+
				  '</div>';
		}
		return html;
	}
});