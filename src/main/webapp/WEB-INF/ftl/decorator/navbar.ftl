<!DOCTYPE html>
<html lang="en">
<head>
    <title><sitemesh:write property='title' /></title>
    <sitemesh:write property='head'/>
</head>
<body>
	<!-- Fixed navbar -->
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
		 <div class="row">
		    <div class="col-xs-12 col-sm-12 col-md-3">
			<div class="navbar-header center-header">
			   <div id="logo-2015">
				  <a class="navbar-brand logo" href="http://www.lezomao.com/">狸猫</a>
			   </div>
			</div>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-4">
			   <form target="_blank" action="${qAction!'/search/movie'}" role="search" class="navbar-form navbar-left form-search" id="qForm">
					<div class="form-group input-append">
					      <input type="search" name="q" value="${qWord!'荒野猎人'}" class="form-control search-query" id="qWord">
						<button type="submit" class="btn btn-success search-btn" id="qBtn">Go</button>
					</div>
				</form>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-5">
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav pull-right">
					<li class="active"><a href="/">首页</a></li>
					<li><a href="/">花絮评说</a></li>
					<li><a href="/">热点关注</a></li>
					<li><a class="btn" href="/">登陆 / 注册</a></li>
				</ul>
			</div>
			</div>
		  </div><!--/div.row -->
		</div>
	</div>
	<!-- /.navbar -->
	
	
	<!-- for next decorator.navbar -->
    <sitemesh:write property='body'/>
</body>
</html>