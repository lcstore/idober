<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>狸猫资讯,识低价、辨真假,比价购物更放心！</title>
<link rel="stylesheet" href="../css/home.css">
<!-- 添加标签图标16x16 -->
<!-- <link rel="icon" href="/favicon.ico" type="image/x-icon"> -->
<style>
</style>
</head>
<body>
	<header id="header">
		<nav role="navigation"
			class="navbar navbar-inverse navbar-default navbar-fixed-top"
			id="header_outer">
			<div class="navbar-header">
				<button data-target=".navbar-header-collapse" data-toggle="collapse"
					class="navbar-toggle" type="button">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a title="Lezomao" href="/" class="navbar-brand" id="header_logo">Lezomao</a>
			</div>
			<div class="collapse navbar-collapse navbar-header-collapse">
				<div class="row">
					<div class="col-md-3">
						<ul class="nav navbar-nav" id="header_menu">
							<li class="active"><a href="http://www.lezomao.com/">首页</a></li>
							<li><a href="#">爱抢眼</a></li>
							<li><a href="#">购实惠</a></li>
						</ul>
					</div>
					<div class="col-md-4">
						<form id="qForm" class="navbar-form navbar-left"  role="search" action="/search/build" target="_blank">
							<div class="form-group">
								<input id="qWord" class="form-control" type="text" size="40" 
									value="牛奶" name="q">
								<button id="qBtn"class="btn btn-success"  type="submit" >Go</button>
							</div>
						</form>
					</div>
					<div class="col-md-3">
						<ul id="header_me" class="nav navbar-nav navbar-right ng-scope">
							<li class="divider-vertical hidden-xs"></li>
							<li><a href="#" class="mr_15">登录</a></li>
							<li><a href="#">注册</a></li>
						</ul>
					</div>
				</div>
			</div>

		</nav>
	</header>
	<div class="main data-box">
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
					<div class="row">
					  <#list model["indexHotList"] as pVo>
						<div class="col-md-4 act-col">
							<div class="act-col-box">
								<div class="list-pic">
									<a href="${pVo.productUrl}" target="_blank">
										<img
										alt="${pVo.productName}"
										src="${pVo.imgUrl}" />
									</a>
								</div>
								<div class="act-txt">
									<a href="${pVo.productUrl}" target="_blank">
										<span>${pVo.productName}</span>
									</a>
								</div>
								<div class="act-price">
									<del>
										<span class="zm-coin">¥</span>${pVo.marketPrice}
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>${pVo.productPrice}</strong>
								</div>
								<div class="act-shop shop-pic">
									<img alt="${pVo.siteName}" src="/img/${pVo.siteId}.png" />
								</div>
							</div>
						</div>
						 </#list>
					</div>
				</div>
			</div>
		</div>
		<div class="data-main ">
			<div class="row">
				<div class="col-md-9 jumbotron listbotron-padding">
					<div class="row-fluid">
					   <#list model["pageList"] as pVo>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="list-pic">
									<a href="${pVo.productUrl}" target="_blank">
										<img
										alt="${pVo.productName}"
										src="${pVo.imgUrl}" />
									</a>
								</div>
								<div class="list-txt">
									<a href="${pVo.productUrl}" target="_blank">
										<span>${pVo.productName}</span>
									</a>
								</div>
								<div class="list-price">
									<del>
										<span class="zm-coin">¥</span>${pVo.marketPrice}
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>${pVo.productPrice}</strong>
								</div>
								<div class="shop-pic">
									<img alt="${pVo.siteName}" src="/img/${pVo.siteId}.png" />
								</div>
							</div>
						</div>
					 </#list>
					</div>
				</div>
				<div class="col-md-3 mgsbotron-padding">
					<div class="msg-container">
						<div class="row-fluid ">
							<div class="col-md-12">
								<div class="msg-col-box">
								   <h3>更新日志：</h3>
								   <span>1.新增搜索功能</span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<footer class="hidden-xs" id="footer-outer">
		<div class="columns">
		  <script type="text/javascript">
		var _bdhmProtocol = (("https:" == document.location.protocol) ? " https://" : " http://");
		document.write(unescape("%3Cscript src='" + _bdhmProtocol + "hm.baidu.com/h.js%3Fa9ae2d488204441bbdd903e86109496b' type='text/javascript'%3E%3C/script%3E"));
		</script>
		</div>
		<div class="text-center">
			Copyright &copy; 2014 LEZOMAO.COM 版权所有
			<p>鄂ICP备14009865号</p>
		</div>
	</footer>
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script
		src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
		<script type="text/javascript">
	$(document).ready(function() {
	   $("#qBtn").click(function(){
	      var qWordNode = $("#qWord");
	      var qWord =qWordNode.val();
	      if(qWord){
		    $('qForm').submit();
	      }
		});
	});
	</script>
</body>
</html>