package com.rhcloud.semdic.diatessaron;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ADPLogin
 */
@WebServlet(description = "class that manages login to the ArabicDiatessaronProject", urlPatterns = { "/ADPLogin" })
public class ADPLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String message;
	private ServletContext ctx = null;
	private final String userID = "admin";
	private final String password = "password";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ADPLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	      message = "تسجيل الدخول";
	      ctx = config.getServletContext(); 
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		output(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		output(request, response);
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void output(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	     // get request parameters for userID and password
		String user = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		
		if(userID.equals(user) && password.equals(pwd)){
			HttpSession session = request.getSession();
			session.setAttribute("user", "Giuliano");
			//setting session to expire in 30 mins
			session.setMaxInactiveInterval(30*60);
			Cookie userName = new Cookie("user", user);
			response.addCookie(userName);
			String encodedURL = response.encodeRedirectURL("ADPLoginSuccess.jsp");
			response.sendRedirect(encodedURL);
		}else{
	    	request.setAttribute("error", "Unknown login, please try again."); // Set error.
	        request.getRequestDispatcher("index.jsp").forward(request, response); // Forward to same page so that you can display error.
		}

		/*
		// Set response content type
		  response.setCharacterEncoding("UTF-8");
	      response.setContentType("text/html");

	      // Actual logic goes here.
	      PrintWriter out = response.getWriter();
			Enumeration<String> en=request.getParameterNames();
			 
			out.println("<ul>");
			while(en.hasMoreElements())
			{
				Object objOri=en.nextElement();
				String param=(String)objOri;
				String value=request.getParameter(param);
				out.println("<li>Parameter Name is '"+param+"' and Parameter Value is '"+value+"'</li>");
			}	      
			out.println("</ul>");
			out.println("<h1 align=\"right\">" + message + "</h1>");
	      out.println("<p>username: " + request.getParameter("username") + "</p>");*/
	}

}
