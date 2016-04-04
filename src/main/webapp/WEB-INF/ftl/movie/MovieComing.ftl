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
				   <#list newMovieVos as oMovie>
			       <#if ((oMovie_index%colCount)==0) >
				   <div class="row content movie-bk">
				   </#if>
						<div class="col-xs-6 col-sm-2 col-md-2 col-lg-2">	
							   <a href="/movie/detail/${oMovie.code}"  target="_blank">
								  <img src="${oMovie.imgUrl}" alt="${oMovie.title}" class="img-rounded">
								</a>
								<div class="name-box">
								  <a href="/movie/detail/${oMovie.code}" title="${oMovie.title}" target="_blank">
								    ${unifyOf(oMovie.title,12,".")}
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