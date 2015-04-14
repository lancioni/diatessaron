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

/**
 * Servlet implementation class ADPLogin
 */
@WebServlet(description = "class that manages login to the ArabicDiatessaronProject", urlPatterns = { "/ADPLogin" })
public class ADPLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String message;
	private ServletContext ctx = null;
       
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
	      out.println("<p>username: " + request.getParameter("username") + "</p>");
	}

}
