import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import opennlp.ccg.grammar.*;
import opennlp.ccg.hylo.*;
import opennlp.ccg.lexicon.MorphItem;
import opennlp.ccg.lexicon.Word;
import opennlp.ccg.ngrams.StandardNgramModel;
import opennlp.ccg.parse.*;
import opennlp.ccg.realize.*;
import opennlp.ccg.synsem.*;
import opennlp.ccg.util.GroupMap;

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
	      message = "هذا هو الجارتست@!";
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
	      
	        UndirectedGraph<String, DefaultEdge> stringGraph = createStringGraph();

	        // note undirected edges are printed as: {<v1>,<v2>}
	        out.println(stringGraph.toString());
	  }
	  
	  
	  /**
	     * Craete a toy graph based on String objects.
	     *
	     * @return a graph based on String objects.
	     */
	    private static UndirectedGraph<String, DefaultEdge> createStringGraph()
	    {
	        UndirectedGraph<String, DefaultEdge> g =
	            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);

	        String v1 = "v1";
	        String v2 = "v2";
	        String v3 = "v3";
	        String v4 = "v4";

	        // add the vertices
	        g.addVertex(v1);
	        g.addVertex(v2);
	        g.addVertex(v3);
	        g.addVertex(v4);

	        // add edges to create a circuit
	        g.addEdge(v1, v2);
	        g.addEdge(v2, v3);
	        g.addEdge(v3, v4);
	        g.addEdge(v4, v1);

	        return g;
	    }
	    
	  public void destroy()
	  {
	      // do nothing.
	  }


}
