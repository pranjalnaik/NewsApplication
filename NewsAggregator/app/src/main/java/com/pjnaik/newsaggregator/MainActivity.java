package com.pjnaik.newsaggregator;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerlayout;
    private ListView listview;
    static final String servicecall = "ACTION_SERVICE_MSG";
    static final String articlenews = "ACTION_STORY_NEWS";
    static final String sourceapi = "DATA_SOURCE";
    static final String articledata = "NEWS_DATA_ARTICLE";
    private ArrayList<String> textlist = new ArrayList<>();
    private Menu menu;
    private ArrayList<Articles> articleslist = new ArrayList<>();
    private Input input;
    private ArrayList<String> categorylist = null;
    private PageAdapter pageadapter;
    private ActionBarDrawerToggle actionbar;
    private HashMap<String, Sources> hashmap = new HashMap<String, Sources>();
    private List<Fragment> fraglist;
    private ViewPager viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, NewsDisplay.class);
        startService(intent);
        input = new Input();
        IntentFilter intentf = new IntentFilter(articlenews);
        registerReceiver(input, intentf);
        drawerlayout = findViewById(R.id.drawlayout);
        listview = findViewById(R.id.drawsources);
        listview.setAdapter(new ArrayAdapter<>(this, R.layout.drawer, textlist));
        listview.setOnItemClickListener(
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { selectItem(position); }
                });
        actionbar = new ActionBarDrawerToggle(this, drawerlayout, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (networkStatus()) {
            if (savedInstanceState != null) {
                setTitle(savedInstanceState.getCharSequence("title"));
                setResources((ArrayList<Sources>) savedInstanceState.getSerializable("sourcelist"),
                        savedInstanceState.getStringArrayList("categorylist"));
            } else {
//                int k = sourcelist.size();
//                setTitle("News Gateway" + " (" + sourcelist.size() + ")");
                SourcesLoader sourcesLoader = new SourcesLoader(this, "");
                sourcesLoader.getSourceData(this);
            }
        } else { dialogBox(); }
        fraglist = getFraglist();
        pageadapter = new PageAdapter(getSupportFragmentManager());
        viewer = (ViewPager) findViewById(R.id.mainviewpage);
        viewer.setAdapter(pageadapter);
        viewer.setOffscreenPageLimit(10);

        if (savedInstanceState != null) {
            for (int i = 0; i < savedInstanceState.getInt("size"); i++) { fraglist.add(getSupportFragmentManager().getFragment(savedInstanceState,
                    "NewsFragment" + Integer.toString(i))); }
        } else { viewer.setBackgroundResource(R.drawable.newspaper); }
        pageadapter.notifyDataSetChanged();
    }

    public void dialogBox()
    {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(this);
        alertdialogbuilder.setTitle("Network Error");
        alertdialogbuilder.setMessage("Network Issue. Please retry.");
        alertdialogbuilder.setNegativeButton(Html.fromHtml("<font color='#254E58'>OK</font>"),
                new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int arg1) { }});
        alertdialogbuilder.show();
        AlertDialog alertdialog = alertdialogbuilder.create();
        alertdialog.show();
    }

    @Override
    protected void onPostCreate(Bundle postcreatestate) {
        super.onPostCreate(postcreatestate);
        actionbar.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configchange) {
        super.onConfigurationChanged(configchange);
        actionbar.onConfigurationChanged(configchange);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mainmenu) {
        this.menu = mainmenu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionbar.onOptionsItemSelected(item)) { return true; }
        SourcesLoader sourceloader = new SourcesLoader(this, "" + item);
        sourceloader.getSourceData(this);
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int number) {
        viewer.setBackground(null);
        setTitle(textlist.get(number));
        Intent mainintent = new Intent(servicecall);
        mainintent.putExtra(sourceapi, hashmap.get(textlist.get(number)));
        sendBroadcast(mainintent);
        drawerlayout.closeDrawer(listview);
    }

    public void setResources(ArrayList<Sources> sourcelist, ArrayList<String> categorylist) {
        textlist.clear();
        hashmap.clear();
        Collections.sort(sourcelist);
        for (Sources source : sourcelist) {
            textlist.add(source.getNews());
            hashmap.put(source.getNews(), source); }
        ((ArrayAdapter<String>) listview.getAdapter()).notifyDataSetChanged();
        if (this.categorylist == null) {
            this.categorylist = new ArrayList<>(categorylist);
            if (menu != null) {
                this.categorylist.add(0, "all");
                for (String c : this.categorylist) {
                    menu.add(c);
            }
         }
    }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu mainmenu) {
        if (this.categorylist != null) {
            mainmenu.clear();
            for (String categorylist : this.categorylist) { mainmenu.add(categorylist); }
        }
        return super.onPrepareOptionsMenu(mainmenu);
    }

    private List<Fragment> getFraglist() {
        List<Fragment> fraglist = new ArrayList<Fragment>();
        return fraglist;
    }

    @Override
    protected void onDestroy() {
        try{
            super.onDestroy();
            Intent mainintent = new Intent(MainActivity.this, NewsDisplay.class);
            stopService(mainintent);
            unregisterReceiver(input);
        }
        catch(IllegalArgumentException e){ }
    }

    @Override
    protected void onStop() {
        try{
            super.onStop();
            Intent mainintent = new Intent(MainActivity.this, NewsDisplay.class);
            stopService(mainintent);
            unregisterReceiver(input);
            }
        catch(IllegalArgumentException e){ }
    }

    @Override
    protected void onPause() {
        try{
            unregisterReceiver(input);
            super.onPause();
        }
        catch(IllegalArgumentException e){ }
    }

    public boolean networkStatus() {
        boolean bool = true;
        ConnectivityManager connectivitymanager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnectedOrConnecting()) { return bool; }
        else { return !bool; }
    }

    @Override
    public void onSaveInstanceState(Bundle bundlesavedInstanceState) {
        super.onSaveInstanceState(bundlesavedInstanceState);
        int total = 0;
        int loop = 0;
        for (loop = 0; loop < fraglist.size(); loop++)
        {
            if (fraglist.get(loop).isAdded())
            {
                total++;
                String newsfrag = "NewsFragment" + loop;
                getSupportFragmentManager().putFragment(bundlesavedInstanceState, newsfrag, fraglist.get(loop));
            }
        }
        bundlesavedInstanceState.putInt("size", total);
        bundlesavedInstanceState.putStringArrayList("categorylist", categorylist);
        ArrayList<Sources> sourcelist = new ArrayList<>();
        for (String hasher : hashmap.keySet()) { sourcelist.add(hashmap.get(hasher)); }
        bundlesavedInstanceState.putSerializable("sourcelist", sourcelist);
        bundlesavedInstanceState.putCharSequence("title", getTitle());
    }

    public void setTitle() {
    }

    public class Input extends BroadcastReceiver {
        @Override
        public void onReceive(Context inputcontext, Intent inputintent) {
            switch (inputintent.getAction()) {
                case articlenews:
                    if (inputintent.hasExtra(articledata)) {
                        articleslist = (ArrayList) inputintent.getSerializableExtra(articledata);
                        changeFragment(articleslist);
                    }
                    break;
            }
        }

        private void changeFragment(List<Articles> articlelist) {
            if(!networkStatus())
              dialogBox();
            int adapterloop = 0;
            for (adapterloop = 0; adapterloop < pageadapter.getCount(); adapterloop++)
            {

                pageadapter.notifyChangeInPosition(adapterloop);
            }
            fraglist.clear();

            int fragloop = 0;
            for (fragloop = 0; fragloop < articlelist.size(); fragloop++)
              fraglist.add(Data.newInstance((articlelist.get(fragloop)), "" + fragloop, "" + articlelist.size()));
            pageadapter.notifyDataSetChanged();
            viewer.setCurrentItem(0);
        }
    }

    private class PageAdapter extends FragmentPagerAdapter {
        private long item = 0;
        public PageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object)
        {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position)
        {
            return fraglist.get(position);
        }

        @Override
        public int getCount()
        {
            return fraglist.size();
        }

        @Override
        public long getItemId(int position)
        {
            return item + position;
        }

        public void notifyChangeInPosition(int position)
        {
            item += getCount() + position;
        }

    }

}