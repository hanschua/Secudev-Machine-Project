<%@page import="utilities.BBCodeProccessor"%>
<%@page import="org.apache.shiro.subject.Subject"%>
<%@page import="org.apache.shiro.SecurityUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="models.PostDAO"%>
<%@page import="models.Post"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="res/css/lib/colorbox.css" />
<link rel="stylesheet" href="res/css/board.css" />
<style>
.menu {
	border-collapse: collapse;
}

.menu td {
	padding: 5px;
}
</style>
</head>
<body>
	<c:import url="WEB-INF/menu.jsp"></c:import>
	<shiro:user>
		<script src="res/js/lib/jquery-2.1.4.min.js"></script>
		<script src="res/js/lib/jquery.colorbox-min.js"></script>
		<script src="res/js/board.js"></script>
		<center>
			<H1>The Board</H1>
		</center>
		<c:import url="WEB-INF/createpost.jsp"></c:import>
		<!-- <div style="width: 96%; margin: 0 auto; text-align: right;">
		<a href="/createpost.jsp">Create Post</a>
	</div>-->
		<%
			int currentPage = 1;
				int recordsPerPage = 10;
				if (request.getParameter("page") != null) {
					try {
						currentPage = Integer.parseInt(request.getParameter("page"));
						if (currentPage < 1) throw new Exception();
					} catch (Exception e) {
						response.sendRedirect("error.jsp");
						return;
					}
				}
				PostDAO dao = new PostDAO();
				List<Post> posts = dao.getList((currentPage - 1) * recordsPerPage, recordsPerPage);
				for (Post p : posts)
					p.setInfo("post", BBCodeProccessor.process(p.getInfo("post")));
				int noOfRecords = dao.getNoOfRecords();
				int noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
				if (noOfPages < currentPage) {
					response.sendRedirect("error.jsp");
					return;
				}
				request.setAttribute("posts", posts);
				request.setAttribute("currentPage", currentPage);
				request.setAttribute("noOfPages", noOfPages);
		%>
		<c:forEach var="post" items="${posts}">
			<div class="post">
				<div class="postheader">
					<div class="postdate">
						Posted:
						<c:out value='${post.getInfo("datetime_created")}' />
					</div>
					<div class="postid">
						<a href="#">#<c:out value='${post.getInfo("id")}' /></a>
					</div>
				</div>
				<div class="postbody">
					<div class="postuserinfo">
						<a class="userlink"
							href='profile.jsp?id=${post.getInfo("username")}'> <c:out
								value='${post.getInfo("username")}' /></a>
						<div class="postuserjoineddate">
							Joined Date:
							<c:out value='${post.getInfo("datetime_joined")}' />
						</div>
					</div>
					<c:if test="${post.getInfo(\"deleted\").equals(\"false\") }">
						<c:set var="username"
							value="<%=SecurityUtils.getSubject().getPrincipal()%>" />
						<c:set var="isAdmin"
							value="<%=SecurityUtils.getSubject().hasRole(\"admin\")%>" />
						<c:set var="id" value="${post.getInfo(\"id\")}" />
						<c:if
							test="${post.getInfo(\"username\").equals(username) || isAdmin}">
							<div class="postuseroptions">
								<a class="editlink" href="editpost.jsp?id=${id}">Edit</a> <a
									class="deletelink" href="#"
									onclick="deletePost(${id}); return false;">Delete</a>
							</div>
						</c:if>
						<div class="postcontent">
							<div class="posttext">${post.getInfo("post")}</div>
						</div>
					</c:if>
					<c:if test="${post.getInfo(\"deleted\").equals(\"true\") }">
						<div class="deleted">Deleted</div>
					</c:if>
				</div>
				<c:if
					test="${post.getInfo(\"deleted\").equals(\"false\") && post.getInfo(\"datetime_lastedited\")!=null}">
					<div class="postfooter">
						<div class="postdate">
							Last Edited:
							<c:out value='${post.getInfo("datetime_lastedited")}' />
						</div>
					</div>
				</c:if>
			</div>
		</c:forEach>
		<div class="pagination">
			<div>
				<div class="pagination_n">
					<a
						<c:if test="${currentPage > 1}">href="board.jsp?page=${currentPage - 1}"</c:if>>Previous</a>
				</div>
				<c:forEach begin="1" end="${noOfPages}" var="i">
					<c:choose>
						<c:when test="${currentPage eq i}">
							<div class="pagination_n">${i}</div>
						</c:when>
						<c:otherwise>
							<div class="pagination_n">
								<a href="board.jsp?page=${i}">${i}</a>
							</div>
						</c:otherwise>
					</c:choose>
				</c:forEach>
				<div class="pagination_n">
					<a
						<c:if test="${currentPage lt noOfPages}">href="board.jsp?page=${currentPage + 1}"</c:if>>Next</a>
				</div>
			</div>
		</div>
	</shiro:user>
</body>
</html>