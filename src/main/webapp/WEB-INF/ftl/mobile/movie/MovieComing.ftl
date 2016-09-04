<!DOCTYPE html>
<html lang="zh-cn">
<head>
   <title><sitemesh:write property='title' /></title>
   <sitemesh:write property='head'/>
</head>
<body>
		<div class="container movie-box box-mtop"  id="coming">
				<div class="title"><h4>即将上映</h4></div>	
				   <#assign colCount="6"?number >
				   <#list upcomingObj.dataList as oMovie>
			       <#if ((oMovie_index%colCount)==0) >
				   <div class="row content movie-bk">
				   </#if>
						<div class="col-xs-6 col-sm-2 col-md-2 col-lg-2">	
						       <#if (oMovie.torrents_size>0 || oMovie.shares_size>0)>
							     <span class="download-newly"></span>
						       </#if>
							   <a href="/movie/detail/${oMovie.id}.html"  target="_blank">
								  <img src="${oMovie.image}" alt="${oMovie.name}" class="img-rounded" width="190" height="270">
								</a>
								<div class="name-box">
								    <a href="/movie/detail/${oMovie.id}.html" title="${oMovie.name}" target="_blank">
								          ${unifyOf(oMovie.name,12,".")}
								     </a>
								</div>
						</div>
					<#if (((oMovie_index+1)%colCount)==0) || (!oMovie_has_next)>
					</div>
					</#if>
					</#list>
		</div>
<sitemesh:write property='body'/>
</body>
</html>