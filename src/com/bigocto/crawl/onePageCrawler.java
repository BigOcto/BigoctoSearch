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
                            if(urlObj.getDepth() < mSelectList.size()-1){
                                parseH5Urls(mSelectList.get(urlObj.getDepth()), urlObj);
                            }
                            if (urlObj.getDepth() == mSelectList.size()-1){
                                parseH5Content(mSelectList.get(urlObj.getDepth()),urlObj);
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

    public article parseH5Content(selectObj selectObj, urlObj urlObj) throws IOException {
        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        System.out.println("Fetching : " + urlObj.getUrl());
        for (int i = 0; i< elements.size(); i++){
            Element element = elements.get(i);
            if (i==0){
                JDBCConnect.getInstance().
            }
        }
        for (Element link : elements) {
            System.out.println(link.text());
        }
        return null;
    }

    public void parseH5Urls(selectObj selectObj, urlObj urlObj) throws IOException {

        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        System.out.println("Fetching : " + urlObj.getUrl());
        int i = 0;
        for (Element link : elements) {
            //mQueue.add(link.attr("abs:href"));
            if (!link.attr("abs:href").equals("") && link.attr("abs:href") != null) {
                if (link.attr("abs:href").contains(selectObj.getUrlContain())) {
                    urlObj ob = new urlObj();
                    ob.setDepth(selectObj.getParseDepth()+1);
                    ob.setUrl(link.attr("abs:href"));
                    enQueueUrl(ob);
                    System.out.println(" URL: " + (++i) + " " + link.attr("abs:href"));
                }
            }
        }
    }

    private String getUrlNames(String url){
        String[] s = url.split("\\.", 2);
        return s[1];
    }

}