package com.pjnaik.newsaggregator;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import java.io.Serializable;
import java.util.ArrayList;

import static com.pjnaik.newsaggregator.MainActivity.servicecall;
import static com.pjnaik.newsaggregator.MainActivity.articlenews;
import static com.pjnaik.newsaggregator.MainActivity.sourceapi;
import static com.pjnaik.newsaggregator.MainActivity.articledata;

public class NewsDisplay extends Service {

    private boolean bool = true;
    private ServiceReceiver servicer;
    private ArrayList<Articles> articlelist = new ArrayList<>();

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        servicer = new ServiceReceiver();
        IntentFilter intentfilter = new IntentFilter(servicecall);
        registerReceiver(servicer, intentfilter);
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            stopSelf();
            unregisterReceiver(servicer);
            bool = false;
            super.onDestroy();
        }catch(IllegalArgumentException e){ }
    }

    public void articleupdate(ArrayList<Articles> arrayList) {
        articlelist.clear();
        articlelist = new ArrayList<>(arrayList);
        Intent intent = new Intent(articlenews);
        intent.putExtra(articledata, (Serializable) articlelist);
        sendBroadcast(intent);
        articlelist.clear();
    }

    public class ServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == servicecall) {
                    if (intent.hasExtra(sourceapi)) {
                        Sources newssources = (Sources) intent.getSerializableExtra(sourceapi);
                        ArticlesLoader articlesLoader = new ArticlesLoader(NewsDisplay.this, "" + newssources.getSourceid());
                        articlesLoader.getSourceData();
                    }
            } }
    }}