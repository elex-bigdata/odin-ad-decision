package com.elex.odin.entity;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 上午10:25
 */
public class UserFeatureInfo {

    protected String featureValue;
    protected int pv;
    protected int adImpression;
    protected int adClick;

    public String getFeatureValue() {
        return featureValue;
    }

    public void setFeatureValue(String featureValue) {
        this.featureValue = featureValue;
    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
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
}
