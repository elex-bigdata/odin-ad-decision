package com.elex.odin.entity;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 上午10:53
 */
public class FeatureADInfo {

    private String adID;
    private int adImpression;
    private int adClick;
    private int pv;
    private int searchCount;
    private Double pvCtr;
    private Double impCtr;
    private Double adFillRate;

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public int getAdImpression() {
        return adImpression;
    }

    public void setAdImpression(int adImpression) {
        this.adImpression = adImpression;
    }

    public int getAdClick() {
        return adClick;
    }

    public void setAdClick(int adClick) {
        this.adClick = adClick;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    public Double getPvCtr() {
        return pvCtr;
    }

    public void setPvCtr(Double pvCtr) {
        this.pvCtr = pvCtr;
    }

    public Double getImpCtr() {
        return impCtr;
    }

    public void setImpCtr(Double impCtr) {
        this.impCtr = impCtr;
    }

    public Double getAdFillRate() {
        return adFillRate;
    }

    public void setAdFillRate(Double adFillRate) {
        this.adFillRate = adFillRate;
    }
}
