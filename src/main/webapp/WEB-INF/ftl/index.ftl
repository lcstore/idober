<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>乐助猫，助你快乐生活</title>
<!-- Bootstrap core CSS -->
<link href="css/bootstrap.css" rel="stylesheet">
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>

<style type="text/css">
body {
	padding-top: 70px;
}

.bs-example {
	background-color: #fff;
	border-color: #ddd;
	border-radius: 4px 4px 0 0;
	border-width: 10px;
	box-shadow: none;
	margin-left: 0;
	margin-right: 0;
}

.row-left {
	float: left
}

.row-right {
	float: right;
	margin-right: 15px;
	width: 23%;
}

.floot {
	background-color: black;
}
.p-price {
    height: 20px;
    overflow: hidden;
}
.p-price strong {
    float: left;
    color: #e4393c;
    font-size: 14px;
    margin-right: 4px;
}
.p-img, .p-name, .p-price, .p-market, .p-detail {
    overflow: hidden;
}
.p-img {
    padding: 5px 0;
}

a img {
    border: 0 none;
}
img {
    vertical-align: middle;
}
del {
    text-decoration: line-through;
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
				<a title="Worktile" href="/" class="navbar-brand" id="header_logo">eMao</a>
			</div>
			<div class="collapse navbar-collapse navbar-header-collapse">

				<ul class="nav navbar-nav" id="header_menu">
					<li class="active"><a href="/">首页</a></li>
					<li><a href="#">今日推荐</a></li>
					<li><a href="#">实惠特区</a></li>
				</ul>
				<form class="navbar-form navbar-left" role="search">
					<div class="form-group">
						<input placeholder="Search" class="form-control" type="text"
							size="40"> <span class="form-group-btn">
							<button class="btn btn-success" type="submit">Go</button>
						</span>
					</div>
				</form>
				<ul id="header_me" class="nav navbar-nav navbar-right"
					style="margin-right: 40px;">
					<li class="divider-vertical hidden-xs"></li>
					<li><a href="#">注册</a></li>
					<li><a href="#">登录</a></li>
				</ul>
			</div>
		</nav>
	</header>
	<div id="wrap-all">
		<div class="inner" id="main">
			<div id="index-wrap">
				<div class="container">
					<div class="row">
						<div class="index-banner">
							<div class="index-banner-wrap">
								<div class="index-banner-inner"></div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="index-hot panel panel-default">
							<div class="panel-body">
							    <#list model["statList"] as pVo>
									<div class="col-md-3">
										<section>
										<div class="p-img">
										<a href="${pVo.productUrl}" target="_blank">
										<img width="220" height="220" class="err-product" data-img="1" alt="${pVo.productName}" src="${pVo.imgUrl}" title="${pVo.productName}">
										</a>
										</div>
										<div class="p-name">
										<a href="${pVo.productUrl}" target="_blank">
										  ${pVo.productName}
										</a>
										</div>
										<div class="p-price"><strong>￥${pVo.productPrice}</strong></div>
										</section>
									</div>
							    </#list>
							</div>
						</div>
					</div>
					<div></div>
					<div class="row">
						<div class="index-body ">
							<div class="row">
								<div class="col-md-9 row-left">
									<div class="body-left panel panel-default">
										<div class="panel-body">
											<div class="col-md-4">
												<article class="post tag-bs2">
													<section class="post-featured-image">
														<a
															onclick="_hmt.push(['_trackEvent', 'imagelink', 'click', 'Karma'])"
															target="_blank" href="/karma/" class="thumbnail"> <img
															width="800" height="600" alt="Karma"
															src="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															data-original="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															style="display: block;">
														</a> <span class="bs2"></span>
													</section>
												</article>
											</div>
											<div class="col-md-4">
												<article class="post tag-bs2">
													<section class="post-featured-image">
														<a
															onclick="_hmt.push(['_trackEvent', 'imagelink', 'click', 'Karma'])"
															target="_blank" href="/karma/" class="thumbnail"> <img
															width="800" height="600" alt="Karma"
															src="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															data-original="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															style="display: block;">
														</a> <span class="bs2"></span>
													</section>
												</article>
											</div>
											<div class="col-md-4">
												<article class="post tag-bs2">
													<section class="post-featured-image">
														<a
															onclick="_hmt.push(['_trackEvent', 'imagelink', 'click', 'Karma'])"
															target="_blank" href="/karma/" class="thumbnail"> <img
															width="800" height="600" alt="Karma"
															src="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															data-original="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															style="display: block;">
														</a> <span class="bs2"></span>
													</section>
												</article>
											</div>
											<div class="col-md-4">
												<article class="post tag-bs2">
													<section class="post-featured-image">
														<a
															onclick="_hmt.push(['_trackEvent', 'imagelink', 'click', 'Karma'])"
															target="_blank" href="/karma/" class="thumbnail"> <img
															width="800" height="600" alt="Karma"
															src="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															data-original="http://static.bootcss.com/expo/img/d/b0/76066886855f77948cea9821f8c6b.jpg"
															style="display: block;">
														</a> <span class="bs2"></span>
													</section>
												</article>
											</div>
										</div>
									</div>
								</div>
								<div class="col-md-3 row-right">
									<div class="body-right">
										<div class="">
											<div class="row panel panel-default">
												<div class="panel-body">
													<article class="post tag-bs2 ">
														<section>
															<img alt="提高效率"
																src="https://dn-wtbox.qbox.me/img/index/i3.png?ver=3.2.26">
															<br>
															<h4>提高效率</h4>
															灵活的任务和日程，让团队成员的<br> 效率迅速提升，工作就是这么简单
														</section>
													</article>
												</div>
											</div>
											<div class="row panel panel-default">
												<div class="panel-body">
													<article class="post tag-bs2 ">
														<section>
															<img alt="提高效率"
																src="https://dn-wtbox.qbox.me/img/index/i3.png?ver=3.2.26">
															<br>
															<h4>提高效率</h4>
															灵活的任务和日程，让团队成员的<br> 效率迅速提升，工作就是这么简单
														</section>
													</article>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<footer class="hidden-xs " id="footer-outer" role="contentinfo">
				<div class="container">
					<hr>
					<div class="text-center row-fluid">
						Copyright &copy; 2014 LEZOMAO.COM 版权所有 &nbsp; <a
							href="http://www.miitbeian.gov.cn/">鄂ICP备14009865号</a>
					</div>
				</div>
			</footer>
		</div>
	</div>
</body>
</html>