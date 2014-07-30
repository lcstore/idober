<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8"%>
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
</style>
</head>
<body>
	<div class="navbar navbar-inverse nav-pills navbar-fixed-top"
		role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">lezomao</span> <span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">乐助猫</a>
			</div>

			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav navbar-left">
					<li class="active"><a href="#">首页</a></li>
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
				<ul class="nav navbar-nav navbar-right">
					<li><a href="#">注册</a></li>
					<li><a href="#">登录</a></li>
					<li><a href="#">个人中心</a></li>
					<li><a href="#">关于我们</a></li>
				</ul>
			</div>
			<!--/.navbar-collapse -->
		</div>
	</div>

	<div class="row">
		<div class="col-sm-6 col-md-4">
			<div class="thumbnail">
				<img data-src="holder.js/300x300" alt="...">
				<div class="caption">
					<h3>Thumbnail label</h3>
					<p>...</p>
					<p>
						<a href="#" class="btn btn-primary" role="button">Button</a> <a
							href="#" class="btn btn-default" role="button">Button</a>
					</p>
				</div>
			</div>
		</div>
	</div>

	<div>
		<div class="container">
			<ul class="pagination">
				<li class="disabled"><span>&laquo;</span></li>
				<li class="active"><span>1 <span class="sr-only">(current)</span></span></li>
				<li><a href="#">2</a></li>
				<li><a href="#">3</a></li>
				<li><a href="#">4</a></li>
				<li><a href="#">5</a></li>
				<li><a href="#">&raquo;</a></li>
			</ul>
		</div>
	</div>
</body>
</html>