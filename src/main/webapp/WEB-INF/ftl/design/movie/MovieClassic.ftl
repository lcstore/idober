<!DOCTYPE html>
<html lang="zh-cn">
<head>
   <sitemesh:write property='head'/>
  <!--
    http://movie.douban.com/nowplaying/shenzhen/
    
    https://www.douban.com/doulist/1295618/
    https://www.douban.com/doulist/114465/?start=450&sort=seq&sub_type=
    -->
</head>
<body>
<div class="container box-mtop"  id="movieClassic">
		<div class="row movie-box">
					<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9 left-box">	
					    <div class="title"><h4>经典电影</h4></div>					
						<div class="row content movie-bk">
							<div class="col-xs-6 col-sm-3 col-md-2 col-lg-2">
							   <a href="/Html/GP22522.html" title="澳门风云3" target="_blank">
								  <img src="http://tu.joy3g.com/20160225044255872.jpg" alt="澳门风云3" class="img-rounded">
								</a>
								<div class="name-box">
								  <a href="/Html/GP22522.html" title="澳门风云3" target="_blank">澳门风云3
								  </a>
								</div>
							</div>
							<div class="col-xs-6 col-sm-3 col-md-2 col-lg-2">
							   <a href="/Html/GP22522.html" title="澳门风云3" target="_blank">
								  <img src="http://tu.joy3g.com/20160225044255872.jpg" alt="澳门风云3" class="img-rounded">
								</a>
								<div class="name-box">
								  <a href="/Html/GP22522.html" title="澳门风云3" target="_blank">澳门风云3
								  </a>
								</div>
							</div>
						<!--end of row-->
						</div>
					</div>
				<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3  right-box movie-bk" id="movieRank">
				      <div class="tabbable">
					      <ul class="nav nav-tabs tab-box">
					        <span class="rank-title"><h4>票房榜</h4></span>
					        <li class="active"><a href="#tab1" data-toggle="tab">综合</a></li>
					        <li><a href="#tab2" data-toggle="tab">全国</a></li>
					        <li><a href="#tab3" data-toggle="tab">北美</a></li>
					      </ul>
					      <div class="tab-content rank-content">
					        <div class="tab-pane active" id="tab1">
					          <div class="hot-bottom">
							    <span class="badge">01</span>
							    <a href="#">海贼王 </a>
							    <span class="time-right">02-01</span>
							  </div>
					        </div>
					        <div class="tab-pane" id="tab2">
					           <div class="hot-bottom">
							    <span class="badge">02</span>
							    <a href="#">百无禁忌！女高中生私房话</a>
							    <span class="time-right">02-02</span>
							  </div>
					        </div>
					        <div class="tab-pane" id="tab3">
					           <div class="hot-bottom">
							    <span class="badge">02</span>
							    <a href="#">百无禁忌！女高中生私房话</a>
							    <span class="time-right">02-03</span>
							  </div>
					        </div>
					      </div>
					   </div>	
				</div>
		<!-end of row-->		
		</div>
				
</div>
<sitemesh:write property='body'/>		
</body>
</html>