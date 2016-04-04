<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="Keywords" content="低价,正品,折扣,优惠,电影,热门,经典,LEZOMAO,乐助猫">
<meta name="Description" content="乐助猫,识低价、辨真假,比价购物更放心！寻热门、觅经典,个性电影更贴心！">
<title><sitemesh:write property='title' />-乐助猫</title>
<sitemesh:write property='head'/>

<link rel="shortcut icon" href="/assets/img/gt_favicon.png">

<link rel="stylesheet" href="/assets/css/bootstrap.min.css?v=${version}">

<!-- Custom styles for our template -->
<link rel="stylesheet" href="/assets/css/bootstrap-theme.css?v=${version}"
	media="screen">
<link rel="stylesheet" href="/assets/css/main.css?v=${version}">

	<script src="/assets/js/jquery-1.11.3.min.js?v=${version}"></script>
	<script type="text/javascript">
	$(document).ready(function() {
		$('img').error(function(){
		       $(this).attr('src', '/assets/img/noimg220x220.jpg');
		       $(this).error = null;
		});
	});
	</script>
	<script>
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?f73a9b08e4d8fd7c7147c87d1d1dd8e7";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	</script>
	<!--
	  <script src="http://l.tbcdn.cn/apps/top/x/sdk.js?appkey=23250128"></script> 
	  -->
</head>

<body class="home">
    <!-- for next decorator.header -->
    <sitemesh:write property='body'/>
    
    
	<!-- JavaScript libs are placed at the end of the document so the pages load faster -->
	<script src="/assets/js/bootstrap-3.2.0.min.js?v=${version}"></script>
	<script src="/assets/js/headroom.min.js?v=${version}"></script>
	<script src="/assets/js/jQuery.headroom.min.js?v=${version}"></script>
	<script src="/assets/js/template.js?v=${version}"></script>
	<script type="text/javascript">
	    (function(win,doc){
	        var s = doc.createElement("script"), h = doc.getElementsByTagName("head")[0];
	        if (!win.alimamatk_show) {
	            s.charset = "gbk";
	            s.async = true;
	            s.src = "http://a.alimama.cn/tkapi.js";
	            h.insertBefore(s, h.firstChild);
	        };
	        var o = {
	            pid: "mm_28465473_4294201_43604587",/*推广单元ID，用于区分不同的推广渠道*/
	            appkey: "",/*通过TOP平台申请的appkey，设置后引导成交会关联appkey*/
	            unid: "tbclick",/*自定义统计字段*/
	            type: "click" /* click 组件的入口标志 （使用click组件必设）*/
	        };
	        win.alimamatk_onload = win.alimamatk_onload || [];
	        win.alimamatk_onload.push(o);
	    })(window,document);
	</script>

</body>
</html>