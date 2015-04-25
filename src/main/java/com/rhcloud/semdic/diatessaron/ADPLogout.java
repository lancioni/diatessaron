package com.rhcloud.semdic.diatessaron;

import java.io.IOException;
import java.util.ArrayList;

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
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;

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
    	@SuppressWarnings("unchecked")
		MongoCollection<Document> diacoll = (MongoCollection<Document>) ctx.getAttribute("diacoll");
		BasicDBObject query=new BasicDBObject("_id","1").append("verses._id", "1");
		DBObject projection = (DBObject) JSON.parse("{'verses.$':'1'}");
		FindIterable<Document> f = diacoll.find(query).projection((Bson) projection);
		Document First = f.first();
		@SuppressWarnings("unchecked")
		ArrayList<Object> verses = (ArrayList<Object>) First.get("verses");
		Object verse = verses.get(0);
		System.out.println(verse.toString());
		System.out.println(((Document) verse).get("text"));
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