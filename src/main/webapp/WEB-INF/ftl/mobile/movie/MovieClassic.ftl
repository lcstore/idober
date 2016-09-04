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
					    <#list classicObj.dataList as oMovie>
					       <#if ((oMovie_index%colCount)==0) >
					      	<div class="row content movie-bk">
						   </#if>
								<div class="col-xs-6 col-sm-6 col-md-2 col-lg-2">
								   <a href="/movie/detail/${oMovie.id}.html" target="_blank">
									  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded" width="116" height="166">
									</a>
									<div class="name-box">
									  <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" target="_blank">
									    ${unifyOf(oMovie.name,12,".")}
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
						         <#list oRank.dataList as oMovie>
						           <div class="hot-bottom">
								    <span class="badge"> 
								     ${(oMovie_index<9)?string('0'+(oMovie_index+1),(oMovie_index+1))}
								    </span>
								    <a href="/movie/detail/${oMovie.id}.html" target="_blank">${unifyOf(oMovie.name,12,".")}</a>
								    <span class="time-right">${oMovie.rate?string("0.0")}</span>
								   </div>
							   </#list>
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