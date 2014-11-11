package com.elex.odin.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-11-11
 * Time: 下午4:52
 */
public class DecisionRule {

    private String tag = "exp";
    private Map<String,FeatureAttribute> featureAttributes = new HashMap<String, FeatureAttribute>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, FeatureAttribute> getFeatureAttributes() {
        return featureAttributes;
    }

    public void setFeatureAttributes(Map<String, FeatureAttribute> featureAttributes) {
        this.featureAttributes = featureAttributes;
    }
}
