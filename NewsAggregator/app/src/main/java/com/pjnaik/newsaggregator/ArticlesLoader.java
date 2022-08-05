package com.pjnaik.newsaggregator;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticlesLoader {

    private NewsDisplay newsdisplay;
    private final String TAG = "CountryLoaderRunnableArticles";
    private String url = "https://newsapi.org/v2/top-headlines?";
    private String apikey = "60b4993799e74ddb99cd88e9c4be604d";
    private ArrayList<Articles> articlelist = new ArrayList<>();
    private String newschannels;

    public ArticlesLoader(NewsDisplay newsdisplay, String newschannels) {

        this.newsdisplay = newsdisplay;
        this.newschannels = newschannels;
    }

    public void getSourceData() {

        RequestQueue queue = Volley.newRequestQueue(newsdisplay);

        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("sources", newschannels);
        buildURL.appendQueryParameter("apiKey", apikey);
        String finalurl = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResults(response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceDataArt: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceDataArt: " + jsonObject);
                handleResults(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        };

        JsonObjectRequest jsonArrayRequest =
                new JsonObjectRequest(Request.Method.GET, finalurl,
                        null, listener, error){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("User-Agent", "");
                        return headers;
                    }
                };

        queue.add(jsonArrayRequest);
    }
    private void handleResults(String result) {
        parseJSON(result);
        newsdisplay.articleupdate(articlelist);
    }

    private void parseJSON(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            JSONArray jObjectJSONArray = jsonObject.getJSONArray("articles");
            for (int i = 0; i < jObjectJSONArray.length(); i++) {
                Articles articles = new Articles();
                articles.setArticlewriter(jObjectJSONArray.getJSONObject(i).getString("author"));
                articles.setArticletitle(jObjectJSONArray.getJSONObject(i).getString("title"));
                articles.setURLarticle(jObjectJSONArray.getJSONObject(i).getString("url"));
                articles.setPicture(jObjectJSONArray.getJSONObject(i).getString("urlToImage"));
                articles.setArticletimestamp(jObjectJSONArray.getJSONObject(i).getString("publishedAt"));
                articles.setArticledata(jObjectJSONArray.getJSONObject(i).getString("description"));
                articlelist.add(articles);
            }
        } catch (Exception exception) { exception.printStackTrace(); }
    }
}
