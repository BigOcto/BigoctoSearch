package com.bigocto.crawl;

import java.util.ArrayList;
import java.util.List;

public class Search_Impl {

	public static void main(String[] args) {
		String url = "http://www.shicimingju.com/book/";
		urlObj urlObj = new urlObj();
		urlObj.setUrl(url);
		urlObj.setDepth(0);

		selectObj s1 = new selectObj(0, "a[href]", "/book/");
		selectObj s2 = new selectObj(1, "a[href]", "/book/");
		selectObj s3 = new selectObj(2, "div.bookyuanjiao h2 , p", "");
		List<selectObj> list = new ArrayList<selectObj>();
		list.add(s1);
		list.add(s2);
		list.add(s3);

		onePageCrawler c2 = new onePageCrawler(list);
		onePageCrawler.mQueue.add(urlObj);
		c2.setmListener(c2);
		c2.doSearch();
		c2.setThreadNum(5);
	}

}
