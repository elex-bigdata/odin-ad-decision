package com.elex.odin.entity;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:12
 */
public class Advertise {

    //ODIN对应的ID
    private int adid;
    //网盟的ID
    private int origAdid;
    private String category = "all_all";
    private String firstCategory;
    private String secondCategory;
    private String position;
    private String code;
    private String mediaType;
    private String name;
    private String size;
    private String time;
    private String network;

    public int getAdid() {
        return adid;
    }

    public void setAdid(int adid) {
        this.adid = adid;
    }

    public int getOrigAdid() {
        return origAdid;
    }

    public void setOrigAdid(int origAdid) {
        this.origAdid = origAdid;
    }

    public String getFirstCategory() {
        return firstCategory;
    }

    public void setFirstCategory(String firstCategory) {
        this.firstCategory = firstCategory;
    }

    public String getSecondCategory() {
        return secondCategory;
    }

    public void setSecondCategory(String secondCategory) {
        this.secondCategory = secondCategory;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String firstCategory, String secondCategory) {
        this.firstCategory =  firstCategory;
        this.secondCategory = secondCategory;
        this.category = firstCategory + "_" + secondCategory;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
}
