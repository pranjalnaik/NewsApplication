package com.pjnaik.newsaggregator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Data extends Fragment implements View.OnClickListener{
    ImageView picturenews;
    private Articles articlelist;

    public static final Data newInstance(Articles articles, String indices, String total) {

        Data data = new Data();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(getArticlesdata(), articles);
        bundle.putString(getPositiondata(), indices);
        bundle.putString(getNewsdata(), total);
        data.setArguments(bundle);
        return data;
    }

    public static final String articlesdata = "DATA_ARTICLE_FRAG";
    @NonNull
    private static String getArticlesdata() {

        return articlesdata;
    }
    public static final String newsdata = "DATA_TOTAL";
    @NonNull
    private static String getNewsdata() {

        return newsdata;
    }
    public static final String positiondata = "DATA_INDICE";
    @NonNull
    private static String getPositiondata() {

        return positiondata;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewgroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.news_data, viewgroup, false);
        articlelist = (Articles) getArguments().getSerializable(articlesdata);
        String indice = getArguments().getString(positiondata);
        String newstotal = getArguments().getString(newsdata);
        TextView head = view.findViewById(R.id.head);
        TextView writer = view.findViewById(R.id.writer);
        TextView timestamp = view.findViewById(R.id.timestamp);
        TextView description = view.findViewById(R.id.article_preview);
        TextView number = view.findViewById(R.id.article_count);
        picturenews = view.findViewById(R.id.picture);

        head.setOnClickListener(this);
        description.setOnClickListener(this);
        picturenews.setOnClickListener(this);

        head.setText(articlelist.getArticletitle());
        head.setText(articlelist.getArticletitle());
        writer.setText(articlelist.getArticlewriter());
        description.setText(articlelist.getArticledata());
        number.setText(Integer.parseInt(indice) + 1 + " of " + newstotal);

        if (articlelist.getArticletimestamp() != null) {
            DateFormat jsondate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            jsondate.setLenient(false);
            DateFormat reformatteddate = new SimpleDateFormat("MMM dd, yyyy hh:mmaa");
            reformatteddate.setLenient(false);
            String reformatted = articlelist.getArticletimestamp();
            Date updateddate;
            int trial = 0;
            int tries = 2;
            boolean bool = false;
            while (!bool) {
                try {
                    updateddate = jsondate.parse(reformatted);
                    timestamp.setText(reformatteddate.format(updateddate));
                    bool = true;
                } catch (ParseException e) {
                    jsondate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                    if (++trial == tries) {
                        bool = true;
                        timestamp.setText("");
                    }
                }
            }
        }
        if (articlelist.getPicture().length() > 0)
            uploadPhoto(articlelist.getPicture(), view);
        else
            uploadPhoto("null", view);
        return view;
    }

    private void uploadPhoto(String url, View v) {
        Picasso picasso = new Picasso.Builder(this.getContext()).listener((picasso1, uri, exception) -> picasso1.load(R.drawable.brokenimage).into(picturenews)).build();
        picasso.load(url).error(R.drawable.brokenimage).placeholder(R.drawable.loading).into(picturenews);
    }

    @Override
    public void onClick(View view) {
        String articleurl = articlelist.getURLarticle();
        Intent articleintent = new Intent(Intent.ACTION_VIEW);
        articleintent.setData(Uri.parse(articleurl));
        startActivity(articleintent);
    }

    @Override
    public void onSaveInstanceState(Bundle savestate) {

        super.onSaveInstanceState(savestate);
    }

    @Override
    public void onActivityCreated(Bundle articlebundle) {
        super.onActivityCreated(articlebundle);
        if (articlebundle != null) { }
    }

}