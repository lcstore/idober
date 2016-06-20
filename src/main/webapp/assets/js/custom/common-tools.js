$(document).ready(function() {
	$('img').error(function(){
	       $(this).attr('src', '/assets/img/noimg220x220.jpg');
	       $(this).error = null;
	});
});