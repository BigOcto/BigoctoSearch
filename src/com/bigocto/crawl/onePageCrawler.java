package com.bigocto.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
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
                list_bool = list_filter(urlObj.getUrl(), m_list);
                status_bool = http_status_filter(urlObj.getUrl());
                if (list_bool) {
                    if (status_bool) {
                        try {
                            if(urlObj.getDepth() < mSelectList.size()){
                                parseH5Urls(mSelectList.get(urlObj.getDepth()), urlObj);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        m_list.add(urlObj.getUrl());//the url that is already traveled
                    }
                }
            }
        }
    }

    public article parseH5Content(selectObj selectObj, urlObj urlObj) throws IOException {
        Document doc = Jsoup.connect(urlObj.getUrl()).get();
        Elements elements = doc.select(selectObj.getDocumentSelect());
        System.out.println("Fetching : " + urlObj.getUrl());
        int i = 0;
        for (Element link : elements) {

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
                    ob.setDepth(selectObj.getParseDepth());
                    ob.setUrl(link.attr("abs:href"));
                    enQueueUrl(ob);
                    System.out.println(" URL: " + (++i) + " " + link.attr("abs:href"));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        String url = "http://www.shicimingju.com/book/sanguoyanyi/1.html";
        urlObj urlObj = new urlObj();
        urlObj.setUrl(url);
        urlObj.setDepth(0);
        mQueue.add(urlObj);

        selectObj s1 = new selectObj(1, "a[href]", "/book/");
        selectObj s2 = new selectObj(2, "a[href]", "/book/");
        selectObj s3 = new selectObj(1, "div.bookyuanjiao h2 , p", "");
        List<selectObj> list = new ArrayList<selectObj>();
        list.add(s1);
        list.add(s2);
        list.add(s3);

        onePageCrawler c2 = new onePageCrawler(list);
        c2.setmListener(c2);
        c2.doSearch();
        c2.setThreadNum(5);
    }
}