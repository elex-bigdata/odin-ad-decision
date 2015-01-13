package com.elex.odin.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: liqiang
 * Date: 14-11-11
 * Time: 下午3:07
 */
public class ExploreRule {

    private String tag = "exp";
    private String name ="";
    private int rate = 0;
    private String where = "";
    private List<Integer> adIDs = new ArrayList<Integer>();
//    private List<Advertise> ads;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

/*    public List<Advertise> getAds() {
        return ads;
    }

    public void setAds(List<Advertise> ads) {
        this.ads = ads;
    }*/

    public List<Integer> getAdIDs() {
        return adIDs;
    }

    public void setAdIDs(List<Integer> adIDs) {
        this.adIDs = adIDs;
    }
}
