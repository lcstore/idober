<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>欢迎登陆，精彩无价资讯等你来用！</title>
<style> 
body.home{
 background-color: #f8f6f2;
}
.login-wrap .single-panel, .other-warp .single-panel {
    background-color: #f8f6f2;
}
.login-mini{
  width:400px;
}
.single-panel-inner {
    margin: 5% auto 2% auto;
    background: 0 0;
    border: none;
    box-shadow: none;
}
.single-panel-header {
    border: 0;
    text-align: center;
}
a:link {
    color: #6f6e6b;
    outline: 0 none;
}
.logo-min {
    vertical-align: middle;
    overflow: hidden;
    display: inline-block;
}

.logo-min .wtf-worktile {
    font-size: 50px;
    vertical-align: middle;
}

.logo-min .wtf-worktiletext {
    font-size: 175px;
    display: inline-block;
    height: 20px;
    margin-left: 10px;
    line-height: 52px;
    vertical-align: top;
}

.login-wrap .login-tab {
    margin: 0;
    padding-bottom: 30px;
    font-size: 18px;
}
.flex-row {
    display: flex;
    display: -ms-flexbox;
    display: -webkit-flex;
    flex-direction: row;
    -ms-flex-direction: row;
    -webkit-flex-direction: row;
}
.login-wrap .login-tab li {
    cursor: pointer;
    list-style-type: none;
}
.login-title {
    color: #cacfd3;
}
.login-title.current {
    color: #6f6e6b;
}
a.login-title:link{
text-decoration:none;
}
a.login-title:visited{
text-decoration:none;
}
a.login-title:hover{
text-decoration:none;
}
a.login-title:active{
text-decoration:none;
}

.flex-se1 {
    flex: 1;
    -ms-flex: 1;
    -webkit-flex: 1;
}
.login-wrap .login-tab li.line {
    width: 40px;
    text-align: center;
}
.single-panel-section {
    margin-top: 10px;
}
.panel-hide {
    display: none !important;
}
.login_weixin_qr_code {
    overflow: hidden;
    margin: 0 -26px;
}
.single-panel-footer {
    border-top: 1px #e8edf3 solid;
    padding: 20px;
    text-align: center;
    line-height: 14px;
}
.single-panel-footer .split {
    display: inline-block;
    height: 14px;
    margin: 0 10px;
    border-left: 1px #e8edf3 solid;
}
.single-panel-footer a:hover {
   color: #da4f4a;
    text-decoration: none;
}
</style>
<script>
 function saveRetTo(){
   var search= window.location.search;
   if(/retTo=([^&]+)/.test(search)){
      var retTo = RegExp.$1;
      console.log('retTo:'+retTo);
      if(retTo){
        $.cookie('retTo', retTo, {path: '/' });
      }
   }
 } 
 function toLoginQQ(){
   saveRetTo();
   var qqAppId= $('#qqAppId').val();
   var qqRedirectUrl= $('#qqRedirectUrl').val();
   var loginUrl='https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id='+qqAppId+'&redirect_uri='+qqRedirectUrl+'&state=lezomao&scope=all';
   window.location.href = loginUrl;
 } 
</script>
</head>
<body>
<div class="inner " id="main">
  <div class="other-warp login-wrap">
    <div class="single-panel clearfix" wt-minheight="58">
      <div class="single-panel-inner login-mini ">
        <div class="single-panel-header login_logo_area">
          <a title="Worktile" href="/" class="logo-min">
            <i class="wtfont wtf-worktile"></i>
            <i class="wtfont wtf-worktiletext"></i>
          </a>
        </div>
        <div class="single-panel-body">
          <ul class="login-tab flex-row">
            <li class="flex-se1 text-right current login-title">帐号登录</li>
            <li class="line">|</li>
            <li class="flex-se1">
              <i class="fa fa-weixin"></i>
              <span class="login-title">微信登录</span>
            </li>
            <li class="line">|</li>
            <li class="flex-se1">
              <a class="login-title" href="#" onclick='toLoginQQ()'>
                <img alt="QQ登录" src="/assets/img/qqlogin.png">
                <span class="login-title">QQ登录</span>
              </a>
            </li>
          </ul>
          <div class="single-panel-section">
            <form class="form-horizontal wt-form" name="login_form">
              <div class="form-group">
                <div class="input-group-">
                  <input type="text" tabindex="1" name="login_name" placeholder="邮箱/用户名/手机" class="form-control"></div>
              </div>
              <div class="form-group">
                <div class="input-group-">
                  <input type="password" tabindex="2" name="login_password" placeholder="密码" class="form-control"></div>
              </div>
              <div class="form-group">
                <button data-loading-text="登录中…" tabindex="4" type="button" class="btn btn-success btn-lg btn-block">登 录</button></div>
            </form>
          </div>
          <div class="login_weixin_qr_code text-center panel-hide" id="weixin_qr_code">
            <iframe width="300px" height="400px" frameborder="0" src="" scrolling="no"></iframe>
          </div>
        </div>
        <div class="single-panel-footer">
          <a href="/forgot" class="ng-scope">忘记密码</a>
          <span class="split"></span>
          <a href="/signup" class="ng-scope">免费注册</a></div>
      </div>
    </div>
  </div>
</div>
</body>
</html>