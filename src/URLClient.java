import java.io.*;
import java.net.*;

/*
 * A client to retrieve a web page using the URL package, instead
 * of raw socket. <p>
 *
 * @author Xiannong Meng
 * @date   September 8, 2004
 *
 */
public class URLClient
{
    public static void main(String argv[]) throws Exception
    {
//	if (argv.length != 1) {
//	    System.err.println("usage: java URLClient [url]");
//	    System.exit(1);
//	}
	URL u = new URL("http://www.seu.edu.cn");  // argv is in the form "http://www.yahoo.com/"
	HttpURLConnection uC;
	int responseCode;
	InputStream input;
	BufferedReader remote;

	try
	    {
		// connet to the site
		uC = (HttpURLConnection)u.openConnection(); 
		System.out.println("port " + u.getPort());
		responseCode = uC.getResponseCode();
	    }
	catch (Exception ex)
	    {
		throw new Exception("first : " + ex.getMessage());
	    }

	if (responseCode != HttpURLConnection.HTTP_OK)
	    {
		throw new Exception("HTTP response code : " + 
				    String.valueOf(responseCode));
	    }

	try
	    {
		input = uC.getInputStream();
		remote = new BufferedReader(new InputStreamReader(input));

		String line = new String();

		// we will read and print a few lines only
		line = remote.readLine();
		System.out.print(" first line :::::: ");
		System.out.println(line);
   
		line = remote.readLine();
		System.out.print(" second line :::::: ");
		System.out.println(line);

		line = remote.readLine();
		System.out.print(" second line :::::: ");
		System.out.println(line);

		System.out.println(" more lines ...");

		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
		System.out.println(remote.readLine());
	    }
	catch (Exception ex)
	    {
		throw new Exception(ex.getMessage());
	    }
	System.out.println(" host : " + u.getHost());
	System.out.println(" protocol : " + u.getProtocol());
	System.out.println(" port : " + u.getPort());
	System.out.println(" allow interaction : " + uC.getAllowUserInteraction());
	System.out.println(" allow interaction default : " + uC.getDefaultAllowUserInteraction());

	System.out.println("URL : " + uC.getURL());
	System.out.println("header : " + uC.getHeaderField("http"));
    }
}