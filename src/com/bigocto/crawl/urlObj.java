package com.bigocto.crawl;

/**
 * Created by zhangyu
 * on 2015/8/17.
 */
public class urlObj {
    String url;
    int depth;   //所属URL存在的页面级数

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
