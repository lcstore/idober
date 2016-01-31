<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>乐助猫,识低价、辨真假,比价购物更放心！双11折扣，货比三家知实惠，对比验证辨真假。</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<div class="container-fluid">
				<#list model.qResponse as oDoc>
					<div class="row top-margin">
						  <div class="col-md-3 movie-left">
						       <img src="${oDoc.imgUrl}" class="img-thumbnail" style="width: 210px;height: 270px;" alt="${oDoc.name}"/>
					      </div>
					      <div class="col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item"><h1 class="movie-title-margin">${oDoc.name}<small>${oDoc.enname}</small><span class="badge movie-badge">${oDoc.year}</span></h1></li>
								    <li class="list-group-item"><strong>导演：</strong>${oDoc.directors}</li>
								    <li class="list-group-item"><strong>主演：</strong>${oDoc.actors}</li>
								    <li class="list-group-item"><strong>国家地区：</strong>${oDoc.region}</li>
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
								    <li class="list-group-item"><strong>下载地址：</strong>
									    <#assign oTorrents = oDoc.torrents?eval>
								        <#list oTorrents as oTor>
									        	<strong>${oTor.name}</strong>
									        	<a role="button" class="btn btn-success btn-xs" href="${oTor.url}">
									        	  下载
												</a>
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