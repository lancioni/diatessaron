import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Uploads
 */
@WebServlet("/uploads")
public class JarTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private String message;

	  public void init() throws ServletException
	  {
	      // Do required initialization
	      message = "مرحبًا أيها العالم! إني وصلت...";
	  }

	  public void doGet(HttpServletRequest request,
	                    HttpServletResponse response)
	            throws ServletException, IOException
	  {
	      // Set response content type
		  response.setCharacterEncoding("UTF-8");
	      response.setContentType("text/html");

	      // Actual logic goes here.
	      PrintWriter out = response.getWriter();
	      out.println("<h1 align=\"right\">" + message + "</h1>");
	  }
	  
	  public void destroy()
	  {
	      // do nothing.
	  }


}
