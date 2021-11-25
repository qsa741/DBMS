<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<script>
	$('#schemaDetails').tabs({
	    border:false
	});
	
	$(document).ready(function() {
		if(${sessionScope.JYDBID eq null}) {
			alert('세션이 만료되었습니다. 로그인이 필요합니다.');
			window.location='/dbms/signOut';
		} else {
			$('#schemaDetailsInfo').datagrid({
				url : '/dbms/schemaDetailsInfo?schema=' + $('#dbmsTree').tree('getSelected').text
			});
			$('#schemaDetailsRoleGrants').datagrid({
				url : '/dbms/schemaDetailsRoleGrants?schema=' + $('#dbmsTree').tree('getSelected').text
			});
			$('#schemaDetailsSystemPrivileges').datagrid({
				url : '/dbms/schemaDetailsSystemPrivileges?schema=' + $('#dbmsTree').tree('getSelected').text
			});
		}
	});
</script>
<div id="schemaDetails" class="easyui-tabs">
	<div class="tab" title="Info" style="display:none;">
    	<table id="schemaDetailsInfo" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'PARAMETER'">Parameter</th>
    				<th data-options="field:'VALUE'">Value</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div class="tab" title="Role Grants" style="display:none;">
        <table id="schemaDetailsRoleGrants" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'GRANTED_ROLE'">Role</th>
    				<th data-options="field:'ADMIN_OPTION'">Admin</th>
    				<th data-options="field:'DEFAULT_ROLE'">Default Role</th>
    				<th data-options="field:'GRANTEE'">Grantee</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div class="tab" title="System Privileges" style="display:none;">
         <table id="schemaDetailsSystemPrivileges" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'PRIVILEGE'">Privileage</th>
    				<th data-options="field:'ADMIN_OPTION'">Admin</th>
    				<th data-options="field:'GRANTEE'">Grantee</th>
    				<th data-options="field:'TYPE'">Type</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div class="tab" title="Object Grants" style="display:none;">
         <table id="schemaDetailsObjectGrants" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'privileage'">Privileage</th>
    				<th data-options="field:'admin'">Admin</th>
    				<th data-options="field:'grantee'">Grantee</th>
    				<th data-options="field:'grantor'">Grantor</th>
    				<th data-options="field:'owner'">Owner</th>
    				<th data-options="field:'objectType'">Object Type</th>
    				<th data-options="field:'objectName'">Object Name</th>
    				<th data-options="field:'columnName'">Column Name</th>
    				<th data-options="field:'type'">Type</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div class="tab" title="Objects" style="display:none;">
         <table id="schemaDetailsObjects" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'tablespace'">Tablespace</th>
    				<th data-options="field:'objectName'">Object Name</th>
    				<th data-options="field:'objectType'">Object Type</th>
    				<th data-options="field:'headerFile'">Header File</th>
    				<th data-options="field:'headerBlock'">Header Block</th>
    			</tr>
    		</thead>
    	</table>
    </div>
    <div class="tab" title="Extents" style="display:none;">
         <table id="schemaDetailsExtents" class="easyui-datagrid" data-options="singleSelect:'true'">
    		<thead>
    			<tr>
    				<th data-options="field:'tablespace'">Tablespace</th>
    				<th data-options="field:'segmentName'">Segment Name</th>
    				<th data-options="field:'objectName'">Object Name</th>
    				<th data-options="field:'fileId'">File ID</th>
    				<th data-options="field:'blockId'">Block ID</th>
    				<th data-options="field:'blocks'">#Blocks</th>
    			</tr>
    		</thead>
    	</table>
    </div>
</div>