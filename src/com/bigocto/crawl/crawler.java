package com.bigocto.crawl;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by bigocto(zhangyu)
 * on 2015/8/12.
 */
public abstract class crawler{
    public int mThreadNum = 1;
    public int mParseMostDepth = 1;
    public static Queue<urlObj> mQueue = new LinkedList<urlObj>();
    public static List<String> m_list = new LinkedList<String>();
    public static List<String> mTimeOutList = new LinkedList<String>();

    public pageCrawlerListener mListener;

    public void setThreadNum(int threadNum) {
        mThreadNum = threadNum;
    }

    public void setmParseMostDepth(int mParseMostDepth) {
        this.mParseMostDepth = mParseMostDepth;
    }

    public static boolean list_filter(String url, List<String> list) {
        if(list.size() > 1000){
            for (int i = 0; i< list.size()-1000; i++){
                list.remove(i);
            }
        }
        for (String aList : list) {
            if (url.equals(aList)) {
                return false;
            }
        }
        return true;
    }

    /**
     * URL out queue
     * @return URL
     * @throws Exception
     */
    public synchronized urlObj deQueueUrl() throws Exception {
        if (mQueue != null) {
            urlObj  urlObj = mQueue.poll();
            notifyAll();
            return urlObj;
        }else {
            wait();
        }
        return null;
    }

    /**
     * Already parsed URL join queue
     * @param urlObj Not parse URL
     */
    public synchronized void enQueueUrl(urlObj urlObj) {
        if (!mQueue.contains(urlObj)) {
            if (!urlObj.getUrl().equals("") && !urlObj.equals(null)) {
                mQueue.add(urlObj);
                notifyAll();
            }
        }
    }

    /**
     * Test URL status
     */
    public Boolean http_status_filter(String url){

        HttpResponse response;
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget=new HttpGet(url);
            httpget.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);

            response = httpclient.execute(httpget);
            StatusLine status=response.getStatusLine();
            System.out.print(status.getStatusCode());
            if(status.getStatusCode()==200){
                return true;
            }else{
                return false;
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mTimeOutList.add(url);//timeout urls are saved

            m_list.add(url);//timeout urls are saved in already used url

            System.out.println("TimeOut url :"+url);
            return false;
        }
    }

    public void setmListener(pageCrawlerListener mListener) {
        this.mListener = mListener;
    }

    public void doSearch() {
        for (int i = 0; i < mThreadNum; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        mListener.parseH5();
                    }
                }
            }).start();
        }
    }

    public interface pageCrawlerListener{
        public void parseH5();
    }
}
