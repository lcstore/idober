<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <title>文章创建</title>
    
    <link href="/assets/css/article.css" type="text/css" rel="stylesheet">
    <link href="/assets/ueditor/themes/default/css/ueditor.css" type="text/css" rel="stylesheet">
    <link href="//cdn.bootcss.com/bootstrap-datetimepicker/4.17.42/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet">
    <script type="text/javascript" charset="utf-8" src="/assets/ueditor/ueditor.config.js"></script>
    <script type="text/javascript" charset="utf-8" src="/assets/ueditor/ueditor.all.js"></script>
    <script type="text/javascript" src="/assets/ueditor/lang/zh-cn/zh-cn.js"></script>
    <script type="text/javascript" src="/assets/js/custom/article.js"></script>
    
    <style>
      body.home {
		    background-color: #dde1e5;
		    padding-top: 100px;
	 }
    </style>
</head>
<body>
<div class="container" >
     <div class="article-body">
        <div class="article-header well">
	       <form target="_blank" action="${qAction!'/search/movie'}"   role="form" id="saveForm">
	         <div class="form-group row">
				  <div class="col-sm-4">
				    <div class="input-group">
				      <div class="input-group-addon">标题</div>
				      <input class="form-control" type="text" name="title" placeholder="文章标题">
				    </div>
				  </div>
				  <div class="col-sm-3">
				    <div class="input-group">
				      <div class="input-group-addon">来源</div>
				      <input class="form-control" type="text" name="source" placeholder="文章来源" value="狸猫资讯">
				    </div>
				  </div>
				  <div class="col-sm-3">
				    <div class="input-group">
				      <div class="input-group-addon">作者</div>
				      <input class="form-control" type="text" name="author"  placeholder="文章作者"  value="狸猫">
				    </div>
				  </div>
				  <div class="col-sm-2">
				    <div class="input-group">
				      <div class="input-group-addon">类型</div>
				      <select class="form-control" name="type"  >
						  <option value="album">专辑</option>
						  <option value="joke">笑话</option>
						  <option value="news">新闻</option>
						  <option value="movie">电影</option>
					   </select>
				    </div>
				  </div>
			  </div>
	         <div class="form-group row">
				  <div class="col-sm-4">
				    <div class="input-group">
				      <div class="input-group-addon">置顶</div>
				      <div class="stick-top">
						<label class="radio-inline col-sm-offset-2">
						  <input type="radio" name="sticktop" id="stick-top-1" value="1">是
						</label>
				        <label class="radio-inline">
						  <input type="radio" name="sticktop" id="stick-top-2" value="0" checked="checked">否
						</label>
					  </div>
				    </div>
				  </div>
				  <div class="col-sm-4">
				    <div class="input-group date" id="startPicker">
				       <input class="form-control" name="start" type="text" >
				       <span class="input-group-addon">
				         <span class="glyphicon glyphicon-calendar"></span>
                       </span>
				    </div>
				  </div>
				  <div class="col-sm-4">
				    <div class="input-group" id="endPicker">
				      <input class="form-control" name="end" type="text">
				      <span class="input-group-addon">
				         <span class="glyphicon glyphicon-calendar"></span>
                      </span>
				    </div>
				  </div>
			  </div>
			  <div class="form-group row">
				  <div class="col-sm-12">
				    <div class="input-group">
				      <div class="input-group-addon">摘要</div>
				      <textarea class="form-control" rows="2" name="abstract"></textarea>
				    </div>
				  </div>
			  </div>
			  <div class="form-group row">
				  <div class="col-sm-12">
				    <div class="input-group">
				      <div class="input-group-addon">扩展</div>
				      <textarea class="form-control" rows="2" name="extend">{"link":"","cover":""}</textarea>
				    </div>
				  </div>
			  </div>
		 </form>
		</div>
	     <div class="">
	        <script type="text/plain" id="myEditor" style="width:100%;height:360px;">
	          <div><p>这里我可以写一些输入提示1</p></div>
	          <div><p>这里我可以写一些输入提示2</p></div>
	        </script>
	    </div>
	    <div class="article-header well">
	      <div class="row">
	       <div class="col-sm-3 col-md-3">
	        <button type="button" class="btn btn-success btn-xs " onclick="saveArticle(this)">保存</button>
	       </div>
	       <div class="col-sm-3 col-md-3">
	       <button type="button" class="btn btn-info btn-xs" onclick="previewArticle()">预览</button>
	        </div>
	       <div class="col-sm-6 col-md-6">
	       <button type="button" class="btn btn-danger btn-xs" onclick="saveAndRelease()">保存并发布</button>
	        </div>
     	  </div>
     	</div>
     </div>
</div>

<script type="text/javascript">
    function saveArticle(ele) {
       console.log("saveArticle");
       var errCount=0;
       var sExtend = $('#saveForm textarea[name=extend]').first().val();
       var oParam = $.parseJSON(sExtend) || {}; 
       oParam.abstract = $('#saveForm textarea[name=abstract]').first().val();
       oParam.type = $('#saveForm select[name=type] option:selected').first().val();
       $("#saveForm  div.form-group input[type=text][name],#saveForm  div.form-group input[type=radio][name]:checked").each(function(){
          var oEle = $(this);
          var sName = oEle.attr('name');
          var sVal = oEle.val() || '';
          sVal =  sVal.replace(/^\s+|\s+$/g,"");
          if(sVal){
              if('start'==sName || 'end'==sName){
	            sVal = moment(sVal,'YYYY-MM-DD HH:ss').format('YYYY-MM-DD[T]HH:ss:00.000[Z]');
	          }
          }else {
            if(/form-control/.test(oEle.attr('class'))){
                var oParentEle = oEle.parent();
	            var sCls = oParentEle.attr('class') || '';
	            sCls = sCls.replace(/has-error|has-success|has-warning/gm,'');
	            oParentEle.attr('class',sCls +' has-error');
            }
            errCount++;
          }
          oParam[sName]=sVal.trim();
           console.log("input "+sName+'='+sVal);
       });
       var sContent = UE.getEditor('myEditor').getContent();
       sContent = sContent.replace(/"Powered by 135editor.com"|_135editor|135编辑器/gm,'');
       oParam['content']=sContent;
       console.log("oParam:"+JSON.stringify(oParam));
       if(errCount==0){
		   	$.ajax({
				type : 'POST',
				url : '/article/save.action',
				dataType : 'json',
				contentType : 'application/json',
				data : JSON.stringify(oParam)
			}).done(function(oBack) {
				console.log('data:' + JSON.stringify(oBack));
			}).fail(function(data) {
				// window.location.reload();
			})
       }

    }
    function previewArticle(){
    }
</script>
<script type="text/javascript" src="//cdn.bootcss.com/moment.js/2.15.1/moment.min.js"></script>
<script type="text/javascript" src="//cdn.bootcss.com/bootstrap-datetimepicker/4.17.42/js/bootstrap-datetimepicker.min.js"></script>
</body>
</html>