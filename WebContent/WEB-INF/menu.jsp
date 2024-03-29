<style>
.menu {
	border-collapse: collapse;
}

.menu td {
	padding: 5px;
}
</style>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="javax.security.auth.Subject"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<shiro:guest>
   	Hi there!  Please <a href="/login.jsp">Login</a> or <a
		href="/register.jsp">Register</a>
	today! <br />
</shiro:guest>
<shiro:user>
	<table class="menu" border="1">
		<tr>
			<td><form id="logout" method="post" action="/user/logout">
					<a href="#" onclick="document.getElementById('logout').submit();">Logout</a>
				</form></td>
			<td><a
				href="/profile.jsp?id=<%=SecurityUtils.getSubject().getPrincipal().toString()%>">View
					Profile</a></td>
			<td><a href="/user/editinfo.jsp">Edit Profile</a></td>
			<td><a href="/user/board.jsp">Board</a></td>
			<shiro:hasRole name="admin">
				<td><a href="/admin/secret.jsp">Secret Admin Page Here</a></td>
				<td><a href="/admin/backup.jsp">Secret Backups Page Here</a></td>
			</shiro:hasRole>
		</tr>
	</table>
</shiro:user>