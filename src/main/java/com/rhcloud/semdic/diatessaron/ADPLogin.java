package com.rhcloud.semdic.diatessaron;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;

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
    MongoClient mongo;
    MongoDatabase mongoDB;
    private String host;
    private String sport;
    private String db;
    String dbuser;
    String dbpwd;
    int port;
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
	@SuppressWarnings("deprecation")
	public void init(ServletConfig config) throws ServletException {
	      message = "تسجيل الدخول";
	      ctx = config.getServletContext();
	      host = System.getenv("OPENSHIFT_MONGODB_DB_HOST");
	      sport = System.getenv("OPENSHIFT_MONGODB_DB_PORT");
	      db = System.getenv("OPENSHIFT_APP_NAME");
	      dbuser = System.getenv("OPENSHIFT_MONGODB_DB_USERNAME");
	      dbpwd = System.getenv("OPENSHIFT_MONGODB_DB_PASSWORD");
	      System.out.println("host=" + host);
	      System.out.println("sport=" + sport);
	      System.out.println("db=" + db);
	      System.out.println("dbuser=" + dbuser);
	      System.out.println("dbpwd=" + dbpwd);
	      port = Integer.decode(sport);
	      if (dbuser == null)
		      mongo = new MongoClient(host, port);
	      else {
		      MongoCredential credential = MongoCredential.createCredential(dbuser, db, dbpwd.toCharArray());
		      mongo = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
	      }
	      mongoDB = mongo.getDatabase(db);
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
		MongoCollection<Document> coll = mongoDB.getCollection("users");
        DBObject qryParse = (DBObject) JSON.parse("{'_id':'"
        		+ user
        		+ "',"
        		+ "'pwd':'"
        		+ pwd
        		+ "'}");
		FindIterable<Document> foundDocs = coll.find((Bson) qryParse);
		Document firstDoc = foundDocs.first();
		/*if (firstDoc != null) 
			System.out.println(firstDoc.toString());
		else
			System.out.println("invalid user or password");*/
			
		
		//System.out.println(qryWord(mongoDB, "{'english_words.word':'book'}"));
		if (firstDoc != null) {
			HttpSession session = request.getSession();
			session.setAttribute("user", user);
			//setting session to expire in 30 mins
			session.setMaxInactiveInterval(30*60);
			Cookie userName = new Cookie("user", user);
			response.addCookie(userName);
			String encodedURL = response.encodeRedirectURL("ADPLoginSuccess.jsp");
			response.sendRedirect(encodedURL);
			MongoCollection<Document> diacoll = mongoDB.getCollection("diatessaron");
			/*DBObject qryV1 = (DBObject) JSON.parse("{'_id':'1','verses._id':'1'}");
			DBObject projection = (DBObject) JSON.parse("{'verses.$':'1'}");
			FindIterable<Document> foundQryV1 = diacoll.find((Bson) qryV1).projection((Bson) projection);
			Document firstQryV1 = foundQryV1.first();
			if (firstQryV1 != null) {
				ArrayList verses = (ArrayList) firstQryV1.get("verses");
				
			}*/
			ctx.setAttribute("diacoll", mongoDB.getCollection("diatessaron"));
		}else{
	    	request.setAttribute("error", "Unknown user or password, please try again."); // Set error.
	        request.getRequestDispatcher("index.jsp").forward(request, response); // Forward to same page so that you can display error.
		}

	}
	
/*	StringBuilder qryWord(DB mdb, String qry) {
        StringBuilder strOut = new StringBuilder("");
                                   try {
        mdb.requestStart();
        DBCollection coll = mdb.getCollection("wn");
        DBObject qryParse = (DBObject) JSON.parse(qry);
        DBCursor cursorDoc = coll.find(qryParse);
        while (cursorDoc.hasNext()) {
            strOut.append(cursorDoc.next().toString());
        }
        }
                    finally {
            mdb.requestDone();
                       }
        return strOut;
}*/


}
