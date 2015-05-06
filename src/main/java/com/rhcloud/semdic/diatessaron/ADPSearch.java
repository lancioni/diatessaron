package com.rhcloud.semdic.diatessaron;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;

/**
 * Servlet implementation class ADPSearch
 */
@WebServlet(description = "servlet to perform searches in the Diatessaron database", urlPatterns = { "/ADPSearch" })
public class ADPSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext ctx = null;
	private MongoCollection<Document> diacoll;
    PrintWriter out;
    JSONObject json;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ADPSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ctx = config.getServletContext();
		diacoll = (MongoCollection<Document>) ctx.getAttribute("diacoll");
		System.out.println(ctx.getAttribute("diacoll").toString());
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			output(request, response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			output(request, response);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void output(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, JSONException {
        response.setContentType("text/html; charset=utf-8");
        out = response.getWriter();
        json = new JSONObject();
        //Logs map of request parameters
        Map<String, String[]>params = request.getParameterMap();
        if (params.containsKey("loading")) {
        	populate(request, response);
        }
        else if (params.containsKey("chapter")) {
        	populate_verses(request, response, Integer.parseInt(params.get("chapter")[0]));
        }
        out.print(json);
	}
    void populate(HttpServletRequest request, HttpServletResponse response) throws JSONException {
    	
		BasicDBObject query=new BasicDBObject();
		DBObject projection = new BasicDBObject("_id", 1);
		FindIterable<Document> f = diacoll.find(query).sort(new BasicDBObject("_id", 1)) .projection((Bson) projection);
		final JSONArray outarray = new JSONArray();
		f.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        outarray.put(document.get("_id"));
		    }
		});
		json.put("loading", outarray);
    }
    void populate_verses(HttpServletRequest request, HttpServletResponse response, int chapter) throws JSONException {
    	BasicDBObject query = new BasicDBObject("_id", chapter);
    	DBObject projection = new BasicDBObject("verses._id", 1);
    	projection.put("_id",0);
    	FindIterable<Document> f = diacoll.find(query).sort(new BasicDBObject("_id", 1)).projection((Bson) projection);
		final JSONArray outarray = new JSONArray();
		f.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		    	Gson gson = new GsonBuilder().create();
		    	JsonArray verses = gson.toJsonTree(document.get("verses")).getAsJsonArray();
		        outarray.put(verses);
		    }
		});
		
		json.put("verses", outarray);
    }
}	
