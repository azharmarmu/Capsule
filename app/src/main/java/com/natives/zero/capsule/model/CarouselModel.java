package com.natives.zero.capsule.model;

public class CarouselModel {
    private String url, link;

    public CarouselModel(String url, String link) {
        this.url = url;
        this.link = link;
    }

    public String getUrl() {
        return url;
    }

    public String getLink() {
        return link;
    }
}
