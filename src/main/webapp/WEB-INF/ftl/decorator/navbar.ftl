<!DOCTYPE html>
<html lang="en">
<head>
    <sitemesh:write property='head'/>
</head>
<body>
	<!-- Fixed navbar -->
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<!-- Button for smallest screens -->
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target=".navbar-collapse">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="/"><img
					src="/assets/img/logo.png" alt="乐助猫,识低价、辨真假,比价购物更放心！"></a>
			</div>
			<div class="center-block">
			   <form target="_blank" action="/search/build" role="search" class="navbar-form navbar-left form-search" id="qForm">
					<div class="form-group input-append">
						<#if qWord==null>
					      <input type="search" name="q" value="牛奶" class="form-control search-query" id="qWord">
						<#else>
					      <input type="search" name="q" value="${qWord}" class="form-control search-query" id="qWord">
					     </#if>
						<button type="submit" class="btn btn-success search-btn" id="qBtn">Go</button>
					</div>
				</form>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav pull-right">
					<li class="active"><a href="/">首页</a></li>
					<li><a href="/">About</a></li>
					<li><a href="/">Contact</a></li>
					<li><a class="btn" href="/">SIGN IN / SIGN UP</a></li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
	</div>
	<!-- /.navbar -->
	
	
	<!-- for next decorator.navbar -->
    <sitemesh:write property='body'/>
</body>
</html>