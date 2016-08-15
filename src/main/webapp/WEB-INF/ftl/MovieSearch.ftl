<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta name="keywords" content="狸猫资讯,为你所用,迅雷下载,种子下载,免费下载">
<meta name="description" content="《狸猫资讯》(LezoMao.com)是一款智能的资讯软件,已为你寻找关注的内容：${model.qWord},为你所用，才是资讯！">
<meta name="title" content="${model.qWord}-搜索结果,为你所用，才是资讯 - 狸猫资讯(LezoMao.com)" />
<title>${model.qWord}-搜索结果,为你所用，才是资讯 - 狸猫资讯(LezoMao.com)</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<div class="container-fluid">
				<#list model.qResponse as oDoc>
				    <#assign oDirectors = (oDoc.directors![])>
					<#assign oActors = (oDoc.actors![])>
					<#assign oRegions = (oDoc.regions![])>
					<#assign oGenres = (oDoc.genres![])>
					<div class="row top-margin">
						  <div class="col-md-3 movie-left">
						      <a  target="_blank" href="/movie/detail/${oDoc.id}.html">
						       <img src="${oDoc.image}" class="img-thumbnail movie-img" alt="${oDoc.name}"/>
						      </a>
					      </div>
					      <div class="col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item">
										    <h1 class="movie-title-margin">
											      <a  target="_blank" href="/movie/detail/${oDoc.id}.html">
												  ${oDoc.name}
											      <#if ((oDoc.name?length+oDoc.enname?length)<=40) > 
											       <small>${oDoc.enname}</small>
											      </#if>
											      <span class="badge movie-badge">${oDoc.year}</span>
										      </a>
										    </h1>
								    <li class="list-group-item"><strong>导演：</strong>${oDirectors?join(' ')}</li>
								    <li class="list-group-item"><strong>主演：</strong>${oActors?join(' ')}</li>
								    <li class="list-group-item"><strong>类型：</strong>${oGenres?join(' ')}</li>
								    <li class="list-group-item"><strong>上映：</strong>
								        <#list oRegions as region>
								          <#if (!region_has_next) >
								            ${region}(${oDoc.release?string("yyyy-MM-dd")})
					 					  </#if>	
								        </#list>
								    </li>
								  </ul>
					       </div>
				      </div>
				</#list>
				</div>
			</div>
		</div>
	</div>
</body>
</html>