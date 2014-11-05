package com.elex.odin.entity;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:12
 */
public class Advertise {

    private int adid;
    private String category = "other";
    private String media;
    private String name;

    public int getAdid() {
        return adid;
    }

    public void setAdid(int adid) {
        this.adid = adid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        //TODO: split the name to category or media
        this.name = name;
    }
}
