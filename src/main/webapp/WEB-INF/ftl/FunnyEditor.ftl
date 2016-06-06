<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta name="description" content="狸猫资讯(LezoMao.com)，${oDoc.name}(${oDoc.year})，导演：${(oDoc.directors)?replace(';', '、')}，主演：${(oDoc.actors)?replace(';', '、')}，类型：${(oDoc.actors)?replace(';', '、')}"/>
<meta name="keywords" content="狸猫资讯、乐助猫、lezo、lezomao,${(oDoc.directors)?replace(';', '、')},${(oDoc.actors)?replace(';', '、')},${oDoc.name}(${oDoc.year}),${oDoc.enname},种子下载,迅雷下载,高清下载" />
<meta name="title" content="${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载 -  乐助猫,狸猫资讯(LezoMao.com)" />
<title>${oDoc.name}${oDoc.enname}(${oDoc.year}),种子下载,迅雷下载,高清下载-  乐助猫,狸猫资讯(LezoMao.com)</title>
</head>
<body>
  <div class="container">
   <#list model.qResponse as oDoc>
       <#assign oContent = oDoc.content?eval>
       <div class="row weui_media_row">
          <span class="weui_media_hd" href="${oDoc.source_url}" style="background-image:url(${oContent.cover})" ></span>
		  <div class="weui_media_bd">
	        <h1 href="${oDoc.source_url}" class="weui_media_title">
	          (${(oDoc_index+1)})${oDoc.title}
	        </h1>
	        <p class="weui_media_desc"></p>
	        <p class="weui_media_extra_info">${oDoc.post_date}</p>
	        <p class="weui_media_extra_info">阅读数:${oContent.read_num},点赞数:${oContent.like_num},文章长度:${oContent.html?length}</p>
	      </div>
	      <div class="weui_detail">
			    ${oContent.html}
	      </div>
	   </div>
	</#list>
	<nav>
	  <ul class="pagination">
	    <li>
	      <a href="#" aria-label="Previous">
	        <span aria-hidden="true">&laquo;</span>
	      </a>
	    </li>
	    <li><a href="#">1</a></li>
	    <li><a href="#">2</a></li>
	    <li><a onclick="">3</a></li>
	    <li><a href="#">4</a></li>
	    <li><a href="#">5</a></li>
	    <li>
	      <a href="#" aria-label="Next">
	        <span aria-hidden="true">&raquo;</span>
	      </a>
	    </li>
	  </ul>
	</nav>
  </div>
</body>
</html>