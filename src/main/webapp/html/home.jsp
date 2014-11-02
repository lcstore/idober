<!DOCTYPE html>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<html lang="zh-cn">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫，助你快乐生活</title>
<link rel="stylesheet" href="../css/bootstrap.css">
<link rel="stylesheet" href="../css/bootstrap-theme.min.css">
<style>
body {
	font-family: 'Microsoft Yahei', 'Segoe UI', Verdana, Helvetica,
		sans-serif;
}

.divider-vertical {
	height: 50px;
	border-left: 1px solid #111415;
	border-right: 1px solid #646668;
	opacity: 0.4;
}

.navbar-nav .active {
	border-radius: 0.3;
}

.act-col-box {
	background-color: #fefefe;
	border: 1px solid #efefef;
	border-radius: 10px;
	text-align: center;
	margin: 10px -10px;
	padding: 10px;
	height: 280px;
}

.list-col-box {
	background-color: #fefefe;
	text-align: center;
	margin: 0px -16px;
	padding: 10px;
	height: 280px;
}

.list-col-box:hover {
	border: 2px solid #efefef;
	border-radius: 2px;
	background: none repeat scroll 0 0 #fff;
	margin: 0px -14px;
}

.actbox-padding {
	padding-left: 10px;
	padding-right: 10px;
	padding-top: 48px;
	padding-bottom: 0px;
}

.act-pic img {
	width: 200px;
	height: 200px;
}

.shop-pic {
	border: 1px solid white;
	float: right;
	position: absolute;
	right: 5%;
	top: 5%;
	z-index: 1;
}

.shop-pic img {
	width: 36px;
	height: 36px;
}

.act-list-pic {
	float: left;
	height: 180px;
	margin: 6px 0 0;
	overflow: hidden;
	position: relative;
	width: 180px;
}

.act-list-text {
	color: #333;
	float: right;
	line-height: 23px;
	padding: 0;
	width: 450px;
}

.list_price {
	color: #c40000;
	font-size: 18px;
	font-weight: bolder;
	margin-right: 8px;
}

.zm-coin {
	font-family: arial;
}

.msg-container {
	font-family: arial;
}

.listbotron-padding {
	padding-left: 30px;
	padding-right: 0px;
	padding-top: 0px;
	padding-bottom: 0px;
	margin-bottom: 0px;
	margin-top: -30px;
}
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
							<li class="active"><a href="/">首页</a></li>
							<li><a href="#">爱抢眼</a></li>
							<li><a href="#">购实惠</a></li>
						</ul>
					</div>
					<div class="col-md-4">
						<form class="navbar-form navbar-left" role="search">
							<div class="form-group">
								<input type="text" size="40" class="form-control"
									placeholder="Search">
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
	<div class="main">
		<div class="act">
			<div class="jumbotron actbox-padding">
				<div class="container-fluid">
					<div class="row">
						<div class="col-md-4 act-col">
							<div class="act-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/36177809" target="_blank">
										<img
										alt="Coolpad 酷派 大神F1青春版（8297D） 3G手机（智尚白） TD-SCDMA/GSM 双卡双待 真八核"
										src="http://d9.yihaodianimg.com/N07/M02/3C/61/CgQIz1QOcRiAc3wtAAF4PA8xBek31801_200x200.jpg" />
									</a>
								</div>
								<div class="act-txt">
									<a href="http://item.yhd.com/item/36177809" target="_blank">
										<span>Coolpad 酷派 大神F1青春版（8297D） 3G手机（智尚白） TD-SCDMA/GSM
											双卡双待 真八核 ... </span>
									</a>
								</div>
								<div class="act-price">
									<del>
										<span class="zm-coin">¥</span>999
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>999</strong>
								</div>
								<div class="act-shop shop-pic">
									<img alt="1号店"
										src="http://d7.yihaodianimg.com/N02/M02/40/EB/CgQCsFLVBOOAE0boAAAK5UNpfUI56300.png" />
								</div>
							</div>
						</div>
						<div class="col-md-4 act-col">
							<div class="act-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
								<div class="act-txt">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<span>Olay 玉兰油 新生塑颜金纯活能水 150ml</span>
									</a>
								</div>
								<div class="act-price">
									<del>
										<span class="zm-coin">¥</span>240
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>169</strong>
								</div>
								<div class="act-shop shop-pic">
									<img alt="1号店"
										src="http://d7.yihaodianimg.com/N02/M02/40/EB/CgQCsFLVBOOAE0boAAAK5UNpfUI56300.png" />
								</div>
							</div>
						</div>
						<div class="col-md-4 act-col">
							<div class="act-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
								<div class="act-txt">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<span>Olay 玉兰油 新生塑颜金纯活能水 150ml</span>
									</a>
								</div>
								<div class="act-price">
									<del>
										<span class="zm-coin">¥</span>240
									</del>
									<strong class="list_price"><span class="zm-coin">¥</span>169</strong>
								</div>
								<div class="act-shop shop-pic">
									<img alt="1号店"
										src="http://d7.yihaodianimg.com/N02/M02/40/EB/CgQCsFLVBOOAE0boAAAK5UNpfUI56300.png" />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="data-main ">
			<div class="row">
				<div class="col-md-9 jumbotron listbotron-padding">
					<div class="row-fluid">
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
						<div class="col-md-4">
							<div class="list-col-box">
								<div class="act-pic">
									<a href="http://item.yhd.com/item/8095858" target="_blank">
										<img alt="Olay 玉兰油 新生塑颜金纯活能水 150ml"
										src="http://d6.yihaodianimg.com/N03/M08/22/31/CgQCs1ER8ayAd6G1AAPUTW9n4fU51801_200x200.jpg" />
									</a>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-3">right</div>
			</div>
		</div>
	</div>
	<footer></footer>
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script
		src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
</body>
</html>
