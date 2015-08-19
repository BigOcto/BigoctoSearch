package com.bigocto.crawl;

/**
 * Created by bigocto(zhangyu)
 * on 2015/8/17.
 * <p/>
 * 每级爬取页面晒选内容
 */
public class selectObj {

    int parseDepth;   //解析URL深度
    String documentSelect;
    String urlContain;

    public selectObj(int parseDepth, String documentSelect, String urlContain) {
        this.parseDepth = parseDepth;
        this.documentSelect = documentSelect;
        this.urlContain = urlContain;
    }

    public int getParseDepth() {
        return parseDepth;
    }

    public void setParseDepth(int parseDepth) {
        this.parseDepth = parseDepth;
    }

    public String getDocumentSelect() {
        return documentSelect;
    }

    public void setDocumentSelect(String documentSelect) {
        this.documentSelect = documentSelect;
    }

    public String getUrlContain() {
        return urlContain;
    }

    public void setUrlContain(String urlContain) {
        this.urlContain = urlContain;
    }
}
