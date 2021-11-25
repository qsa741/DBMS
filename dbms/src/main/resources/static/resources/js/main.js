$(document).ready(function(){
	$('#sessionCheck').click(function() {
		$.ajax({
			url : 'http://10.47.39.102:8080/dbms/sessionCheck',
			success : function(data) {
				console.log(data);
			}
		});
	});
		
	$('#kafkaSend').click(function() {
		$.ajax({
			url : '/dbms/kafkaTest',
			data: {
				msg : 'TESTEST'
			},
			dataType : 'text',
			success : function(data) {
				alert(data);
				console.log(data);
			},
			error : function(e) {
				console.log(e);
			}
		});
	});
});