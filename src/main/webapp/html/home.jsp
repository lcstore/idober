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

				<ul class="nav navbar-nav" id="header_menu">
					<li class="active"><a href="/">首页</a></li>
					<li><a href="/tour">聚实惠</a></li>
					<li><a href="/plan">爆款</a></li>
					<li><a href="/mobile">鉴定故事</a></li>
				</ul>

				<ul id="header_me" ng-controller="user_ctrl"
					class="nav navbar-nav navbar-right ng-scope">
					<li class="divider-vertical hidden-xs"></li>

					<li><a href="/signin" class="mr_15">登录</a></li>
					<li><a href="/signup?utm_source=nav_bar">注册</a></li>

				</ul>
			</div>
		</nav>
	</header>
	<div class="main"></div>
	<footer></footer>
	<script src="http://cdn.bootcss.com/jquery/1.11.1/jquery.min.js"></script>
	<script
		src="http://cdn.bootcss.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
</body>
</html>
