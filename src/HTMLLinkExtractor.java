import java.util.Vector;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** Written by Tim Macinta 1997                           <br>
 *  Distributed under the GNU Public License
 *       (a copy of which is enclosed with the source).   <br>
 *                                                        <br> 
 *  This LinkExtractor can extract URLs from HTML files.  <br>
 *
 *  <p> 
 *  Revised or added comments to some sections of code.<br>
 *
 *  Modified by Xiannong Meng to fix the finite state machine
 *  to recognize urls containing white spaces. <br>
 *  April 2005<p>
 *
 *  Revised 2013-04-27<br>
 *  Change the use of Vector to ArrayList<p>
 */

//public class HTMLLinkExtractor implements LinkExtractor {
public class HTMLLinkExtractor {
  
    ArrayList<URL> urls = new ArrayList<URL>();  // list of URLs
    int next_url = 0;                // next URL to return
    int url_count = 0;               // number of URLs
    URL base = null;                 // base URL

    /** Creates a new HTMLLinkExtractor that will enumerate all the
     *  URLs in the give "cache_file".
     */
    public HTMLLinkExtractor(File cache_file, URL base_url) throws IOException {
	this.base = base_url;
	//    System.err.println("in HTMLLinkExtractor: base " + base_url.getFile());
	InputStream in = new FileInputStream(cache_file);
	int state = 0;
	StringBuffer sb = new StringBuffer();
	int i = in.read();
	//    System.err.println("before extractor");
	while (i >= 0) {
	    //      System.err.print((char)i);
	    switch (state) {
	    case 0:
		if (i == '<') state = '<';
		break;
	    case '<':
		if (i == '>') {
		    state = 0;
		    analyze(sb.toString());
		    //	  System.err.println("in extractor <" + sb.toString());
		    sb.setLength(0);
		} else if (i == 'a' || i == 'A') {
		    state = 'a';
		    sb.append((char) i);
		}
		break;
	    case 'a':
		if (Character.isWhitespace((char)i))
		    {
			state = '+';
			sb.append((char)i);
		    }
		break;
	    case '+':
		if (!Character.isWhitespace((char)i))
		    {
			state = '-';
			sb.append((char)i);
		    }
		break;
	    case '-':
		if (i == '>')
		    {
			state = 0;
			analyze(sb.toString());
			//  System.err.println("in extractor -" + sb.toString());
			sb.setLength(0);
		    }
		else if (!Character.isWhitespace((char)i))
		    sb.append((char)i);
		break;
	    }
	    //      System.err.println("state: " + (char)state);
	    i = in.read();
	}
	//    System.err.println("after extractor" + sb.toString());
	if (sb.length() > 0) analyze(sb.toString());

	in.close();
    }

    /** 
     *  Creates a new HTMLLinkExtractor that will enumerate all the
     *  URLs in the given string.<p>
     *  
     *  @param thisPage The web page being processed
     *  @param base_url The url base for this web page (i.e., relative base)
     */
    public HTMLLinkExtractor(String thisPage, URL base_url) throws IOException {

	this.base = base_url;
	//    System.err.println("in HTMLLinkExtractor: base " + base_url.getFile());
	int state = 0;
	int pLength = thisPage.length();
	if (pLength == 0)
	    return;

	char[] in = new char[pLength];
	in = thisPage.toCharArray();
	int c = 0;

	StringBuffer sb = new StringBuffer();
	int i = in[c++];
	//    System.err.println("before extractor");
	while (c < pLength) {
	    //      System.err.print((char)i);
	    switch (state) {
	    case 0:
		if (i == '<') state = '<';
		break;
	    case '<':
		if (i == '>') {
		    state = 0;
		    analyze(sb.toString());
		    //	  System.err.println("in extractor <" + sb.toString());
		    sb.setLength(0);
		} else if (i == 'a' || i == 'A') {
		    state = 'a';
		    sb.append((char) i);
		}
		break;
	    case 'a':
		if (Character.isWhitespace((char)i))
		    {
			state = '+';
			sb.append((char)i);
		    }
		break;
	    case '+':
		if (!Character.isWhitespace((char)i))
		    {
			state = '-';
			sb.append((char)i);
		    }
		break;
	    case '-':
		if (i == '>')
		    {
			state = 0;
			analyze(sb.toString());
			//  System.err.println("in extractor -" + sb.toString());
			sb.setLength(0);
		    }
		else if (!Character.isWhitespace((char)i))
		    sb.append((char)i);
		break;
	    }
	    //      System.err.println("state: " + (char)state);
	    i = in[c++];
	}
	//    System.err.println("after extractor" + sb.toString());
	if (sb.length() > 0) analyze(sb.toString());
    }



