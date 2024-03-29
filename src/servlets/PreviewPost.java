package servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import models.Post;
import utilities.BBCodeProccessor;
import utilities.ValidImageChecker;

/**
 * Servlet implementation class PreviewPost
 */
@WebServlet("/previewpost")
public class PreviewPost extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PreviewPost() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String post = request.getParameter("post");
		if (post != null) {
			if (ValidImageChecker.checkUsingRegex(Post.ImagePattern, post)) {
				response.getWriter().print("post " + BBCodeProccessor.process(post));
			} else {
				response.getWriter().print("image");
			}
		}
	}

}
