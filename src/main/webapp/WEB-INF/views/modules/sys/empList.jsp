<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
	.width{width: 100px;}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sys/user/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sys/emp/list");
			$("#searchForm").submit();
	    	return false;
	    }
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/sys/emp/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			<a href="${ctx}/sys/emp/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/emp/emplist">员工列表</a></li>
		<li><a href="${ctx}/sys/emp/empform">员工添加</a></li>	
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/emp/emplist" method="post" class="breadcrumb form-search ">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<sys:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
		<ul class="ul-form">
			<li><label>工&nbsp;&nbsp;&nbsp;号：</label><form:input path="no" htmlEscape="false" maxlength="50" class="input-medium width"/></li>
			<li><label>姓&nbsp;&nbsp;&nbsp;名：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-medium width"/></li>
			<%-- <li><label>审核状态：</label><form:input path="status" htmlEscape="false" maxlength="50" class="input-medium width"/></li> --%>
			<li>
				<label>员工状态：</label>
<%-- 				<form:input path="status" htmlEscape="false" maxlength="50" class="input-medium width"/>
 --%>				<form:select path="status" class="input-xlarge width">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('emp_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>一级部门：</label><sys:treeselect id="company" name="company.id" value="${user.company.id}" labelName="company.name" labelValue="${user.company.name}" 
				title="公司" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/></li>
			<li><label>二级部门：</label><sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}" 
				title="部门" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
				<!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				<input id="btnImport" class="btn btn-primary" type="button" value="导入"/></li> -->
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th>序号</th>
			<th>工号</th>
			<th class="sort-column name">姓名</th>
			<th>部门</th>
			<th>录入时间</th>
			<th>类型</th>
			<th>员工状态</th>
			<th>注册照片</th>
			<th>照片授权状态</th>
			<!-- <th class="sort-column login_name">登录名</th>
			<th>电话</th>
			<th>手机</th>
			<th>角色</th> -->
			<shiro:hasPermission name="sys:user:edit">
			<th>操作</th></shiro:hasPermission>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" varStatus="status"  var="user">
			<tr>
				<td>${status.count}</td>
				<td>${user.no}</td>
				<td>${user.name}</td>
				<td>${user.company.name}&nbsp;/&nbsp;${user.office.name}</td>
				<td><fmt:formatDate value="${user.createDate}" type="both" dateStyle="full"/></td>
				<td>${fns:getDictLabel(user.userType, "sys_user_type", "")}</td>
				<td>${fns:getDictLabel(user.status, "emp_status", "")}</td>
				<td><img style="width: 50px;height: 50px;" src="${user.photo}"/></td>
				<td>${fns:getDictLabel(user.authPhone, "photo_status", "")}</td>
				<%-- <td><a href="${ctx}/sys/user/form?id=${user.id}">${user.loginName}</a></td>
				<td>${user.phone}</td>
				<td>${user.mobile}</td>
				<td>${user.roleNames}</td> --%>
				<shiro:hasPermission name="sys:user:edit"><td>
    				<a href="${ctx}/sys/emp/empform?id=${user.id}">修改</a>
    				<a href="javaScript:;">授权</a>
    				<c:if test="${user.status eq '0'}">
    					<a href="${ctx}/sys/emp/audit?id=${user.id}">审核</a>
    				</c:if>
					<a href="${ctx}/sys/user/delete?id=${user.id}" onclick="return confirmx('确认要删除该用户吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>