package com.bigocto.crawl;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by zhangyu
 * on 2015/8/12.
 */
public abstract class crawler {
    public int mThreadNum = 0;
    public static Queue<String> mQueue = new LinkedList<String>();
    public static List<String> m_list = new LinkedList<String>();
    public static List<String> mTimeOutList = new LinkedList<String>();
    public static int Doc_ID = 1;
    public static ParseHtml parseH = new ParseHtml();

    public void setThreadNum(int threadNum) {
        mThreadNum = threadNum;
    }

    public static boolean list_filter(String url, List<String> list) {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            if (url.equals(it.next())) {
                return false;
            }
        }
        return true;
    }


    public synchronized String deQueueUrl() throws Exception {
        if (m_list != null) {
            String url = mQueue.remove();
            return url;
        } else {
            mThreadNum--;
            if (mThreadNum > 0) {
                wait();
                mThreadNum++;
            } else {
                notifyAll();
                return null;
            }
        }
        return null;
    }

    public synchronized void enQueueUrl(String url) {
        if (!mQueue.contains(url)) {
            if (!url.equals("") && !url.equals(null)) {
                Doc_ID++;
                try {
                    parseH.parse(url, Doc_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mQueue.add(url);
                notifyAll();

            }
        }

    }
}
