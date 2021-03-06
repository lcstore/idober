<!DOCTYPE html>
<html lang="en">
<head>
    <title><sitemesh:write property='title' /></title>
    <sitemesh:write property='head'/>
    
</head>
<body>
	<!-- Fixed navbar -->
	<div id="top-navbar" class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
		 <div class="row">
		    <div class="col-xs-12 col-sm-12 col-md-3">
			<div class="navbar-header center-header">
			   <div id="logo-2015">
				  <a class="navbar-brand logo" href="http://www.lezomao.com/">
				      <img alt="狸猫资讯" src="${static_host}/assets/img/cat-logo.png" width="96px" height="76px">
				      <span class="site">狸猫资讯</span>
				  </a>
			   </div>
			</div>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-4">
			   <form target="_blank" action="${qAction!'/search/movie'}" role="search" class="navbar-form navbar-left form-search" id="qForm">
					<div class="form-group input-append">
					      <input type="search" name="q" value="${qWord!'头脑特工队'}" class="form-control search-query" id="qWord">
						<button type="submit" class="btn btn-success search-btn" id="qBtn">Go</button>
					</div>
				</form>
			</div>
			<div class="col-xs-12 col-sm-12 col-md-5">
				<ul class="navbar-collapse collapse nav navbar-nav channel-nav">
					<li><a class="active" href="/">首页</a></li>
					<li><a href="#">影视</a></li>
					<li><a href="#">科技</a></li>
					<li class="login-nav">
					
					<a id="login"  href="javascript:void(0)" rel="nofollow" class="btn btn-xs login-btn" role="button" onclick="toLogin()">登陆</a>
					/
					<a id="signup" href="javascript:void(0)" rel="nofollow" class="btn btn-xs login-btn" role="button" onclick="toSignup()">注册</a>
					<div id="wb_connect_btn" ></div>
					</li>
					
				</ul>
			</div>
		  </div><!--/div.row -->
		</div>
	</div>
	<!-- /.navbar -->
	
	
	<!-- for next decorator.navbar -->
    <sitemesh:write property='body'/>
    <script>
      function toLogin(){
        window.location.href='/login?retTo='+encodeURIComponent(window.location.href);
      }
      function toSignup(){
      }
      function toLogout(){
        console.log('do logout...');
        if (typeof(QC) != "undefined"){
          QC.Login.signOut();
        }else {
          $.removeCookie('__wb__k');
        }
        $.removeCookie('user_nick');
        
        var $Login = $('#login-user');
		$Login.attr('id','login');
		$Login.attr('onclick','toLogin()');
		$Login.text('登陆');
		var $Signup = $('#logout');
		$Signup.attr('id','signup');
		$Signup.attr('onclick','toSignup()');
		$Signup.text('注册');
      }
    </script>

</body>
</html>