    /** 
     *  Analyzes "param", which should be the contents between a '<' and a '>',
     *  and adds any URLs that are found to the list of URLs.<p>
     * 
     *  @param param The parameter section
     */
    public void analyze(String param) {

	StringTokenizer st = new StringTokenizer(param);
	//    System.err.println("in analyze " + param);
	if (st.countTokens() < 2) return;
	String first_word = st.nextToken().toLowerCase();
	//    System.err.println("in analyze(first_word) " + first_word);
	if (first_word.equals("a")) {
	    analyzeAnchor(st.nextToken(""));
	} else if (first_word.equals("frame")) {
	    analyzeFrame(st.nextToken(""));
	} else if (first_word.equals("base")) {
	    extractBase(st.nextToken(""));
	} 
    }

    /** 
     *  Analyzes the <a> tag.  <p>
     *
     *  @param anchor  The anchor text to be analyzed.
     */
    void analyzeAnchor(String anchor) {
	String href = extract(anchor, "href");
	//    if (href == null) System.err.println("href null anchor" + anchor);
	if (href == null) return;
	try {
	    //	href = fixUrl(href);
	    //	System.err.println("in analyzeAnchore: adding " + base + "|" + href);
	    addURL(new URL(base, href));
	} catch (MalformedURLException e) {
	    anchor = anchor.toLowerCase();
	    // java doesn't understand mailto and will throw an exception
	    //	    if (!href.startsWith("mailto:")) {
	    //		e.printStackTrace();
	    //	    }
	}
    }

    /** 
     *  Analyzes the <frame> tag.  <p>
     *
     *  @param frame The 'frame' tag being analyzed
     */
    void analyzeFrame(String frame) {

	String src = extract(frame, "src");
	//    if (src == null) System.err.println("src null");
	if (src == null) return;
	try {
	    //	System.err.println("in analyzeFrame: adding " + base + "|" +
	    //	src);
	    addURL(new URL(base, src));
	} catch (MalformedURLException e) {
	    //	    e.printStackTrace();
	}
    }

    /** 
     *  Extracts the base URL from the <base> tag. <p>
     * 
     *  @param b  Base tag, currently only works for 'href'
     */
    void extractBase(String b) {

	String b2 = extract(b, "href");
	if (b2 != null) {
	    try {
		base = new URL(base, b2);
	    } catch (MalformedURLException e) {
		//		e.printStackTrace();
	    }
	}
    }

    /** 
     * Adds "url" to the list of URLs.  <p>
     *
     * @param url  The url to be added
     */
    public void addURL(URL url) {

	urls.add(url);
	url_count++;
    }

    /**
     *  Check to see if there is more elements. <p>
     *
     *   @return true if the list has more elements, false otherwise
     */
    public boolean hasMoreElements() {

	return url_count != next_url;
    }
    
    /**
     *  Return the next element in the list.<p>
     * 
     *  @return The next element in the list
     */
    public Object nextElement() {

	Object ob = urls.get(next_url);
	next_url++;
	return ob;
    }
  
    /** 
     *  Resets this enumeration.  
     */
    public void reset() {

	next_url = 0;
    }

    /** Returns the value in "line" associated with "key", or null if "key"
     *  is not found.  For instance, if line were "a href="blah blah blah"
     *  and "key" were "href" this method would return "blah blah blah".
     *  <p>
     *  Keys are case insensitive.<p>
     *
     *  @param line  The source data to work with
     *  @param key   The pattern to look for
     */
    String extract(String line, String key) {

	//	System.err.println("line in extract |" + line + "|");
	line = line.replace('\'', '\"');  // some sites use ' instead of "
	//	System.err.println("line in extract after replace |" + line + "|");
	try {
	    key = key.toLowerCase();
	    String lower_case = line.toLowerCase();
	    int i = lower_case.indexOf(key);
	    if (i < 0) return null;
	    i += key.length();
	    if (line.charAt(i) != '=') return null;
	    i++;
	    int i2;
	    if (line.charAt(i) == '"') {
		i++;
		i2 = line.indexOf('"', i);
		if (i2 < 0) {
		    return line.substring(i);
		} else {
		    return line.substring(i, i2);
		}
	    } else {
		int targ = line.length();
		for (i2 = i; i < targ; i++) {
		    if (Character.isWhitespace(line.charAt(i))) break;
		}
		return line.substring(i, i2); 
	    }
	} catch (StringIndexOutOfBoundsException e) {}
	return null;
    }

