$(document).ready(function() {
	$.ajax({
		url : '/dbms/loadObject',
		data : {
			schema : root.text,
			objectType : node.id,
			objectName : node.text
		},
		dataType : 'json',
		success :  function(data) {
			console.log(data);
			var cols = []
			var obj = data.columns;
			for (var value in obj) {
				var menuItem = {
					field: obj[value],
					title: obj[value],
					width: 100,
					align: 'center'
				};
				cols.push(menuItem);
			} 
			$('#dbmsDatagrid').datagrid({
				title : node.text,
				columns : [cols],
				data : data.data
			});
			if(node.id == 'TABLE' && node.children == null) {
				$('#dbmsTree').tree('append', {
					parent : node.target,
					data : data.children
				});
			}
		} 
	});
});