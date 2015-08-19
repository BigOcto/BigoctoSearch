package com.bigocto.crawl;
import java.sql.*;

/**
 * @author Big_Octos
 * @date 2014/6
 */
public class JDBCConnect {

    private Statement article_ste;
    private Statement tags_ste;
    private Statement content_ste;
    private Statement article_url;

    private static JDBCConnect singleInstance;
    public static JDBCConnect getInstance(){
        if (singleInstance == null){
            synchronized (JDBCConnect.class){
                if (singleInstance == null){
                    singleInstance = new JDBCConnect();
                }
            }
        }
        return singleInstance;
    }
    public JDBCConnect() {
        String driver = "com.mysql.jdbc.Driver";
        String artlcle_url = "jdbc:mysql://localhost:3306/zhangyu_sca";
        String user = "root";
        String password = "root";
        try {
            Class.forName(driver);
            Connection article_conn = DriverManager.getConnection(artlcle_url, user, password);

            if (article_conn.isClosed()) {
                System.out.println("False connect database");
            }

            article_ste = article_conn.createStatement();
            tags_ste = article_conn.createStatement();
            content_ste = article_conn.createStatement();
            article_url = article_conn.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param name  文章名
     * @param author    作者
     * @param years 年代
     */
    public synchronized void InsertArticle(String name, String author, String years, String introduction) {
        String insert_article = "INSERT INTO zhangyu_sca.article (name, author, years , introduction) values ( '" + name + "','" + author + "' , '" + years + "'," + "'" + introduction + "' );";
        try {
            article_ste.executeUpdate(insert_article);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param list_name 章节名
     * @param content 内容
     * @param name  书名
     * @param author    作者
     */
    public synchronized void InsertContent(String list_name, String content, String name, String author) {

        String select_article_id = "SELECT id from zhangyu_sca.article WHERE (name =  '" + name + "' and  author =  '" + author + "' );";

        try {
            ResultSet re = content_ste.executeQuery(select_article_id);
            if (re.next()) {
                int id = re.getInt(1);
                content_ste.executeUpdate("INSERT INTO zhangyu_sca.article_content (article_id , list_name, list_content) values ( '" + id + "' , '" + list_name + "' , '" + content + "');");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void InsertTags(String name, String author, String tags) {
        String select_article_id = "SELECT id from zhangyu_sca.article WHERE (name = '" + name + "' and author = '" + author + "');";
        try {
            ResultSet re = tags_ste.executeQuery(select_article_id);
//			ResultSetMetaData m = re.getMetaData();
//			int count = m.getColumnCount();
//			while (re.next()){
//				for (int i =1; i<= count; i++){
//					System.out.println(re.getInt(i));
//				}
//			}
            if (re.next()) {
                int id = re.getInt(1);
                tags_ste.executeUpdate("INSERT INTO zhangyu_sca.article_tags(article_id, tags) values ('" + id + "' ,'" + tags + "');");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void InsertUrls(String author, String years, String url){
        try {

        }
    }
}
