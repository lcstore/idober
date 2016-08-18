var _hmt = _hmt || [];
(function() {
	var hm = document.createElement("script");
	hm.src = "//hm.baidu.com/hm.js?f73a9b08e4d8fd7c7147c87d1d1dd8e7";
	var s = document.getElementsByTagName("script")[0];
	s.parentNode.insertBefore(hm, s);
})();

$(document).ready(function() {
	$('img').error(function() {
		$(this).attr('src', '/assets/img/noimg220x220.jpg');
		$(this).error = null;
	});
	loadLoginQQ();
});
var loadLoginQQ = function() {
	if (!document.cookie || document.cookie.indexOf('__qc__k=TC_MK=') < 0) {
		return false;
	}
	var hm = document.createElement("script");
	// hm.src = "http://qzonestyle.gtimg.cn/qzone/openapi/qc_loader.js"; //减少版本检查请求
	hm.src = "http://qzonestyle.gtimg.cn/qzone/openapi/qc-1.0.1.js";
	hm['data-appid'] = $('qqAppId').val();
	hm['data-redirecturi'] = $('qqRedirectUrl').val();
	hm.charset = "utf-8";
	loadScript(hm, checkLoginQQ);

	function checkLoginQQ() {
		if (QC.Login.check()) {
			QC.Login.getMe(function(openId, accessToken) {
				console.log([ "当前登录用户的", "openId为：" + openId,
						"accessToken为：" + accessToken ].join("\n"));
				var $Login = $('#login');
				$Login.attr('id', 'login-user');
				$Login.text(openId);
				var $Signup = $('#signup');
				$Signup.attr('id', 'logout');
				$Signup.attr('onclick', 'toLogout()');
				$Signup.text('退出');
			});
		}
	}
	function loadScript(script, callback) {
		script.type = "text/javascript";
		if (script.readyState) { // IE
			script.onreadystatechange = function() {
				if (script.readyState == "loaded"
						|| script.readyState == "complete") {
					script.onreadystatechange = null;
					callback();
				}
			};
		} else { // Others
			script.onload = function() {
				callback();
			};
		}
		if (!script.src) {
			var err = new Error();
			err.name = 'LoadScriptError';
			err.message = 'src must not be empty';
			throw err;
		}
		document.getElementsByTagName("head")[0].appendChild(script);
	}
}
