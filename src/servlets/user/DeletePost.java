package servlets.user;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import models.Post;
import models.PostDAO;
import models.exceptions.SecurityBreachException;

/**
 * Servlet implementation class DeletePost
 */
@WebServlet("/user/deletepost")
public class DeletePost extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DeletePost() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {

			Subject currentUser = SecurityUtils.getSubject();

			if (!currentUser.isAuthenticated()) throw new SecurityBreachException();

			String id = request.getParameter("id");

			Post p = new Post();
			p.setIdString(id);

			PostDAO dao = new PostDAO();

			if (!(dao.checkIfCreator(p.getId(), currentUser.getPrincipal().toString()) || currentUser.hasRole("admin")))
				throw new SecurityBreachException();

			if (dao.checkIfDeleted(p.getId())) {
				response.getWriter().print("deleted");
			} else if (dao.delete(p)) {
				response.getWriter().print("success");
				System.out.println("Hooray Post Created!");
			} else {
				response.getWriter().print("fail");
			}

		} catch (SecurityBreachException e) {
			response.sendRedirect("error.jsp");
		} catch (Exception e) {
			response.sendRedirect("error.jsp");
		}

	}

}
