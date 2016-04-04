$(document).ready(function() {
		$("#searchMovie").click(function(e) {
			var self = $(this);
			self.attr('disabled',"disabled");
			var title = self.attr('title');
			var location = window.location;
			var sBase = window.location.origin;
			var sUrl = sBase+"/movie/fetch";
			console.log('sUrl:'+sUrl+',title:'+title)
			$.getJSON(sUrl, {
				'title' : encodeURI(title)
			}, function(result) {
				if (result && result.statusVo && result.statusVo.code==200) {
					var omsg = $("#searchmsg");
					var sNewCls = omsg.attr('class').replace(/\shidden/,'');
					omsg.attr('class',sNewCls);
				}
			});
		});
});
