var commonFunction = {

	// 根据属性获取DOM节点
	getDom:function(){

	}

	//
};

(function(){
	// 返回按钮功能 根据ID 绑定
	var backDom = document.getElementById('back');
	backDom.onclick = function(){history.go(-1);}

})();