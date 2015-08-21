package com.bigocto.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author bigocto(zhangyu)
 * @date 2014/6
 */
public class onePageCrawler extends crawler implements crawler.pageCrawlerListener {
    List<selectObj> mSelectList = new ArrayList<selectObj>();

    public onePageCrawler(List<selectObj> list){
        this.mSelectList = list;
    }

    @Override
    public void parseH5() {
        {
            urlObj urlObj = null;
            boolean list_bool = false;
            boolean status_bool = false;

            try {
                urlObj = deQueueUrl();//out of queue
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (urlObj !=null && urlObj.getUrl()!= null && !urlObj.getUrl().isEmpty()){
                list_bool = list_filter(getUrlNames(urlObj.getUrl()), m_list);
                status_bool = http_status_filter(urlObj.getUrl());
                if (list_bool) {
                    if (status_bool) {
                        try {
                            //TODO Default last page content parse
                            if(urlObj.getDepth() == 0){
                                for (urlObj url : parseH5Urls(mSelectList.get(urlObj.getDepth()), urlObj)){
                                    enQueueUrl(url);
                                }
                            }

                            if (urlObj.getDepth() == 1){
                                article article = parseH5Introduction(mSelectList.get(3), urlObj);
                                JDBCConnect.getInstance().InsertArticle(article.getName(),article.getAuthor(),article.getYears(),urlObj.getUrl(),article.getInstroduction());

                                int id = JDBCConnect.getInstance().SelctArticleID(urlObj.getUrl());
                                for (urlObj url:parseH5Urls(mSelectList.get(urlObj.getDepth()), urlObj)){
                                    if (list_filter(url.getUrl(),m_list)){
                                        JDBCConnect.getInstance().InsertUrls(id,url.getUrl());
                                        enQueueUrl(url);
                                    }
                                }

                            }
                            if (urlObj.getDepth() == 2){
                                listArticle listArticle = parseH5Content(mSelectList.get(urlObj.getDepth()), urlObj);
                                int id = JDBCConnect.getInstance().SelctUrls(urlObj.getUrl());
                                JDBCConnect.getInstance().InsertContent(listArticle.getList_name(), listArticle.getContent(),id);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        m_list.add(getUrlNames(urlObj.getUrl()));//the url that is already traveled
                    }
                }
            }
        }
    }

    public article parseH5Introduction(selectObj selectObj, urlObj urlObj) throws IOException {
        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        System.out.println("Fetching : " + urlObj.getUrl());

        article article  = new article();
        for (int i =0; i< elements.size(); i++){
            Element element = elements.get(i);
            String t = element.text();
            switch (i){
                case 0:
                    article.setName(t.substring(1,t.length()-1));
                    break;
                case 1:
                    article.setInstroduction(t.substring(3,t.length()));
                    break;
                case 2:
                    article.setAuthor(t.substring(3,t.length()));
                    break;
                case 3:
                    article.setYears(t.substring(3,t.length()));
                    break;
            }
        }
        for (Element link : elements) {
            System.out.println(link.text());
        }
        return article;
    }
    /**
     * Parse article content
     */
    public listArticle parseH5Content(selectObj selectObj, urlObj urlObj) throws IOException {
        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        System.out.println("Fetching : " + urlObj.getUrl());
        String list_name = null;
        StringBuffer content = new StringBuffer();
        for (int i = 0; i< elements.size(); i++){
            Element element = elements.get(i);
            if (i==0){
                list_name = element.text();
            }else {
                content.append(element.text());
            }
        }
        listArticle listArticle = new listArticle();
        listArticle.setList_name(list_name);
        listArticle.setContent(String.valueOf(content));
        for (Element link : elements) {
            System.out.println(link.text());
        }
        return listArticle;
    }

    public List<urlObj> parseH5Urls(selectObj selectObj, urlObj urlObj) throws IOException {

        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        List<urlObj> list = new ArrayList<urlObj>();
        System.out.println("Fetching : " + urlObj.getUrl());
        int i = 0;
        for (Element link : elements) {
            //mQueue.add(link.attr("abs:href"));
            if (!link.attr("abs:href").equals("") && link.attr("abs:href") != null) {
                if (link.attr("abs:href").contains(selectObj.getUrlContain())) {
                    urlObj ob = new urlObj();
                    ob.setDepth(selectObj.getParseDepth()+1);
                    ob.setUrl(link.attr("abs:href"));
                    list.add(ob);
//                    enQueueUrl(ob);
                    System.out.println(" URL: " + (++i) + " " + link.attr("abs:href"));
                }
            }
        }
        return list;
    }

    private String getUrlNames(String url){
        String[] s = url.split("\\.", 2);
        return s[1];
    }

}