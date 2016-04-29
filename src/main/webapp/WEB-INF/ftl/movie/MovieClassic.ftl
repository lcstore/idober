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
					    <div class="title"><h4>经典电影</h4></div>
					    <#assign colCount="6"?number >
					    <#list classicVos as oMovie>
					       <#if ((oMovie_index%colCount)==0) >
					      	<div class="row content movie-bk">
						   </#if>
								<div class="col-xs-12 col-sm-6 col-md-2 col-lg-2">
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
				<div class="col-xs-12 col-sm-4 col-md-4 col-lg-3  right-box movie-bk" id="movieRank">
				      <div class="tabbable">
					      <ul class="nav nav-tabs tab-box">
					        <span class="rank-title"><h4>票房榜</h4></span>
					        <li class="active"><a href="#tab1" data-toggle="tab">综合</a></li>
					        <li><a href="#tab2" data-toggle="tab">全国</a></li>
					        <li><a href="#tab3" data-toggle="tab">北美</a></li>
					      </ul>
					      <div class="tab-content rank-content txtsize">
					        <div class="tab-pane active" id="tab1">
					           <#list allRankVos as oRank>
						           <div class="hot-bottom">
								    <span class="badge"> 
								     ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
								    </span>
								    <a href="/movie/detail/${oRank.code}" target="_blank">${unifyOf(oRank.title,12,".")}</a>
								    <span class="time-right">${oRank.updateTime?string["MM-dd"]}</span>
								   </div>
							   </#list>
					        </div>
					        <div class="tab-pane" id="tab2">
					           <#list cnRankVos as oRank>
						           <div class="hot-bottom">
								    <span class="badge"> 
								     ${(oRank_index<9)?string('0'+(oRank_index+1),(oRank_index+1))}
								    </span>
								    <a href="/movie/detail/${oRank.code}" target="_blank">${unifyOf(oRank.title,12,".")}</a>
								    <span class="time-right">${oRank.updateTime?string["MM-dd"]}</span>
								   </div>
							   </#list>
					        </div>
					        <div class="tab-pane" id="tab3">
						      <#list enRankVos as oRank>
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
					   </div>	
				</div>
		<!-end of row-->		
		</div>
				
</div>
<sitemesh:write property='body'/>		
</body>
</html>