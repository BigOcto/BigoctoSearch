package com.bigocto.crawl;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.StringTokenizer;


/**
 * @author Big_Octos
 * @date 2014/6
 */
public class crawler implements Runnable{
	
	public static Queue<String> m_queue=new LinkedList<String>();
	public static List<String> m_list=new LinkedList<String>();
	public static List<String> timeout_list=new LinkedList<String>();
	public static List<Thread> threadList=new ArrayList<Thread>(2);
	public static int threads=4;
	public static int Doc_ID=1;
	public static ParseHtml parseH=new ParseHtml();
	public  static boolean list_filter(String url,List<String> list){
		Iterator<String> it=list.iterator();
		while(it.hasNext()){
			
			if(url.equals(it.next())){
				return false;
			}
		}
		return true;
		
	}
	
	
	public synchronized String deQueueUrl() throws Exception{
		if(m_list!=null){
			String url=m_queue.remove();
			return url;
		}else{
			threads--;
			if(threads>0){
				wait();
				threads++;
			}else{
				notifyAll();
				return null;
			}
		}
		return null;
		
	}
	public synchronized void enQueueUrl(String url){
		if(!m_queue.contains(url)){
				if(!url.equals("")&&!url.equals(null)){
					Doc_ID++;
					try {
						parseH.parse(url, Doc_ID);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					m_queue.add(url);
					notifyAll();
					
				}
			}


	}

	public Boolean http_status_filter(String url){

        	HttpResponse response;

			try {
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpGet httpget=new HttpGet(url);
				httpget.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
				
				response = httpclient.execute(httpget);
				StatusLine status=response.getStatusLine();
				System.out.print(status.getStatusCode());
				if(status.getStatusCode()==200){
					return true;
				}else{
					return false;
				}
				
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				timeout_list.add(url);//timeout urls are saved
				
				m_list.add(url);//timeout urls are saved in already used url
				
				System.out.println("TimeOut url :"+url);
				return false;
			}
			
	}
	
	public static Boolean divide(String str){
		List<String> list=new ArrayList<String>();
		list.clear();
		int le=0,mi=0,ri=0,num=0;
		String str_l="";
		StringTokenizer token=new StringTokenizer(str, "/ .");
		
		while (token.hasMoreTokens()){
			list.add(token.nextToken());
		}
    	Iterator it=list.iterator();
    	while(it.hasNext()){
    		num++;
    		switch (str_l){
    			case "seu":
    				le=num;
    				break;
    			case "edu":
    				mi=num;
    				break;
    			case "cn":
    				ri=num;
    				break;
    		}
    		if(le+ri==2*mi){
    			return true;
    		}else{
    			return false;
    		}
    			
    	}
		return false;
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String url="";
		boolean list_bool=false;
		boolean status_bool=false;
		boolean seu_bool=false;
		
		if(m_queue!=null){
			
			try {
				
				url=deQueueUrl();//out of queue
				
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
			
			list_bool=list_filter(url,m_list);
			status_bool=http_status_filter(url);
			seu_bool=divide(url);
			if(list_bool!=false){
				if(status_bool!=false){
					if(seu_bool!=false){
						try{

					        Document doc = Jsoup.connect(url).get();
					        Elements links = doc.select("a[href]");
					        
					        System.out.println("Fetching : "+url);
					        for (Element link : links) {
					        	//m_queue.add(link.attr("abs:href"));
					        	enQueueUrl(link.attr("abs:href"));
					           
					            System.out.println(" URL: "+link.attr("abs:href"));
					        }
					        
							}catch(Exception e){
								e.printStackTrace();
							}

							m_list.add(url);//the url that is already traveled 
					}

					
				}

			}
			
		}else{
			System.out.print("queue NULL");
		}
	}
	
	
    public static void main(String[] args) throws IOException {
	
        String url = "http://www.seu.edu.cn/s/132/main.jspy";
        m_queue.add(url);
        parseH.parse(url,1);
        crawler c1=new crawler();
        crawler c2=new crawler();
        crawler c3=new crawler();
        crawler c4=new crawler();
 
        while(m_queue.size()>0){
        	 c1.run();
        	 c2.run();
        	 c3.run();
        	 c4.run();
        }
        
    }
    


}