<!DOCTYPE html>
<html lang="en">
<head>
    <title><sitemesh:write property='title'/></title>
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
				<a class="navbar-brand" href="index.html"><img
					src="/assets/img/logo.png" alt="Progressus HTML5 template"></a>
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
					<li class="active"><a href="#">Home</a></li>
					<li><a href="about.html">About</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown">More Pages <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="sidebar-left.html">Left Sidebar</a></li>
							<li class="active"><a href="sidebar-right.html">Right
									Sidebar</a></li>
						</ul></li>
					<li><a href="contact.html">Contact</a></li>
					<li><a class="btn" href="signin.html">SIGN IN / SIGN UP</a></li>
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