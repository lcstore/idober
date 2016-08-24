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
					      <div class="title"><h4>最新电影</h4></div>	
					      <div class="txtsize">
					           <#list newlyObj.dataList as oRank>
						           <div class="hot-bottom">
						               <span class="badge"> 
									     ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
									    </span>
									    <a href="/movie/detail/${oRank.id}.html" target="_blank">${unifyOf(oRank.name,14,"")}</a>
									    <span class="time-right">${oRank.timestamp?string["MM-dd"]}</span>
								   </div>
							   </#list>
						  </div>
					</div>
					
					<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9 right-box">	
					   <div class="title"><h4>正在热播</h4></div>		
					   <#assign colCount="4"?number >
					   <#list playingObj.dataList as oMovie>
					      <#if ((oMovie_index%colCount)==0) >
					      <div class="row content content">
						  </#if>
								<div class="col-xs-6 col-sm-6 col-md-3 col-lg-3">
								   <#if (oMovie.torrents_size>0 || oMovie.shares_size>0)>
							         <span class="download-newly"></span>
						           </#if>
								   <a href="/movie/detail/${oMovie.id}.html" target="_blank">
									  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded" width="190" height="270">
									</a>
									<div class="name-box">
									  <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" target="_blank">
									    ${unifyOf(oMovie.name,14,"")}
									  </a>
									</div>
								</div>
						<#if (((oMovie_index+1)%colCount)==0) || (!oMovie_has_next)>
						</div>
						</#if>
					  </#list>			
					</div>
				</div>
		</div>
<sitemesh:write property='body'/>		
</body>
</html>