package com.elex.odin.entity;

import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-10-29
 * Time: 上午10:57
 */
public class FeatureAD {

    private String featureType;
    private String nation;
    //Map<featureValue,Map<adid,wight>>
    private Map<String,Map<String,FeatureADInfo>> featureADs;

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Map<String, Map<String, FeatureADInfo>> getFeatureADs() {
        return featureADs;
    }

    public void setFeatureADs(Map<String, Map<String, FeatureADInfo>> featureADs) {
        this.featureADs = featureADs;
    }
}
