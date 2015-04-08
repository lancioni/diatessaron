import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.jplurk.PlurkSettings;
import com.google.jplurk.exception.PlurkException;

/**
 * Servlet implementation class JarTest
 */
@WebServlet("/jartest")
public class JarTest extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private String message;

	  public void init() throws ServletException
	  {
	      // Do required initialization
	      message = "هذا هو الجارتست";
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
