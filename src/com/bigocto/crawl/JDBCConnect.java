package com.bigocto.crawl;
import java.sql.*;
/**
 * @author Big_Octos
 * @date 2014/6
 */
public class JDBCConnect {

	private int item_id = 0,term_id = 0,doc_id = 0,term_frequency = 0;
	private String sql_url="";
	private String term_value="";
	private int link_id = 0, from_doc_id = 0, to_doc_id = 0;
	public static int dic_id=1;
	private Statement statement;
	private Statement statement2;
	private Statement statement3;
	private Statement statement4;
	private Statement statement5;
	private Connection conn;
	public JDBCConnect(){
		String driver="com.mysql.jdbc.Driver";
		String url="jdbc:mysql://127.0.0.1:3306/crawler";
		String user="root";
		String password="root";
		
		try{
			
			Class.forName(driver);
			conn=DriverManager.getConnection(url,user,password);
		
			if(conn.isClosed()){
				System.out.println("False connect database");
			}
			
			statement= conn.createStatement();
			statement2= conn.createStatement();
			statement3= conn.createStatement();
			statement4= conn.createStatement();
			statement5= conn.createStatement();
			
//			while(re.next()){
//				System.out.println(re.getString("doc_id")+" "+re.getString("url"));
//			}
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void posting_list_up(int item_id2, int term_id2, int doc_id2, int term_frequency2){
		//item_id, term_id, doc_id, term_frequency
		String posting_list_sql="insert crawler.posting_list values ('"+item_id2+"', '"+term_id2+"','"+doc_id2+"','"+term_frequency2+"');";
		try {
			int posting_re=statement.executeUpdate(posting_list_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void documents_up(int oc_id2,int doc_id2, String sql_url2){
		
		//doc_id, urls
		String documents_sql="insert crawler.document values ('"+oc_id2+"','"+doc_id2+"', '"+sql_url2+"')";
		
		try {
			int documents_re=statement3.executeUpdate(documents_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void dictionary_up(int dic_id2, int term_id2, String term_value2){
		//term_id, term_value
		String dictionary_sql="insert crawler.dictionary values ('"+dic_id2+"','"+term_id2+"', '"+term_value2+"')";
		
		try {
			int dictionary_re=statement.executeUpdate(dictionary_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void links_up(int link_id2, int from_doc_id2, int to_doc_id2){
		//link_id, from_doc_id, to_doc_id
		String links_sql="insert crawler.links values ('"+link_id2+"', '"+from_doc_id2+"', '"+to_doc_id2+"')";
		try {
			int links_re=statement.executeUpdate(links_sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int Term_value_unique(String term, int item_id2, int term_id2, int doc_id2, int term_frequency2){
		
		int i=0;
		int term_id=0;
		String select_d="(SELECT * FROM crawler.dictionary where term_value='"+term+"')";
		
		try {
			
			ResultSet links_re2=statement2.executeQuery(select_d);
			
			while(links_re2.next()){
				
				term_id=links_re2.getInt("term_id");
				i++;

			}
			if(i==1){
				posting_list_up(item_id2, term_id, doc_id2, term_frequency2);//if already exist in dictionarys table
			}else{
				posting_list_up(item_id2, term_id2, doc_id2, term_frequency2);//if already exist in dictionarys table
				dictionary_up(dic_id, term_id2, term);
				dic_id++;
			}

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return term_id;
		
	}
	
	public int Doc_url_unique(int oc_id, int doc_id,String url){
		
		int i=0;
		int doc_id2=0;
		String select_d="(SELECT * FROM crawler.document where url='"+url+"')";
		
		try {
			
			ResultSet url_re=statement4.executeQuery(select_d);
			
			while(url_re.next()){
				
				doc_id2=url_re.getInt("doc_id");
				i++;

			}
			if(i==0){
				
				documents_up(oc_id,doc_id, url);
				oc_id++;
			}

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return oc_id;
		
	}
	
public void Serch_Input(String str){
		
		int count=0;
		String search_in="SELECT url,term_frequency from crawler.document ,crawler.dictionary,crawler.posting_list"
					+" where crawler.dictionary.term_id=crawler.posting_list.term_id"
					+" and crawler.document.doc_id=crawler.posting_list.doc_id"
					+" and crawler.dictionary.term_value='���ϴ�ѧ'"
					+" order by term_frequency desc;";
		
		try {
			
			ResultSet url_re=statement5.executeQuery(search_in);
			
			while(url_re.next()){
				count++;
				System.out.println(count+":  "+url_re.getString("url"));
			}
			
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JDBCConnect jd=new JDBCConnect();
		jd.posting_list_up(1, 1, 1, 1);
		jd.dictionary_up(1,1, "sss");
		jd.documents_up(1,1, "dasdsadas");
		jd.links_up(1, 1, 1);
	}
}
