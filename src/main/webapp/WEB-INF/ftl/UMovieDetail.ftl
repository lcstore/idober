<!DOCTYPE html>
<html lang="zh-cn">
<head>
<#if (oDoc.id)?exists>
<link rel="canonical" href="http://www.lezomao.com/movie/detail/${oDoc.id}"/>
</#if> 
<#assign oDirectors = (oDoc.directors![])>
<#assign oActors = (oDoc.actors![])>
<#assign oRegions = (oDoc.regions![])>
<#assign oGenres = (oDoc.genres![])>
<#assign oTorrents = (oDoc.torrents![])>
<#assign oShares = (oDoc.shares![])>
<#assign oCrumbs = (oDoc.crumbs![])>
<meta name="description" content="狸猫资讯(LezoMao.com)，${oDoc.name}(${oDoc.year})，导演：${oDirectors?join('、')}，主演：${oActors?join('、')}"/>
<meta name="keywords" content="狸猫资讯、lezomao,导演：${oDirectors?join('、')}，主演：${oActors?join('、')},${oDoc.name}(${oDoc.year}),${oDoc.enname},种子下载,迅雷下载,高清下载" />
<meta name="title" content="${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载 - 狸猫资讯(LezoMao.com)" />
<title>${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载 - 狸猫资讯(LezoMao.com)</title>
</head>
<body>
	<div class="main data-box">
		<div class="act">
			<div class="container">
			<ol class="breadcrumb">
             <#list oCrumbs as oCrumb>
                  <#assign oLinks = oCrumb />
                  <#if (oCrumb_index==0) >
                     <li>
			           <#list oLinks as oLink>
		                    <#if (oLink_index==0) >
		                      <a href="${oLink.link}">${oLink.name}</a>
		                     <#else>
		                     、<a href="${oLink.link}">${oLink.name}</a>
		                     </#if>	
				        </#list>
			        </li>
                  <#else>
                    <li>
			           <#list oLinks as oLink>
		                    <#if (oLink_index==0) >
		                      <a href="${oLink.link}">${oLink.name}</a>
		                     <#else>
		                     、<a href="${oLink.link}">${oLink.name}</a>
		                     </#if>	
				        </#list>
			        </li>
			     </#if>	
	        </#list>
	        <li class="active"><strong>${oDoc.name}</strong></li>
          </ol>
				<div class="container-fluid">
					<div class="row top-margin">
						  <div class="col-md-3 movie-left">
						       <img src="${oDoc.image}" class="img-thumbnail movie-img" alt="${oDoc.name}"/>
					      </div>
					      <div class="col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item">
										    <h1 class="movie-title-margin">
											  ${oDoc.name}
										      <#if ((oDoc.name?length+oDoc.enname?length)<=40) > 
										       <small>${oDoc.enname}</small>
										      </#if>
										      <span class="badge movie-badge">${oDoc.year}</span>
										    </h1>
								    <li class="list-group-item"><strong>导演：</strong>${oDirectors?join(' ')}</li>
								    <li class="list-group-item"><strong>主演：</strong>${oActors?join(' ')}</li>
								    <li class="list-group-item"><strong>类型：</strong>${oGenres?join(' ')}</li>
								    <li class="list-group-item"><strong>上映：</strong>
								        <#list oRegions as region>
								          <#if (region_index==0) >
								            ${region}(${oDoc.release?string("yyyy-MM-dd")})
								         <#else>
								            &nbsp${region}
					 					  </#if>	
								        </#list>
								    </li>
								    <#if (oShares?size>0) > 
								        <li class="list-group-item"><strong>分享：</strong>
								          <#list oShares as sShare>
								                <#assign oShare = ((sShare)!"{}")?eval>
									        	<a href="${oShare.url}" rel="nofollow" target="_blank">${oShare.name}
									        	</a>
									        	  <#if oShare.secret?? > 
									        	  <span>(${oShare.secret})</span>
									        	  </#if>
									        	  <#if  (sShare_has_next) > 
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
		    <#if oTorrents?size gt 0 >
		      	<strong>下载地址：</strong>
		    <#else>
		        <strong>暂无地址：</strong>
	           <button id="searchMovie" type="button" class="btn btn-warning btn-xs downbtn" title="${oDoc.name}">
	             去找找
			   </button>
			   <span id='searchmsg' class="alert alert-info  btn-xs hidden"></span>
		    </#if>
	        <#list oTorrents as sTor>
	            <#assign oTor = ((sTor)!"{}")?eval>
	            <div id="tor${sTor_index}" class="torblock" >
	              <#if (oTor.type == 'bttiantang-torrent') >
				    <form id="form${sTor_index}" action="/movie/download">
					    <span >
				        	 <strong>${((oTor.name)?length>0)?string((oTor.name),(oDoc.name))}</strong>
						</span>
					    <input class="btn btn-default" type="hidden" name="u" value="${oTor.url}">
					    <input class="btn btn-default" type="hidden" name="m" value="${oTor.method}">
					    <input class="btn btn-default" type="hidden" name="p" value="${oTor.param}">
					    <input class="btn btn-default" type="hidden" name="n" value="${oTor.name}">
					    <button type="submit" class="btn btn-success btn-xs downbtn" >
					       种子下载
					    </button>
					 </form>
				  <#else>
				    <span>
				       <strong>${unifyOf(((oTor.name)?length>0)?string((oTor.name),(oDoc.name)),70,'.')}</strong>
				   </span>
				   <button type="button" rel="nofollow" class="btn btn-success btn-xs downbtn" onclick='window.location.href=urlcodesc.encode("${oTor.url}","thunder");'>
				     迅雷下载
				   </button>
				  </#if>
	            </div>
	        </#list>
	    </li>
		</div>
	</div>
	
	<script src="${static_host}/assets/js/custom/urlcodesc.js?v=${version}"></script>
	<script src="${static_host}/assets/js/custom/detail.js?v=${version}"></script>
</body>
</html>