    /**
     *  Add '/' to a url whose ending is not a file.
     *  Then remove the extra '/'s.
     *  This is an add-hoc fix, only works for "href".<p>
     *   
     *  @param inUrl  The url to examine
     *  @return The fixed url
     */
    static public String fixUrl(String inUrl)
    {
	int docLoc  = inUrl.lastIndexOf('.');
	int protLoc = inUrl.indexOf("://");
	int slashLoc = -1;
	if (protLoc > 0)
	    slashLoc = inUrl.indexOf('/', protLoc+3);
	else
	    slashLoc = inUrl.indexOf('/');
	if (slashLoc < 0 ||
	    (slashLoc > 0 && docLoc < slashLoc)) // none
	    inUrl = inUrl + '/';
	inUrl = removeExtraSlash(inUrl);
	return inUrl;
    }

    /**
     *  Remove extra '/'s in a url.<p>
     *
     *  @param inUrl  The url to examine
     *  @return The fixed url
     */
    static public String removeExtraSlash(String inStr)
    {
	int slashLoc = inStr.indexOf("://");
	int l = inStr.length();
	int hold = slashLoc + 3;
	slashLoc = inStr.indexOf("//", hold);
	while (slashLoc > 0 && slashLoc < l)
	    {
		hold = slashLoc;
		while (hold < l && inStr.charAt(hold) == '/')
		    hold ++;
		if (hold >= l)
		    {
			inStr = inStr.substring(0, slashLoc+1);
			break;
		    }
		else
		    inStr = inStr.substring(0, slashLoc+1) 
			+ inStr.substring(hold);
		slashLoc = inStr.indexOf("//", hold);
		l = inStr.length();
	    }

	return inStr;
    }

    /**
     *  Print the string form of a url for a given url object.<p>
     *
     *  @param theUrl   The url to be printed
     */
    static public void printAUrl(URL theUrl)
    {
	if (theUrl.getPort() > 0)
	    System.out.println(theUrl.getProtocol() + "://" +
			       theUrl.getHost() + ":" +
			       theUrl.getPort() + 
			       theUrl.getFile());
	else if (theUrl.getProtocol().compareToIgnoreCase("mailto") == 0)
	    System.out.println(theUrl.getProtocol() + ":" +
			       theUrl.getFile());
	else 
	    System.out.println(theUrl.getProtocol() + "://" +
			       theUrl.getHost() + 
			       theUrl.getFile());
    }

    /**
     *  Print all urls in the list.<p>
     */
    public void printAllUrls()
    {
	int count = urls.size();
	for (int i = 0; i < count; i ++)
	    {
		URL aUrl = (URL)urls.get(i);
		printAUrl(aUrl);
	    }
    }

    /**
     *  Return all urls from the list.
     *
     *  @return url list
     */
    public ArrayList<URL> getAllUrls()
    {
	return urls;
    }

    /*
     * Simple test drive.
     */
    static public void main(String[] argv) throws Exception
    {
//	if (argv.length != 2) {
//	    System.err.println("usage: java HTMLLinkExtractor [url] [file-to-process]");
//	    System.exit(1);
//	}

	// argv[0] should be in the form of proto://host.domain/dir/file
	URL aUrl = new URL("http://index.html");
	File aFile = new File("D:/2013-spring/index.html");
	InputStream in = new FileInputStream(aFile);
	String page = "";
	int c = in.read();

	while (c >= 0)
	    {
		page += (char)c;
		c = in.read();
	    }
	HTMLLinkExtractor htmlExtractor = 
	    new HTMLLinkExtractor(page, aUrl);

	System.out.println("count of urls : " + htmlExtractor.urls.size());
	htmlExtractor.printAllUrls();
    }
}