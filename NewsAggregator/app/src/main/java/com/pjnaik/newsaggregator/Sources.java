package com.pjnaik.newsaggregator;

import java.io.Serializable;

public class Sources implements Serializable, Comparable<Sources> {
    private String sourceid;
    private String news;
    private String URLnews;
    private String category;

    public Sources() {
    }

    public String getSourceid() {

        return sourceid;
    }

    public void setSourceid(String sourceid) {

        this.sourceid = sourceid;
    }

    public String getNews() {

        return news;
    }

    public void setNews(String news) {

        this.news = news;
    }

    public String getURLnews() {

        return URLnews;
    }

    public void setURLnews(String URLnews) {

        this.URLnews = URLnews;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String NewsCategory) {

        this.category = NewsCategory;
    }

    public int compareTo(Sources other) {

        return news.compareTo(other.news);
    }

}
