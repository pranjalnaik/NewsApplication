package com.pjnaik.newsaggregator;

import java.io.Serializable;

public class Articles implements Serializable {
    private String articlewriter;
    private String articletitle;
    private String articledata;
    private String URLarticle;
    private String articlepicture;
    private String articletimestamp;

    public Articles() { }

    public String getArticlewriter() {

        return articlewriter;
    }

    public void setArticlewriter(String articlewriter) {

        this.articlewriter = articlewriter;
    }

    public String getArticletitle() {

        return articletitle;
    }

    public void setArticletitle(String articletitle) {

        this.articletitle = articletitle;
    }

    public String getArticledata() {

        return articledata;
    }

    public void setArticledata(String articledata) {

        this.articledata = articledata;
    }

    public String getURLarticle() {

        return URLarticle;
    }

    public void setURLarticle(String URLarticle) {

        this.URLarticle = URLarticle;
    }

    public String getPicture() {

        return articlepicture;
    }

    public void setPicture(String image) {

        this.articlepicture = image;
    }

    public String getArticletimestamp() {

        return articletimestamp;
    }

    public void setArticletimestamp(String articletimestamp) {

        this.articletimestamp = articletimestamp;
    }
}
