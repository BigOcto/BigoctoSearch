package com.bigocto.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


/**
 * @author bigocto(zhangyu)
 * @date 2014/6
 */
public class onePageCrawler extends crawler implements crawler.pageCrawlerListener {

    @Override
    public void parseH5() {
        {
            String url = "";
            boolean list_bool = false;
            boolean status_bool = false;

            try {
                url = deQueueUrl();//out of queue
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            list_bool = list_filter(url, m_list);
            status_bool = http_status_filter(url);
            if (list_bool) {
                if (status_bool) {
                    try {
                        parseH5Urls(url);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    m_list.add(url);//the url that is already traveled
                }
            }
        }
    }

    public article parseH5Content(String url) {

        return null;
    }

    public void parseH5Urls(String url) throws IOException {

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("a[target=_blank]");
        System.out.println("Fetching : " + url);
        int i = 0;
        for (Element link : elements) {
            //mQueue.add(link.attr("abs:href"));
            if (!link.attr("abs:href").equals("") && link.attr("abs:href") != null) {
                if (link.attr("abs:href").contains("/chaxun/list")) {
                    enQueueUrl(link.attr("abs:href"));
                    System.out.println(" URL: " + (++i) + " " + link.attr("abs:href"));
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {

        String url = "http://www.shicimingju.com/chaxun/zuozhe/13046.html";
        mQueue.add(url);

        onePageCrawler c2 = new onePageCrawler();
        c2.setmListener(c2);
        c2.doSearch();
    }
}