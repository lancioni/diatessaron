package com.rhcloud.semdic.diatessaron;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

/**
 * Servlet implementation class ADPLogout
 */
@WebServlet("/ADPLogout")
public class ADPLogout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext ctx = null;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	      ctx = config.getServletContext(); 
	}
       
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	response.setContentType("text/html");
    	MongoCollection<Document> diacoll = (MongoCollection<Document>) ctx.getAttribute("diacoll");
    	System.out.println("CTX: " + diacoll.toString());
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("JSESSIONID")){
    			System.out.println("JSESSIONID="+cookie.getValue());
    		}
    		cookie.setMaxAge(0);
    		response.addCookie(cookie);
    	}
    	}
    	//invalidate the session if exists
    	HttpSession session = request.getSession(false);
    	System.out.println("User="+session.getAttribute("user"));
    	if(session != null){
    		session.invalidate();
    	}
    	//no encoding because we have invalidated the session
		//RequestDispatcher rd = ctx.getRequestDispatcher("/index.jsp");
		//PrintWriter out= response.getWriter();
		request.removeAttribute("error");
		//out.println("<font color=red>Either user name or password is wrong.</font>");
		//rd.include(request, response);
    	//request.setAttribute("error", "Unknown login, please try again."); // Set error.
        request.getRequestDispatcher("index.jsp").forward(request, response); // Forward to same page so that you can display error.
    	//response.sendRedirect("index.jsp");
    }

}