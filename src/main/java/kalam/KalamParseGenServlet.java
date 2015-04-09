/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package kalam;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;
import opennlp.ccg.grammar.*;
import opennlp.ccg.hylo.*;
import opennlp.ccg.lexicon.MorphItem;
import opennlp.ccg.lexicon.Word;
import opennlp.ccg.ngrams.StandardNgramModel;
import opennlp.ccg.parse.*;
import opennlp.ccg.realize.*;
import opennlp.ccg.synsem.*;
import opennlp.ccg.util.GroupMap;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom.Document;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Giuliano
 */
public class KalamParseGenServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    int globalCount;
    HashMap<String, Grammar> ParseGrammars = new HashMap<String, Grammar>();
    HashMap<String, Grammar> GenGrammars = new HashMap<String, Grammar>();
//    Grammar grammar;
    Grammar parse_grammar;
    Grammar gen_grammar;
    
    final String[] AFFIX_MARKERS_AS_ARRAY = {"\u0640","="};
    final List<String> AFFIX_MARKERS = Arrays.asList(AFFIX_MARKERS_AS_ARRAY);
    final char TASHDID = '\u0651';
    final char FATHA = '\u064E';
    final char DAMMA = '\u064F';
    final char KASRA = '\u0650';
    final char SUKUN = '\u0652';
    final String HARAKA = "[" + FATHA + "-" + KASRA + "[" + SUKUN + "]]";
    final char FATHATAN = '\u064B';
    final char DAMMATAN = '\u064C';
    final char KASRATAN = '\u064D';
    final String HARAKATAN = "[" + FATHATAN + "-" + KASRATAN + "]";
    final String HARAKA_TAN = "[" + FATHATAN + "-" + KASRA + "[" + SUKUN + "]]";
    final char ALIF = '\u0627';
    String parseAffixMarker;
            
    Parser parser;
    Realizer realizer;
    String parse_gram;
    String gen_gram;
    String parse_lang;
    String gen_lang;
    PrintWriter out;
    JSONObject json;
    final String GRAM_SUFFIX = "-grammar.xml";

    GroupMap<Word,MorphItem> words;
    Set<Word> word_keys;
    List<Word> wordlist;            
    List<String> suffixlist;            

    HashSet<String> WordDecompositions;
    ArrayList<HashSet<String>> InputSentenceDecomposition;
    
    //Variables made global in order to segment the main parsing routine
    //in several calls to update the calling jsp page
    String sentenceToParse;
    String catType;
    String outParses;
    String outResults;
    List<Sign> parses;
    List<String> sentencesToParse;
    Sign[] results;    
    int resLength;
    Sign curParse;
    Category cat;
    Nominal index;
    LF convertedLF;
    LF lf;
    opennlp.ccg.realize.Chart chart;
    String orth;
    int numParses;
    //      FeatureStructure fs;
    LF curLF;
    ArrayList<LF> LFS;
    
    private static final Map<String, String> Languages;
    static
    {
        Languages = new HashMap<String, String>();
        Languages.put("English", "EN");
        Languages.put("Russian", "RU");
        Languages.put("Arabic", "AR");
    }

    /**
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        //initMySql();
        globalCount = 0;
        String getProperty = System.getProperty("user.home");
        Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.INFO, "systemRoot: {0}", getProperty);
        String setProperty = System.setProperty("java.util.prefs.syncInterval","2000000");
        /*String setProperty = System.setProperty("java.util.prefs.PreferencesFactory", "DisabledPreferencesFactory");*/
        Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.INFO, setProperty);
    }
    public static
    <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
      List<T> list = new ArrayList<T>(c);
      java.util.Collections.sort(list);
      return list;
    }

    private String showLexicon() {
        String outString = "";
        outString += "<span class=\"inline\">Lexicon " + parse_gram + "</span> (";
        outString += words.size() + " words loaded). Move the mouse over the words for more information.<BR><BR>";
        for (Word word: wordlist) {
            outString += "<span class=\"word\" title=\"";
            Iterator<MorphItem> morphitem = words.get(word).iterator();
            while (morphitem.hasNext()) {
                outString += morphitem.next().getWord() + " ";
            }
            /*if (word.toString().startsWith("c")) {
                outString += "\" style=\"color: #f00;";
            }*/
            outString += "\"> " + word + "</span> ";
        }
        return outString;
    }
    
    String makeArabicRegex(String baseWord) {
        char State = 'A'; //initial state
        String Regex = "";
        for (char ch: Lists.charactersOf(baseWord)) {
            switch (Character.getType(ch)) {
                case Character.OTHER_LETTER: //C
                    switch (State) {
                        case 'A': case 'V': Regex += ch; break;
                        case 'C': Regex += TASHDID + "?" + ((ch == ALIF) ? FATHA : HARAKA) + "?" + ch; 
                            break;
                        case 'S': Regex += ((ch == ALIF) ? FATHA : HARAKA) + "?" + ch; break;
                    }; State = 'C'; break;
                case Character.SPACE_SEPARATOR: //W
                    switch (State) {
                        case 'C': Regex += TASHDID + "?" + HARAKA_TAN + "?" + ch; break;
                        case 'S': Regex += HARAKA_TAN + "?" + ch; break;
                        case 'V': case 'T': Regex += ch; break;
                      }; State = 'W'; break;
                case Character.NON_SPACING_MARK: //V T 
                    switch (ch) {
                        case FATHA: case KASRA: case DAMMA: //V
                            switch(State) {
                                case 'C': Regex += TASHDID + "?" + ch; break;
                                case 'S': Regex += ch; break;
                            }; State = 'V'; break;
                        case FATHATAN: case KASRATAN: case DAMMATAN: //T
                            switch(State) {
                                case 'C': Regex += TASHDID + "?" + ch; break;
                                case 'S': Regex += ch; break;
                            }; State = 'T'; break;
                    }
                case Character.OTHER_PUNCTUATION:
                    switch(ch) {
                        case '.': Regex += "\\."; 
                            break;
                    };break;
                default: Regex += ch; break;
            }
        }
       
        //FINAL
        switch (State) {
            case 'C': Regex += TASHDID + "?" + HARAKA_TAN + "?"; break;
            case 'S': Regex += HARAKA_TAN + "?"; break;
            case 'V': case 'T': break;
          } //;State = 'E';            
        return StringEscapeUtils.escapeJava(Regex);
    }

    /*
  
  E [shape=doublecircle];
  C -> E [label = "E:S?VT?W"];
  S -> E [label = "E:VT?W"];
  T -> E [label = "E:"];
}     */
    
    void getWordDecompositions(String baseWord, String baseSuffix) {
        // 6. Add the base word
        Set<String> wordForms = new HashSet<String>();
        String splitWord;
        ArrayList<String> definitions;

        wordForms.add(baseWord);
        //Arabic: add regex for vocalization
        if ("AR".equals(parse_lang)) {
            baseWord = makeArabicRegex(baseWord);
        }
        
        definitions = searchDefinition(baseWord);
        if (definitions != null) {
            wordForms.addAll(definitions);
        }
        
        for (String word: wordForms) {
            if ((definitions != null) && (definitions.contains(word))) {
                WordDecompositions.add(word + baseSuffix);}
                // 7. Add decompositions by detaching all possible suffixes
                for (Iterator<String> it = suffixlist.iterator(); it.hasNext();) {
                    String suffix;
                    suffix = it.next();
                    if (word.endsWith(suffix)) {
                        splitWord = word.substring(0,word.lastIndexOf(suffix));
                        WordDecompositions.add(splitWord + " " + parseAffixMarker + suffix + baseSuffix);
                        getWordDecompositions(splitWord, " " + parseAffixMarker + suffix + baseSuffix);
                    }
                }
        }
    }
  
    List<String> getSentencesToParse(String InputSentence) {
        List<String> SentencesToParse;
/*        List<String[]> TokenizedSentencesToParse;
        TokenizedSentencesToParse = new ArrayList<String[]>();
        String[] TokensToParse = InputSentence.split(" ");*/
        SentencesToParse = new ArrayList<String>();
        InputSentence += " ";
        // 1. Create a list of lists to hold possible word decompositions
        InputSentenceDecomposition = new ArrayList<HashSet<String>>();
        // 2. Split the InputSentence into an array
        String[] SplittedInputSentence = InputSentence.split(" ");
        // 3. Loop through the array of splitted words
        for (int i=0; i < SplittedInputSentence.length; i++) {
            // 4. Read the base word
            String baseWord = SplittedInputSentence[i];
            // 5. Create the array of word decompositions
            WordDecompositions = new HashSet<String>();
            getWordDecompositions(baseWord, "");
            InputSentenceDecomposition.add(WordDecompositions);
        }
        Set<List<String>> setOfLists = Sets.cartesianProduct(InputSentenceDecomposition);
        String outString = "";
        for (Iterator<List<String>> it = setOfLists.iterator(); it.hasNext();) {
            List<String> ListInSet = it.next();
            String newSentence = Joiner.on(" ").join(ListInSet);
            outString += newSentence + "; ";
            SentencesToParse.add(newSentence);
        }
        Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.INFO, 
                outString);
        return SentencesToParse;
    }

    void initParseGen(HttpServletRequest request, HttpServletResponse response) {
        sentenceToParse = request.getParameter("sentenceToParse");
        catType = request.getParameter("catType");
        //String showDerivs = request.getParameter("showDerivs");
        
        outParses = "";

        request.setAttribute("sentenceToParse", sentenceToParse);
        outResults = "";
        parses = new ArrayList<Sign>();
        sentencesToParse = getSentencesToParse(sentenceToParse);
        
    }

    void parseSentences() {
            parses.clear();
            for (Iterator<String> it = sentencesToParse.iterator(); it.hasNext();) {
            String sentence = it.next();
            try {
                //List<Word> parse_words = parse_grammar.lexicon.tokenizer.tokenize(sentence);
                // parse words
                //parser.parse(parse_words);

                parser.parse(sentence);
//                Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.INFO, String.valueOf(parser.getLexTime()+parser.getParseTime()));
                parses.addAll(parser.getResult());
            }
            catch (ParseException ex) {
            /*Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.INFO, "The system was unable to parse the input {0}"
                    + "Error: {1} Please try to correct the input sentence.", new Object[]{sentenceToParse, ex.toString()});*/
            }
        }
        results = new Sign[parses.size()];
        parses.toArray(results);

}

    void buildParseList(HttpServletRequest request) throws IOException {
                resLength = results.length;
        numParses = 0;
//      FeatureStructure fs;
        Grammar.theGrammar.prefs.showSem = true;
        LFS = new ArrayList<LF>();
        for (int iParse = 0; iParse < resLength; iParse++) {
            curParse = results[iParse];
            cat = curParse.getCategory();
            String curDeriv = curParse.getDerivationHistory().toString();
            log(curDeriv);
            if ("chunks".equals(catType) || 
                    (cat.getSupertag().equals("s") && cat instanceof AtomCat)) {
            //if ("on".equals(showDerivs)) {
                outParses += "<div class = \"derivs\"><p>" + curDeriv.replace("\n","</p><p>") + "</p></div>";
            //}
            index = cat.getIndexNominal();
//            fs = cat.getFeatureStructure();
            lf = cat.getLF();
            convertedLF = HyloHelper.compactAndConvertNominals(lf, index, curParse);
            if (!LFS.contains(convertedLF)) {
                numParses++;

                String sessionID = request.getSession().getId();
                String tmpFile = "/WEB-INF/grammars/" + sessionID + ".xml";
                log(tmpFile);
                String outputfile = this.getServletContext().getRealPath(tmpFile);
                //ByteArrayOutputStream outputfile = new ByteArrayOutputStream();
                parse_grammar.saveToXml(convertedLF, "", outputfile);
                //ByteArrayInputStream inputfile = new ByteArrayInputStream(outputfile.toByteArray());
                String inputfile = this.getServletContext().getRealPath(tmpFile);
                Document doc = gen_grammar.loadFromXml(inputfile);
                curLF = Realizer.getLfFromDoc(doc);
                LFS.add(curLF);
            }
            }
        }
    }
    
    void realizeParses() {
        
        int i = 0;
         for (int iLF = 0; iLF < LFS.size(); iLF++) {
            curLF = LFS.get(iLF);
            realizer.realize(curLF);
            outParses += "<strong>" + ++i + ".</strong> " + 
                    curLF.prettyPrint("").replace(" ",
                    "&nbsp;").replace(">", "&gt;").replace("<", 
                    "&lt;").replace("&lt;", "<span class=\"role\">").replace("&gt;", 
                    "</span>").replace("\n","<BR/>") + "<BR/>";
            ArrayList<String> orths = new ArrayList<String>();
            chart = realizer.getChart();
            try {
                List<opennlp.ccg.realize.Edge> bestEdges = chart.bestEdges();
    //            log("Edge number: " + String.valueOf(bestEdges.size()));
                for (int len = bestEdges.size(), iChart = 0; iChart < len; iChart++) {
                    orth = chart.bestEdges().get(iChart).getSign().getOrthography();
                    //log(chart.bestEdges().get(iChart).getSign().getDerivationHistory().toString());
                    List<Word> wrds = chart.bestEdges().get(iChart).getSign().getWords();
                    //log(chart.bestEdges().get(iChart).getSign().getWordsInXml().toString());
                    if (!orths.contains(orth)) {
                        if (filterOrth(wrds) == false) {
                            orths.add(normalizeOrth(wrds));
                        }
                    }
                }
            }
            catch (NullPointerException ex) {
                String msg = "Error: No predicate available in the target languate";
                log(msg);
                outParses += "<strong>" + msg + "</strong><BR/>";
            }

            String curOrth;
            for (int iOrth = 0; iOrth < orths.size(); iOrth++) {
                curOrth = orths.get(iOrth);
                outParses += "<div class=" + ("AR".equals(gen_lang) ? "\"arabic\"" :  "\"latin\"")  +
                    ">" + curOrth.replace(" ",
                    "&nbsp;").replace(">", "&gt;").replace("<", 
                    "&lt;").replace("&lt;", "<span class=\"role\">").replace("&gt;", 
                    "</span>").replace("\n","<BR/>") + 
                    "</div>";
            }
            }        
    }
    
    void createOutput() {
                switch (numParses) {
            case 0: outResults += "no parse found by the system";
                break;
            case 1: 
                outResults += numParses + " parse found by the system"; 
                break;
            default: outResults += numParses + " parses found by the system"; 
        }
        if (!"chunks".equals(catType)) {
        switch (resLength) {
            case 0: outResults += " (no partial parse found).";
            break;
            case 1: 
                outResults += " (" + resLength + " partial parse found)."; 
                break;
            default: outResults += " (" + resLength + " partial parses found)."; 
        }
        }
        else {
        outResults += ".";}
        outResults += "<BR/>";
        try {
             json.put("outResults", outResults);
        } catch (JSONException ex) {
            Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            json.put("outParses", outParses);
        } catch (JSONException ex) {
            Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    void processParseGen(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        initParseGen(request, response);
        parseSentences();
        /*Iterator<Sign> parse = parses.iterator();*/
        buildParseList(request);
        realizeParses();
        createOutput();

    }

    public ArrayList<String> searchDefinition(String regex) {
	ArrayList<String> searchResults = new ArrayList<String>();

	Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

	Iterator<Word> ite = word_keys.iterator();

	while (ite.hasNext()) {
		String candidate = ite.next().getForm();
		Matcher m = p.matcher(candidate);
		if (m.matches()) {
			searchResults.add(candidate);
		}
	}	

	if (searchResults.isEmpty()) {
		return null;
	}
	else {
		return searchResults;
	}
}
    
    void loadGrammar(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String grammarName = request.getParameter("grammar");
        String grammarFileName = grammarName + GRAM_SUFFIX;
        String targetName = request.getParameter("target");
        log("target: " + targetName);
         String lmfile = this.getServletContext().getRealPath("/WEB-INF/english/nk.4bo");
        //the grammar is loaded?
            String grammarfile = this.getServletContext().getRealPath("/WEB-INF/grammars/" + grammarFileName);
            log("grammarfile: " + grammarfile);
            URL grammarURL = null;
            try {
                grammarURL = new File(grammarfile).toURI().toURL();
            log("grammarURL" + grammarURL);
            log("Loading grammar from URL: " + grammarURL);
            } catch (MalformedURLException ex) {
                Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
            }                    
        if ("parse_gram".equals(targetName)) {
            parse_gram = grammarName;
            parse_lang = findLanguage(parse_gram);
            log("Parse language: " + parse_lang);
            if ("EN".equals(parse_lang) || "RU".equals(parse_lang)) {
                parseAffixMarker = "=";
            }
            else if ("AR".equals(parse_lang)) {
                parseAffixMarker = "\u0640";
            }
            
            parse_grammar = new Grammar(grammarURL); //ParseGrammars.get(grammarName);
            parser = new Parser(parse_grammar);
            words = parser.lexicon.getWords();
            word_keys = words.keySet();
            wordlist = asSortedList(word_keys);
            suffixlist = new ArrayList<String>();
            for (String affix_marker: AFFIX_MARKERS) {
                for (Word word : wordlist) {
                    String wordform = word.toString();
                    if (wordform.startsWith(affix_marker)) {
                        suffixlist.add(wordform.substring(affix_marker.length()));
                    }
                }
            }
        }

        
        else if ("gen_gram".equals(targetName)) {
            gen_gram = grammarName;
            gen_grammar = new Grammar(grammarURL);
            gen_lang = findLanguage(gen_gram);
            log("Generation language: " + gen_lang);
            realizer = new Realizer(gen_grammar);
            try {
                realizer.signScorer = new StandardNgramModel(4,lmfile);
                } catch (IOException ex) {
                    Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            realizer.pruningStrategy = new NBestPruningStrategy ();
        }

    }
    
    void listGrams(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException 
    {
        /*File f = new File(this.getServletContext().getRealPath("/WEB-INF/english/"));
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(f.list()));*/

        File dir = new File(this.getServletContext().getRealPath("/WEB-INF/grammars/"));
        log(this.getServletContext().getRealPath("/WEB-INF/grammars/"));
        String[] files;
        files = dir.list(new FilenameFilter() {
   @Override
   public boolean accept(File dir, String name) {
   return name.toLowerCase().endsWith(GRAM_SUFFIX);
   }
});
        int fileCount = 0;
        String outString = "";
        outString += "<option value=\"" + String.valueOf(fileCount) + 
                "\">&lt;Select a grammar&gt;</option>";
        for (String filename: files) {
            ++fileCount;
            outString += "\n<option value=\"" + String.valueOf(fileCount) + 
                "\">" + filename.substring(0, filename.length() - GRAM_SUFFIX.length()) + 
                    "</option>";
/*            outString = "<option value=\"" + String.valueOf(fileCount) + 
                    "\">" + filename + "</option>";*/
        }
        json.put("loading", outString);
    }   
    
    List<String> getSuffix(String word) {
        List<String> returnList = new ArrayList<String>();
        String suffix_marker = "";
        String suffix = word;
        for (String affix_marker: AFFIX_MARKERS) {
            String wordform = word;
                    if (wordform.startsWith(affix_marker)) {
                        suffix_marker = affix_marker;
                        suffix = wordform.substring(affix_marker.length());
                        break;
                }
            }
        returnList.add(suffix_marker);
        returnList.add(suffix);
        return returnList;
     }
    
    
    String normalizeOrth(List<Word> inWords) {
        String normalizedOrth = "";
        String curWord;
        for (int i=0; i<inWords.size(); i++) {
            curWord = inWords.get(i).getForm();
            if (i > 0) {
                if (!"".equals(getSuffix(curWord).get(0))) {
                    normalizedOrth += getSuffix(curWord).get(1);
                }
                else {
                    normalizedOrth += " " + curWord;
                }
            }
            else {
                normalizedOrth += curWord;
            }
        }
        return normalizedOrth;
    }
    
    boolean filterOrth(List<Word> inWords) {
        boolean filter = false;
        //a/an filter
        if ("EN".equals(gen_lang)) {
            for (int i = 0; i < inWords.size(); i++) {
                if ("Det".equals(inWords.get(i).getPOS())) {
                    if ("an".equals(inWords.get(i).getForm())) {
                        if (inWords.get(i + 1).getForm().matches("[^aeiouAEIOU].*")) {
                            filter = true;
                            break;
                        }
                    } else if ("a".equals(inWords.get(i).getForm())) {
                        if (inWords.get(i + 1).getForm().matches("[aeiouAEIOU].*")) {
                            filter = true;
                            break;
                        }
                    }
                }
            }
        }
        return filter;
    }

    boolean filterOrth(String inOrth) {
        boolean filter = false;
        //a/an filter
        if (inOrth.matches("(^|(.*\\s))[Aa]n\\s[^aeiouAEIOU].*")) {
            filter = true;
        }
        else if (inOrth.matches("(^|(.*\\s))[Aa]\\s[aeiouAEIOU].*")) {
            filter = true;
        }
        return filter;
    }

    /**
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     * @throws JSONException
     */
    
    String findLanguage(String grammarName) {
        String outLanguage = "";
        
        for (String searchString: Languages.keySet()) {
            if (grammarName.contains(searchString)) {
                outLanguage = Languages.get(searchString);
                break;
            }
        }
        
        return outLanguage;
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, JSONException {
        
        globalCount++;
        response.setContentType("text/html; charset=utf-8");
        out = response.getWriter();
        json = new JSONObject();

        //Logs map of request parameters
        Map params = request.getParameterMap();
        
        if (params.containsKey("catType") && params.containsKey("sentenceToParse")) {
            //processParseGen(request, response);
            if (params.containsKey("action")) {
            String[] actions = (String[]) params.get("action");
            String action = actions[0];
            if ("initParseGen".equals(action)) {
                initParseGen(request, response);
            } else if ("parseSentences".equals(action)) {
                parseSentences();
            } else if ("buildParseList".equals(action)) {
            /*Iterator<Sign> parse = parses.iterator();*/
            buildParseList(request);
            } else if ("realizeParses".equals(action)) {
            realizeParses();
            } else if ("createOutput".equals(action)) {
            createOutput();
            }
            else
            {Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, "Action not sent.");}
            }
        }
        else if (params.containsKey("loading")) {
            listGrams(request, response);
            //TestGen(request,response);
        }
        else if (params.containsKey("grammar") && params.containsKey("target")) {
            loadGrammar(request, response);
            }
        else if (params.containsKey("lexicon")) {
            json.put("lexicon", showLexicon());
        }
        out.print(json);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (JSONException ex) {
            Logger.getLogger(KalamParseGenServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
