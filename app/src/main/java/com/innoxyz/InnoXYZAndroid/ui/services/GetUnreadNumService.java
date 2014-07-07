package com.innoxyz.InnoXYZAndroid.ui.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.MessageCount;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.notify.NotifyCount;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;

import java.util.concurrent.TimeUnit;

/**
 * Created by laborish on 14-1-16.
 */
public class GetUnreadNumService extends Service {

    public static final String UNREAD_COUNT ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_COUNT";

    public static final String UNREAD_NOTIFIES ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_NOTIFIES";
    public static final String UNREAD_MAILS ="com.innoxyz.InnoXYZAndroid.broadcast.UNREAD_MAILS";


    private SimpleObservedData<NotifyCount> unReadNotifies;
    private SimpleObservedData<MessageCount> unReadMails;

    private ScanningThread runner;

    @Override
    public void onCreate() {

        //Log.e("","in service create!!!");

        unReadNotifies = new SimpleObservedData<NotifyCount>(new NotifyCount());
        unReadMails = new SimpleObservedData<MessageCount>(new MessageCount());


        unReadNotifies.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {

                int count = ((NotifyCount) o).total;

                Intent i = new Intent();
                i.setAction(UNREAD_COUNT);
                i.putExtra(UNREAD_NOTIFIES, count);

                sendBroadcast(i);

                //Log.e("", "sent Broadcast UNREAD_NOTIFIES = " + count);
            }
        });
        unReadMails.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {

                int count = ((MessageCount)o).unreads;

                Intent i = new Intent();
                i.setAction(UNREAD_COUNT);
                i.putExtra(UNREAD_MAILS, count);

                sendBroadcast(i);

                //Log.e("", "sent Broadcast UNREAD_MAILS = " + count);
            }
        });


        startThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //release, let the thread getFromWeb right now
        InnoXYZApp.getApplication().getGetUnreadNumNow().release();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopThread();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    class ScanningThread extends Thread {
        private volatile boolean stop =  false;

        public void run(){
            while(!stop){
                try{
                    //wait Semaphore or timeout
                    if( InnoXYZApp.getApplication().getGetUnreadNumNow().tryAcquire(1, 30, TimeUnit.SECONDS) ){
                        //InnoXYZApp.getApplication().getGetUnreadNumNow().acquire();
                    }
                }
                catch (InterruptedException e){

                }
                new StringRequestBuilder(null).setRequestInfo(AddressURIs.LIST_NOTIFY_COUNT)
                        .addParameter("read", "false")
                        .setOnResponseListener(new JsonResponseHandler(unReadNotifies, NotifyCount.class, null))
                        .request();

                new StringRequestBuilder(null).setRequestInfo(AddressURIs.LIST_MESSAGE_COUNT)
                        .addParameter("read", "false")
                        .setOnResponseListener(new JsonResponseHandler(unReadMails, MessageCount.class, null))
                        .request();

            }
        }

        public synchronized void requestStop() {
            stop = true;
        }
    }

    public synchronized void startThread(){
        if(runner == null){
            runner = new ScanningThread();
            runner.start();
        }
    }

    public synchronized void stopThread(){
        if(runner != null){
            runner.requestStop();
            runner = null;
        }
    }

}
