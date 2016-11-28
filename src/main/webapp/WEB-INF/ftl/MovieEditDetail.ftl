<!DOCTYPE html>
<html lang="zh-cn">
<head>
<#if (oDoc.id)?exists>
<link rel="canonical" href="http://www.lezomao.com/movie/detail/${oDoc.id}.html"/>
</#if> 
<#assign oDirectors = (oDoc.directors![])>
<#assign oActors = (oDoc.actors![])>
<#assign oRegions = (oDoc.regions![])>
<#assign oGenres = (oDoc.genres![])>
<#assign oTorrents = (oDoc.torrents![])>
<#assign oShares = (oDoc.shares![])>
<#assign oCrumbs = (oDoc.crumbs![])>
<#assign oFeeds = (oDoc.feeds![])>
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
						  <div class="col-xs-12 col-sm-4 col-md-3 movie-left">
						       <img src="${oDoc.image}" class="img-thumbnail movie-img" alt="${oDoc.name}"/>
					      </div>
					      <div class="col-xs-12 col-sm-8 col-md-9 movie-right">
							      <ul class="list-group">
								    <li class="list-group-item">
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
								    <#if (oShares?size>0) > 
								        <li class="list-group-item"><strong>云盘：</strong>
								          <#list oShares as sShare>
								                <#assign oShare = ((sShare)!"{}")?eval>
									        	  <#if (oShare.level="uhd" || oShare.level="hd" || oShare.level="sd" || oShare.level="bd") > 
									        	  <span class="cer-box cer-${oShare.level}">
									        	  <#else>
									        	  <span class="cer-box cer-normal">
									        	  </#if>
									        	    <a href="${oShare.url}" rel="nofollow" target="_blank" class="noline">${oShare.name}
									        	    </a>
									        	  	<#if oShare.secret?? > 
									        	    <span>(密码: ${oShare.secret})</span>
									        	    </#if>
									        	  </span>
									        
									        	  <#if  (sShare_has_next) > 
									        	   <span>&nbsp; </span>
									        	  </#if>
								          </#list>
								        </li>
									</#if> 
								   <li class="list-group-item tag-info">
								     <strong>IMDb：</strong>
								     <a class="imdb" rel="nofollow" target="_blank" href="http://www.imdb.com/title/tt${oDoc.imdb_s}">tt${oDoc.imdb_s}</a>
								     <strong>豆瓣：</strong>
								     <a class="douban" rel="nofollow" target="_blank" href="https://movie.douban.com/subject/${oDoc.code_s}/">${oDoc.code_s}</a>
								   </li>
								  </ul>
					       </div>
				      </div>
				</div>
			</div>
		</div>
		<div class="container cmd-box">
		  <ul class="list-group">
		    <li class="list-group-item cmd-item">
		     <button class="btn btn-info btn-xs edit-search-btn" type="button" action="edit-search" name="${oDoc.name}" >
			   寻找种子 <span class="badge">${oDoc.src_count?string("0")}</span>
			 </button>
			 <button class="btn btn-warning btn-xs edit-search-btn" type="button" action="update-detail" name="${oDoc.code_s}">
			   更新信息
			  </button>
			 <button class="btn btn-danger btn-xs edit-search-btn" type="button" action="deploy"">
			   发布上线
			  </button>
		    </li>
		  </ul>
		</div>
		<div class="container top-margin">
		   <div class="story">
				<div class="story-header">
					  <h4 class="head-title">剧情介绍
					    <span class="share-box">分享到 <a id="shareQQ" rel="nofollow" target="_blank" href="javascript:void(0)" ></a></span>
					  </h4>
					  
					 
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
	              <#if (oTor.type?ends_with("-torrent")) >
				    <form id="form${sTor_index}" action="/movie/download">
					    <span >
				        	 <strong>${((oTor.name)?length>0)?string((oTor.name),(oDoc.name))}</strong>
						</span>
					    <input class="btn btn-default" type="hidden" name="u" value="${EncodeURL(oTor.url)}">
					    <#if oTor.method?contains("POST")>
					      <input class="btn btn-default" type="hidden" name="m" value="P">
					    </#if> 
					    <input class="btn btn-default" type="hidden" name="p" value="${EncodeURL(oTor.param)}">
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
		
		<div class="container feed-box">
		  <ul class="list-group feed-list">
		  <#list oFeeds as oFeed>
		    <li class="list-group-item feed-item">
		      <div class="tor-box">
		        <h3>
				<span>(${oFeed.code_s})</span>
				  <a target="_blank" href="${oFeed.url_rs}">${oFeed.url_rs}</a>
				  <#if oFeed.secret_rs??>
				  <span>密码：${oFeed.secret_rs}</span>
				  </#if>
				</h3>
		        <span>类型:${oFeed.type}</span>
		        <span>来源：${oFeed.source_name_txt}</span>
		        <span>大小：${oFeed.size_tl!'未知'}</span>
		        <span>更新：${oFeed.date_tdt?string("yyyy-MM-dd HH:mm")}</span>
			  </div>
		      <div class="cmd-box" code="${oFeed.code_s}">
		      <div class="row">
			      <div class="col-lg-3">
			       <div class="input-group">
					  <span class="input-group-addon">链接</span>
					  <input type="text" class="form-control" name="url"  value="${oFeed.url_rs}">
				   </div>
				  </div>
			      <div class="col-lg-2">
			       <div class="input-group">
					  <span class="input-group-addon">密码</span>
					  <input type="text" class="form-control" name="secret"  value="${oFeed.secret_rs}">
				   </div>
				  </div>
			      <div class="col-lg-2">
			       <div class="input-group">
					  <span class="input-group-addon">大小</span>
					  <input type="text" class="form-control" name="size"  value="${oFeed.size_tl}">
				    </div>
				  </div>
			      <div class="col-lg-5">
			       <div class="input-group">
			          <#assign oNameArr = (oFeed.title?split('@>'))>
					  <input type="text" class="form-control" name="name"  value="百度云:${oNameArr[0]}">
					  <span class="input-group-addon">
				        <input type="checkbox" name="cover" >
				      </span>
					  <span class="input-group-addon">
					　　  <select class="select-level" >
					　　         <option value="uhd">超清</option>
					　　         <option value="hd" selected ="selected">高清</option>
					　　         <option value="sd">标清</option>
					　　         <option value="bd">枪版</option>
					　　         <option value="">未知</option>
					　　   </select>
					  </span>
					  <span class="input-group-addon">
					    <a href="javascript:void(0)" class="add-tor" >添加</a>
					  </span>
					</div>
				  </div>
			  </div>
			  </div>
		      <div class="txt-box">
		        <h4>
				 <a target="_blank" href="${oFeed.source_url_rs}">${oFeed.title}</a>
				</h4>
				<p class="txt-info">${oFeed.content}</p>
			  </div>
		    </li>
		  </#list>
		  </ul>
		</div>
	</div>
	<input type="hidden" name="id" value="${oDoc.id}">
	<script src="${static_host}/assets/js/custom/urlcodesc.js?v=${version}"></script>
	<script src="${static_host}/assets/js/custom/detail.js?v=${version}"></script>
    	<script>
		$(function () {
		    $('button[action="edit-search"]').one('click', function(e) {
				var self = $(this);
				self.attr('disabled',"disabled");
				var sName = self.attr('name');
				var nameArr = [];
				nameArr.push(sName);
				var oParam = {
					names : nameArr
				};
				$.ajax({
					type : 'POST',
					url : '/movie/edit/search',
					dataType : 'json',
					contentType : 'application/json',
					data : JSON.stringify(oParam)
				}).done(function(oBack) {
					console.log('data:' + JSON.stringify(oBack));
				}).fail(function(data) {
				})
			});
		    $('button[action="update-detail"]').one('click', function(e) {
				var self = $(this);
				self.attr('disabled',"disabled");
				var sCode = self.attr('name');
				var codeArr = [];
				codeArr.push(sCode);
				var oParam = {
					ids : codeArr
				};
				$.ajax({
					type : 'POST',
					url : '/movie/edit/detail',
					dataType : 'json',
					contentType : 'application/json',
					data : JSON.stringify(oParam)
				}).done(function(oBack) {
					console.log('data:' + JSON.stringify(oBack));
				}).fail(function(data) {
				})
			});
		    $('button[action="deploy"]').one('click', function(e) {
				var self = $(this);
				self.attr('disabled',"disabled");
				var sCode = $('input[name="id"]').val();;
				var codeArr = [];
				codeArr.push(sCode);
				var oParam = {
					ids : codeArr
				};
				$.ajax({
					type : 'POST',
					url : '/movie/edit/deploy',
					dataType : 'json',
					contentType : 'application/json',
					data : JSON.stringify(oParam)
				}).done(function(oBack) {
					console.log('data:' + JSON.stringify(oBack));
				}).fail(function(data) {
				})
			});
		    $('input[name="cover"][type="checkbox"]').on('click', function(e) {
				var self = $(this);
				var cmdEle= self.parents('div.cmd-box[code]');
				var checked = self.prop('checked');
				var btnELe = cmdEle.find('a.add-tor');
				if(checked){
				  btnELe.text('覆写');
				}else {
				  btnELe.text('添加');
				}
			});
		    $('a.add-tor[href]').on('click', function(e) {
				var self = $(this);
				var cmdEle= self.parents('div.cmd-box[code]');
				var code = cmdEle.attr('code');
				var checked = cmdEle.find('input[name="cover"]').prop('checked');
				var name = cmdEle.find('input[name="name"]').val();
				var url = cmdEle.find('input[name="url"]').val();
				var secret = cmdEle.find('input[name="secret"]').val();
				var size = cmdEle.find('input[name="size"]').val();
				var level = cmdEle.find('select.select-level option:selected').val();
				secret = secret?secret:null;
				size = size?size:'-1';
				size = size.trim();
				if(/([0-9\.]+)G$/im.test(size)){
				   size=RegExp.$1;
				   size*=1<<30;
				} else if(/([0-9\.]+)M$/im.test(size)){
				   size=RegExp.$1;
				   size*=1<<20;
				} else if(/([0-9\.]+)K$/im.test(size)){
				   size=RegExp.$1;
				   size*=1<<10;
				}else {
				   size -=0;
				}
				var oParam = {};
				oParam.id = $('input[name="id"]').val();
				oParam.code = code;
				oParam.cover = checked;
				oParam.name = name;
				oParam.url = url;
				oParam.secret = secret;
				oParam.size = size;
				oParam.level = level;
				console.log('oParam:'+JSON.stringify(oParam));
				$.ajax({
					type : 'POST',
					url : '/movie/edit/torrent',
					dataType : 'json',
					contentType : 'application/json',
					data : JSON.stringify(oParam)
				}).done(function(oBack) {
					console.log('data:' + JSON.stringify(oBack));
				}).fail(function(data) {
				})
			});
		});
		</script>

</body>
</html>