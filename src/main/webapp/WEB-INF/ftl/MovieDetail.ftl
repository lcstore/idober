<!DOCTYPE html>
<html lang="zh-cn">
<head>
<title>${oDoc.name}(${oDoc.year})</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
				<div class="container-fluid">
					<div class="row top-margin">
						  <div class="col-md-3 movie-left">
						       <img src="${oDoc.imgUrl}" class="img-thumbnail movie-img" alt="${oDoc.name}"/>
					      </div>
					      <div class="col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item">
										    <h1 class="movie-title-margin">
											    <a href="${oDoc.id}" target="_blank">
												  ${oDoc.name}
											    </a>
										      <#if ((oDoc.name?length+oDoc.enname?length)<=40) > 
										       <small>${oDoc.enname}</small>
										      </#if>
										      <span class="badge movie-badge">${oDoc.year}</span>
										    </h1>
								    <li class="list-group-item"><strong>导演：</strong>${oDoc.directors}</li>
								    <li class="list-group-item"><strong>主演：</strong>${oDoc.actors}</li>
								    <li class="list-group-item"><strong>地区：</strong>${oDoc.region}</li>
								    <li class="list-group-item"><strong>类型：</strong>${oDoc.genres}</li>
								    <li class="list-group-item"><strong>上映：</strong>${oDoc.date?string("yyyy-MM-dd")}</li>
								    <#assign oShares = ((oDoc.shares)!"[]")?eval>
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
								   
								  </ul>
					       </div>
				      </div>
				</div>
			</div>
		</div>
		<div class="container top-margin">
		   <div class="story">
				<div class="story-header">
					  <h4>剧情介绍</h4>
				</div>
				<div class="story-body">
				  <p>${oDoc.story}</p>
				 </div>
			</div>
		</div>
		
		<div class="container downlist top-margin">
		  <li class="list-group-item">
		    <#assign oTorrents = ((oDoc.torrents)!"[]")?eval>
		    <#if oTorrents?size gt 0 >
		      	<strong>下载地址：</strong>
		    <#else>
		        <strong>暂无地址：</strong>
	           <button id="searchMovie" type="button" class="btn btn-warning btn-xs downbtn" title="${oDoc.name}">
	             去找找
			   </button>
			   <span id='searchmsg' class="alert alert-info  btn-xs hidden">已派小猫去寻找下载地址,请耐心等待。5分钟后再刷新页面查看。</span>
		    </#if>
	        <#list oTorrents as oTor>
	            <div id="tor${oTor_index}" class="torblock" >
	              <#if (oTor.type == 'bttiantang-torrent') >
				    <form id="form${oTor_index}" action="/movie/download">
					    <a href="${oTor.url}" >
				        	 <strong>${((oTor.name)?length>0)?string((oTor.name),(oDoc.name))}</strong>
						</a>
					    <input class="btn btn-default" type="hidden" name="u" value="${oTor.url}">
					    <input class="btn btn-default" type="hidden" name="m" value="${oTor.method}">
					    <input class="btn btn-default" type="hidden" name="p" value="${oTor.param}">
					    <input class="btn btn-default" type="hidden" name="n" value="${oTor.name}">
					    <button type="submit" class="btn btn-success btn-xs downbtn" >
					       种子下载
					    </button>
					 </form>
				  <#else>
				    <a href="${oTor.url}" >
				       <strong>${unifyOf(((oTor.name)?length>0)?string((oTor.name),(oDoc.name)),70,'.')}</strong>
				   </a>
				   <button type="button" class="btn btn-success btn-xs downbtn" onclick='window.location.href=urlcodesc.encode("${oTor.url}","thunder");'>
				     迅雷下载
				   </button>
				  </#if>
	            </div>
	        </#list>
	    </li>
		</div>
	</div>
	
	<script src="/assets/js/custom/urlcodesc.js?v=${version}"></script>
	<script src="/assets/js/custom/detail.js?v=${version}"></script>
</body>
</html>