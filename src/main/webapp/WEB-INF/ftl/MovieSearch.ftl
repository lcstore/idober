<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>${model.qWord}-电影搜索</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<div class="container-fluid">
				<#list model.qResponse as oDoc>
					<div class="row top-margin">
						  <div class="col-md-3 movie-left">
						       <img src="${oDoc.imgUrl}" class="img-thumbnail movie-img"  alt="${oDoc.name}"/>
					      </div>
					      <div class="col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item">
									    <h1 class="movie-title-margin">
									     <a href="/movie/detail/${oDoc.id}" target="_blank">
									      ${oDoc.name}
									      </a>
									      <#if ((oDoc.name?length+oDoc.enname?length)<=40) > 
									       <small>${oDoc.enname}</small>
									      </#if>
									      <span class="badge movie-badge">${oDoc.year}</span>
									    </h1>
								    </li>
								    <li class="list-group-item"><strong>导演：</strong>${oDoc.directors}</li>
								    <li class="list-group-item"><strong>主演：</strong>${oDoc.actors}</li>
								    <li class="list-group-item"><strong>地区：</strong>${oDoc.region}</li>
								    <#assign oShares = oDoc.shares?eval>
								    <#if (oShares?size>0) > 
								        <li class="list-group-item"><strong>分享：</strong>
								          <#list oShares as oShare>
									        	<a href="${oShare.url}" target="_blank">${oShare.name}
									        	</a>
									        	  <#if oShare.secret?? > 
									        	  <span>(${oShare.secret})</span>
									        	  </#if>
									        	  <#if  (oShare_has_next) > 
									        	   <span>,&nbsp; </span>
									        	  </#if>
								          </#list>
								        </li>
									</#if> 
								    <li class="list-group-item">
									    <#assign oTorrents = ((oDoc.torrents)!"[]")?eval>
									    <#if (oTorrents)?size <1 >
									      	<strong>下载地址：</strong>
									    </#if>
								        <#list oTorrents as oTor>
										  <div id="tor${oTor_index}">
											  <#if (oTor.type == 'bttiantang-torrent') >
											    <form id="form${oTor_index}" action="/movie/download">
											         <strong>
											         下载地址：
													    <a href="${oTor.url}" >
												        	${unifyOf(((oTor.name)?length>0)?string((oTor.name),(oDoc.name)),70,'.')}
														</a>
													</strong>
												    <input class="btn btn-default" type="hidden" name="u" value="${oTor.url}">
												    <input class="btn btn-default" type="hidden" name="m" value="${oTor.method}">
												    <input class="btn btn-default" type="hidden" name="p" value="${oTor.param}">
												     <input class="btn btn-default" type="hidden" name="n" value="${oTor.name}">
												    <button type="submit" class="btn btn-success btn-xs downbtn" >
												       种子下载
												    </button>
												 </form>
											  <#else>
											   <strong>下载地址：
											   <a href="${oTor.url}" >
    											   	${unifyOf(((oTor.name)?length>0)?string((oTor.name),(oDoc.name)),70,'.')}
												</a>
												</strong>
											   <button type="button" class="btn btn-success btn-xs downbtn" onclick='window.location.href=urlcodesc.encode("${oTor.url}","thunder");'>
											     迅雷下载
											   </button>
											  </#if>
									
								          </div>
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
	<script src="/assets/js/custom/urlcodesc.js?v=${version}"></script>
</body>
</html>