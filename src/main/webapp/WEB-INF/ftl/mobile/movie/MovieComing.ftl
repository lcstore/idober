<!DOCTYPE html>
<html lang="zh-cn">
<head>
   <title><sitemesh:write property='title' /></title>
   <sitemesh:write property='head'/>
</head>
<body>
		<div class="container movie-box box-mtop"  id="coming">
					 <div class="panel panel-default">
						 <div class="panel-heading newly-title"><h4>即将上映</h4></div>
						 <div class="container">
						 <div class="row">
							<#list upcomingObj.dataList as oMovie>
								<div class="movie-list-box">	
								       <#if (oMovie.torrents_size>0 || oMovie.shares_size>0)>
									     <span class="download-newly"></span>
								       </#if>
									   <a href="/movie/detail/${oMovie.id}.html"  >
										  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded list-img">
										</a>
										<div class="name-box">
										    <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" >
										          ${unifyOf(oMovie.name,12,".")}
										     </a>
										</div>
								</div>
							</#list>
								   
						 </div>
						 </div>
					 </div>
		</div>
<sitemesh:write property='body'/>
</body>
</html>