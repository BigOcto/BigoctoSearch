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
public class myCrawler extends crawler implements Runnable{

	public static List<Thread> threadList=new ArrayList<Thread>(2);
	public static int threads=4;

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
				mTimeOutList.add(url);//timeout urls are saved
				
				m_list.add(url);//timeout urls are saved in already used url
				
				System.out.println("TimeOut url :"+url);
				return false;
			}
			
	}
	
	public static Boolean divide(String str){
		List<String> list=new ArrayList<String>();
		list.clear();
		int le=0,mi=0,ri=0,num=0;
		StringTokenizer token=new StringTokenizer(str, "/ .");
		
		while (token.hasMoreTokens()){
			list.add(token.nextToken());
		}
    	Iterator it=list.iterator();
    	while(it.hasNext()){
    		num++;
			String str_l="";

			if(str_l.equals("seu")){
				le=num;
				break;
			}else if (str_l.equals("edu")){
				mi=num;
				break;
			}else if (str_l.equals("cn")){
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
		String url="";
		boolean list_bool=false;
		boolean status_bool=false;
		boolean seu_bool=false;
		
		if(mQueue!=null){
			
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
					        	//mQueue.add(link.attr("abs:href"));
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
        mQueue.add(url);
        parseH.parse(url,1);
        myCrawler c1=new myCrawler();
        myCrawler c2=new myCrawler();
        myCrawler c3=new myCrawler();
        myCrawler c4=new myCrawler();
 
        while(mQueue.size()>0){
        	 c1.run();
        	 c2.run();
        	 c3.run();
        	 c4.run();
        }
        
    }
}