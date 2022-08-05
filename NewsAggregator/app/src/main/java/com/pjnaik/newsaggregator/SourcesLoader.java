package com.pjnaik.newsaggregator;

import android.net.Uri;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SourcesLoader {
    private MainActivity mainActivity;
    private String url = "https://newsapi.org/v2/sources";
    private final String TAG = "CountryLoaderRunnable";
    private String apikey = "60b4993799e74ddb99cd88e9c4be604d";
    private ArrayList<Sources> sourcelist = new ArrayList<>();
    private ArrayList<String> categorylist = new ArrayList<>();
    private String category;

    public SourcesLoader(MainActivity mainactivity, String category) {
        this.mainActivity = mainactivity;
        if (category.isEmpty() || category.equals("all")) { this.category = ""; }
        else { this.category = category; }
    }

    public void getSourceData(MainActivity mainActivity) {

        RequestQueue queue = Volley.newRequestQueue(mainActivity);
        Uri.Builder buildURL = Uri.parse(url).buildUpon();
        buildURL.appendQueryParameter("category", category);
        buildURL.appendQueryParameter("apiKey", apikey);
        String finalurl = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> handleResults(response.toString());

        Response.ErrorListener error = error1 -> {
            Log.d(TAG, "getSourceData: ");
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(new String(error1.networkResponse.data));
                Log.d(TAG, "getSourceData: " + jsonObject);
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

    private void parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonObjectJSONArray = jsonObject.getJSONArray("sources");

            for (int i = 0; i < jsonObjectJSONArray.length(); i++) {
                Sources sources = new Sources();
                sources.setSourceid(jsonObjectJSONArray.getJSONObject(i).getString("id"));
                sources.setNews(jsonObjectJSONArray.getJSONObject(i).getString("name"));
                sources.setURLnews(jsonObjectJSONArray.getJSONObject(i).getString("url"));
                sources.setCategory(jsonObjectJSONArray.getJSONObject(i).getString("category"));
                sourcelist.add(sources);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < sourcelist.size(); i++) {
            mainActivity.setTitle("News Gateway" + " (" + sourcelist.size() + ")");
            if (!categorylist.contains(sourcelist.get(i).getCategory())) {
                categorylist.add(sourcelist.get(i).getCategory());
            }
        }
    }

    private void handleResults(String result) {
        parseJSON(result);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() { mainActivity.setResources(sourcelist, categorylist); }
        }
        );
    }
}
