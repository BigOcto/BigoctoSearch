package com.bigocto.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Big_Octos
 * @date 2014/6
 */
public class ParseHtml {

	public static HashMap<String,Integer> map;
	public static int TOTAL_NUM=0;
	public static int item_ID=1;
	public static int oc_ID=1;
	public static int term_ID=1;
	public static int parse(String url,int doc_id) throws IOException{
		
		Document doc = Jsoup.connect(url).get();
		Elements title = doc.select("title");
		Elements a_label = doc.select("a");
		Elements p_label = doc.select("p");
		
		map=new HashMap<String ,Integer>();//lin shi imformation
		
        System.out.println("Fetching"+ TOTAL_NUM+": "+url);
        TOTAL_NUM++;
        
        String str_t="";
        for (Element link : title) {
        	
        	String link_t=link.text().toLowerCase();
        	
        	List<String> list=divide_sentence(link_t);//divide sentence
        	
        	Iterator it=list.iterator();
        	
        	while(it.hasNext()){
        		
        		str_t=it.next().toString();
            	Boolean bool_t=map_exit(str_t,map);
            	
            	if(bool_t==false){
            		int in=map.get(str_t);
            		in++;
            		map.put(str_t, in);
            		
            		
            	}else{
            		map.put(str_t,1);
            		
                   // System.out.println(str_t);
            	}
        	}
        }
        
        
        String str_a="";
        for (Element link : a_label) {
        	
        	String link_a=link.text().toLowerCase();
     	
        	List<String> list=divide_sentence(link_a);//divide sentence
        	
        	Iterator it=list.iterator();
        	
        	while(it.hasNext()){
        		
        		str_a=it.next().toString();
        		
        		//System.out.println(str_a);
        		
		    	Boolean bool_a=map_exit(str_a,map);
		    	
		    	if(bool_a==false){
		    		int in=map.get(str_a);
		    		in++;
		    		map.put(str_a, in);
		    		
		    		//System.out.println(str_a);
		    	}else{
		    		
		    		map.put(str_a,1);
		    		//System.out.println(str_a);
		    	}
        	}
        }
        
        
        String str_p="";
        for (Element link : p_label) {
        	
        	String link_p=link.text().toLowerCase();
        	
        	List<String> list=divide_sentence(link_p);//divide sentence
        	
        	Iterator it=list.iterator();
        	
        	while(it.hasNext()){
        		
        		str_p=it.next().toString();
        		
	        	Boolean bool_p=map_exit(str_p,map);
	        	
	        	if(bool_p==false){
	        		int in=map.get(str_p);
	        		in++;
	        		map.put(str_p, in);
	        		
	        		//System.out.println(str_p);
	        		
	        	}else{
	        		map.put(str_p,1);
	        		
	        		//System.out.println(str_p);
	        	}
        	 
        	}
        }
        
        
        /*************connect mysql************/
    	JDBCConnect jd=new JDBCConnect();
    	

    	Iterator it_=map.entrySet().iterator();
    	Map.Entry entry;
    	while(it_.hasNext()){
    		entry=(Entry) it_.next();

    	 	//jd.dictionary_up(term_ID, entry.getKey().toString());
            term_ID++;
            item_ID++;
    	}
   
        return term_ID;
	}
	
	public static Boolean map_exit(String str, HashMap<String, Integer>map){
		
		Iterator it=map.entrySet().iterator();
		@SuppressWarnings("rawtypes")
		Map.Entry entry;
		while(it.hasNext()){
			entry=(Entry) it.next();
			String str2=entry.getKey().toString();
			if(str2.equals(str)){
				return false;//Already exist
			}
		}
		return true;
		
	}
	public static List<String> divide_sentence(String str){
		List<String> list=new ArrayList<String>();
		list.clear();
		StringTokenizer token=new StringTokenizer(str, " , . ; : : + - * / @ ! & ( ) [ ]");
		
		while (token.hasMoreTokens()){
			list.add(token.nextToken());
		}
		
		return list;
	}
}
