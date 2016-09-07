<!DOCTYPE html>
<html lang="zh-cn">
<head>
   <title><sitemesh:write property='title' /></title>
   <sitemesh:write property='head'/>
</head>
<body>
<div class="container box-mtop"  id="movieClassic">
		<div class="row movie-box">
					<div class="col-xs-12 col-sm-8 col-md-8 col-lg-9 left-box">	
					     <div class="panel panel-default">
							 <div class="panel-heading newly-title"><h4>经典电影</h4></div>
							 <div class="container">
							 <div class="row">
								<#list classicObj.dataList as oMovie>
									<div class="movie-list-box">	
									       <#if (oMovie.torrents_size>0 || oMovie.shares_size>0)>
										     <span class="download-newly"></span>
									       </#if>
										   <a href="/movie/detail/${oMovie.id}.html"  target="_blank">
											  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded list-img">
											</a>
											<div class="name-box">
											    <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" target="_blank">
											          ${unifyOf(oMovie.name,12,".")}
											     </a>
											</div>
									</div>
								</#list>
									   
							 </div>
							 </div>
						 </div>
					
					</div>
				<div class="col-xs-12 col-sm-4 col-md-4 col-lg-3  right-box movie-bk" id="movieRank">
				      <div class="tabbable">
					      <ul class="nav nav-tabs tab-box">
					        <#list rankList as oRank>
					        <#if (oRank_index==0) >
					      	<li class="active"><a href="#tab${oRank_index}" data-toggle="tab">${oRank.name}</a></li>
						    <#else>
					      	<li><a href="#tab${oRank_index}" data-toggle="tab">${oRank.name}</a></li>
						    </#if>
					        </#list>
					      </ul>
					      <div class="tab-content rank-content txtsize">
					        <#list rankList as oRank>
					        <#if (oRank_index==0) >
					      	<div class="tab-pane active" id="tab${oRank_index}">
						    <#else>
					      	 <div class="tab-pane" id="tab${oRank_index}">
						    </#if>
							   <ul class="list-group newly-group">
							   <#list oRank.dataList as oRank>
								    <li class="list-group-item">
						               <span class="badge"> 
									      ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
									    </span>
									   <a href="/movie/detail/${oRank.id}.html" target="_blank">${unifyOf(oRank.name,14,"")}</a>
									   <span class="time-right">${oRank.rate?string("0.0")}</span>
								    </li>
							   </#list>
							  </ul>
					        </div>
					        </#list>
					       
					      </div>
					   </div>	
				</div>
		<!-end of row-->		
		</div>
				
</div>
<sitemesh:write property='body'/>		
</body>
</html>