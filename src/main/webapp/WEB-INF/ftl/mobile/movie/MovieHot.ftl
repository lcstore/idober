<!DOCTYPE html>
<html lang="zh-cn">
<head>
   <title><sitemesh:write property='title' /></title>
   <sitemesh:write property='head'/>
</head>
<body>
<div class="container"  id="moviehot">
				<div class="row movie-box">
					<div class="col-xs-12 col-sm-3 col-md-3 col-lg-3 left-box">	
						  <div class="panel panel-default">
							  <div class="panel-heading newly-title"><h4>最新电影</h4></div>
							  <ul class="list-group newly-group">
							   <#list newlyObj.dataList as oRank>
								    <li class="list-group-item">
						               <span class="badge"> 
									      ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
									    </span>
									   <a href="/movie/detail/${oRank.id}.html" target="_blank">${unifyOf(oRank.name,14,"")}</a>
									   <span class="time-right">${oRank.timestamp?string["MM-dd"]}</span>
								    </li>
							   </#list>
							  </ul>
						 </div>
					</div>
					
					<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9 right-box">	
					  <div class="panel panel-default">
						  <div class="panel-heading newly-title"><h4>正在热播</h4></div>
						 <div class="container">
						 <div class="row">
								   
						<#list playingObj.dataList as oMovie>
							<div class="movie-list-box">
							   <#if (oMovie.torrents_size>0 || oMovie.shares_size>0)>
						         <span class="download-newly"></span>
					           </#if>
							   <a href="/movie/detail/${oMovie.id}.html" target="_blank">
								  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded list-img">
								</a>
								<div class="name-box">
								  <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" target="_blank">
								    ${unifyOf(oMovie.name,14,"")}
								  </a>
								</div>
							</div>
					  </#list>
								   
						 </div>
						 </div>
					 </div>	
					</div>
				</div>
		</div>
<sitemesh:write property='body'/>		
</body>
</html>