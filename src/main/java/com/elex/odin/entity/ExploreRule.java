package com.elex.odin.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: liqiang
 * Date: 14-11-11
 * Time: 下午3:07
 */
public class ExploreRule {

    private String tag = "exp";
    private Map<String, Integer> rules = new LinkedHashMap<String, Integer>();

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, Integer> getRules() {
        return rules;
    }

    public void setRules(Map<String, Integer> rules) {
        this.rules = rules;
    }
}
