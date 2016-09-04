<!DOCTYPE html>
<html lang="en">
<head>
    <title><sitemesh:write property='title' /></title>
    <sitemesh:write property='head'/>
    
</head>
<body>
	<!--  navbar -->
	<div id="top-navbar" class="navbar-fixed-top" role="navigation">
      <form target="_blank" action="${qAction!'/search/movie'}" role="search" class="form-search" id="qForm">
	  <div class="form-group">
        <div class="input-group input-group-bk">
		  <span class="search-logo-box">
		    <img src="${static_host}/assets/img/cat-logo.png" alt="狸猫资讯" width="36px" height="30px">
		    <span class="site">狸猫资讯</span>
		  </span>
		  <input type="text" name="q" value="${qWord!'头脑特工队'}" class="form-control search-word" id="qWord" style="border-top-left-radius: 6px;border-bottom-left-radius: 6px;">
		  <a href="javascript:void(0)" rel="nofollow" class="box-btn search-btn" role="button" onclick="doQuery()">Go</a>
		  <a href="javascript:void(0)" rel="nofollow" class="login-btn" role="button" onclick="toLogin()">
		    <img src="${static_host}/assets/img/login-face.png" alt="登陆" class="login-img" width="36px" height="30px">
		  </a>
		</div>
	  </div>
	  </form>
	</div>
	<!-- /.navbar -->
	
	
	<!-- for next decorator.navbar -->
    <sitemesh:write property='body'/>
    <script>
      function doQuery(){
        $('#qForm').submit();
      }
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