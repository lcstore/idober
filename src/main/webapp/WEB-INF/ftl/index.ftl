<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫,购实惠,购正品,快捷购物不吃亏</title>
<meta name="Keywords" content="购实惠,购正品,进口食品,美容护理,家用电器,手机数码,LEZOMAO,乐助猫。">
<meta name="Description" content="乐助猫,真实快捷的导购,助您购实惠、购正品,快捷购物不吃亏。">
<link rel="stylesheet" href="../css/bootstrap.css">
<link rel="stylesheet" href="../css/bootstrap-theme.min.css">
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
						<form class="navbar-form navbar-left" role="search" action="/search/build" target="_blank">
							<div class="form-group">
								<input type="text" size="40" class="form-control"
									placeholder="Search" name="q">
								<button type="submit" class="btn btn-success">Go</button>
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
									<div class="list-pic">
										<a href="http://item.yhd.com/item/8095858" target="_blank">
											<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
											src="http://d8.yihaodianimg.com/N03/M05/C0/A3/CgQCtVI_4nyAUV-1AAC4cSD25lI41901_60x60.jpg" />
										</a>
									</div>
								</div>
							</div>
							<div class="col-md-12">
								<div class="msg-col-box">
									<div class="list-pic">
										<a href="http://item.yhd.com/item/8095858" target="_blank">
											<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
											src="http://d8.yihaodianimg.com/N03/M05/C0/A3/CgQCtVI_4nyAUV-1AAC4cSD25lI41901_60x60.jpg" />
										</a>
									</div>
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
</body>
</html>