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
					      <div class="title"><h4>今日更新${dailyObject.total}部</h4></div>	
					      <div class="txtsize">
					           <#list dailyObject.dailyRankVos as oRank>
						           <div class="hot-bottom">
								    <span class="badge"> 
								     ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
								    </span>
								    <a href="/movie/detail/${oRank.code}" target="_blank">${unifyOf(oRank.title,12,".")}</a>
								    <span class="time-right">${oRank.updateTime?string["MM-dd"]}</span>
								   </div>
							   </#list>
						  </div>
					</div>
					
					<div class="col-xs-12 col-sm-9 col-md-9 col-lg-9 right-box">	
					    <div class="title"><h4>本周热门电影</h4></div>		
					    <#assign colCount="4"?number >
					    <#list weeklyObject.dataList as oMovie>
					       <#if ((oMovie_index%colCount)==0) >
					      	<div class="row content content">
						   </#if>
								<div class="col-xs-6 col-sm-6 col-md-3 col-lg-3">
								   <a href="/movie/detail/${oMovie.code}" target="_blank">
									  <img src="${oMovie.imgUrl}" alt="${oMovie.title}" class="img-rounded">
									</a>
									<div class="name-box">
									  <a href="/movie/detail/${oMovie.code}" title="${oMovie.title}" target="_blank">
									    ${unifyOf(oMovie.title,8,".")}
									  </a>
									</div>
								</div>
							<#if (((oMovie_index+1)%colCount)==0) || (!oMovie_has_next)>
							  <!--end of row-->
						      </div>
							</#if>
					    </#list>			
					</div>
				</div>
		</div>
<sitemesh:write property='body'/>		
</body>
</html>