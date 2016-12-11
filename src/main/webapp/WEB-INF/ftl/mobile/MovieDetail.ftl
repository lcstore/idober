<!DOCTYPE html>
<html lang="zh-cn">
<head>
<#if (oDoc.id)?exists>
<link rel="canonical" href="http://www.lezomao.com/movie/detail/${oDoc.id}.html"/>
</#if> 
<#assign copyright_info ="非常抱歉，由于版权问题，该资源已下架/(ㄒoㄒ)/~~">
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
					<div class="row">
						  <div class="detail-img-box col-xs-4 col-md-3">
						       <img src="${oDoc.image}" class="img-thumbnail movie-img" alt="${oDoc.name}"/>
					      </div>
					      <div class="detail-tag-box col-xs-8  col-md-9">
							      <ul class="list-group">
								    <li class="list-group-item tag-info">
										    <h1 class="movie-title-margin">
											  ${oDoc.name}
										      <#if ((oDoc.name?length+oDoc.enname?length)<=40) > 
										       <small>${oDoc.enname}</small>
										      </#if>
										      <span class="badge movie-badge">${oDoc.year}</span>
										    </h1>
								    <li class="list-group-item tag-info"><strong>导演：</strong>${oDirectors?join(' ')}</li>
								    <li class="list-group-item tag-info"><strong>主演：</strong>${oActors?join(' ')}</li>
								    <li class="list-group-item tag-info"><strong>类型：</strong>${oGenres?join(' ')}</li>
								    <li class="list-group-item tag-info"><strong>上映：</strong>
								        <#list oRegions as region>
								          <#if (region_index==0) >
								            ${region}(${oDoc.release?string("yyyy-MM-dd")})
								         <#else>
								            &nbsp${region}
					 					  </#if>	
								        </#list>
								    </li>
								    <li class="list-group-item tag-info"><strong>分享：</strong>
								      <span class="share-box"><a id="shareQQ" rel="nofollow" target="_blank" href="javascript:void(0)" ></a></span>
								    </li>
								    
								   
								  </ul>
					       </div>
				      </div>
				</div>
			</div>
		</div>
		<div class="container top-margin">
		   <div class="story">
				<div class="story-header">
					<h4 class="head-title">剧情介绍</h4>
				</div>
				<div class="story-body">
				  <p>${oDoc.story}</p>
				 </div>
			</div>
		</div>
		<#if (oShares?size>0) > 
	    <div class="container top-margin">
			 <ul class="list-group">
			  <li class="list-group-item">
			    <h4 class="list-group-item-heading">云盘资源</h4>
			  </li>
			  <#if (oDoc.copyright_s?? && oDoc.copyright_s=0)>
	             <#assign oShares = ([])>
	          </#if>
			  <#list oShares as sShare>
	                <#assign oShare = ((sShare)!"{}")?eval>
	                 <li class="list-group-item">
		        	  <#if (oShare.level="uhd" || oShare.level="hd" || oShare.level="sd" || oShare.level="bd") > 
		        	  <span class="cer-${oShare.level}"></span>
		        	  <#else>
		        	   <span class=""></span>
		        	  </#if>
		        	    <a href="${oShare.url}" rel="nofollow" target="_blank" class="noline">${oShare.name}
		        	    </a>
		        	  	<#if oShare.secret?? > 
		        	     <span>&nbsp;(密码: ${oShare.secret})</span>
		        	    </#if>
		        	  </span>
		        	 </li>
	          </#list>
			</ul>
		</div>
		</#if> 
		
		<div class="container top-margin">
		<div class="list-group">
		    <span class="list-group-item"> <h4 class="list-group-item-heading">下载资源</h4> </span>
		    <#if oTorrents?size gt 0 >
		          <#if (oDoc.copyright_s?? && oDoc.copyright_s=0)>
		             <div class="alert alert-info share-copyright">${copyright_info}</div>
		             <#assign oTorrents = ([])>
		          </#if>
		      	 <#list oTorrents as sTor>
	              <#assign oTor = ((sTor)!"{}")?eval>
	              <#if (oTor.type == 'bttiantang-torrent') >
	              <a href="javascript:void(0)" rel="nofollow" class="list-group-item" >
				     <span class="badge">种子</span>
				     <form id="form${sTor_index}" action="/movie/download">
					    <span >
				        	 <strong>${((oTor.name)?length>0)?string((oTor.name),(oDoc.name))}</strong>
						</span>
					    <input class="btn btn-default" type="hidden" name="u" value="${oTor.url}">
					    <input class="btn btn-default" type="hidden" name="m" value="${oTor.method}">
					    <input class="btn btn-default" type="hidden" name="p" value="${oTor.param}">
					    <input class="btn btn-default" type="hidden" name="n" value="${oTor.name}">
					 </form>
				  </a>
				  <#else>
				    <a href="javascript:void(0)" rel="nofollow" class="list-group-item" onclick='window.location.href=urlcodesc.encode("${oTor.url}","thunder");' >
				      <strong>${unifyOf(((oTor.name)?length>0)?string((oTor.name),(oDoc.name)),70,'.')}</strong>
				      <span class="badge">迅雷</span>
				    </a>
				  </#if>
	        </#list>
		    <#else>
		    <span class="list-group-item">  
		      <button id="searchMovie" type="button" class="btn btn-warning btn-xs find-btn" title="${oDoc.name}">
	             去找找
			   </button>
			   <span id='searchmsg' class="alert alert-info  btn-xs hidden"></span>
		    </span>
		    </#if>
	    </div>
		</div>
	</div>
	
	<script src="${static_host}/assets/js/custom/urlcodesc.js?v=${version}"></script>
	<script src="${static_host}/assets/js/custom/detail.js?v=${version}"></script>


</body>
</html>