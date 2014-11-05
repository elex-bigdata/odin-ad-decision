package com.elex.odin.entity;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午2:50
 */
public class ADMatchMessage {

    private int status = 0 ;
    private String adid = "";
    private String msg = "";
    private long took;
    private String tag = "default";

    public ADMatchMessage(int status, String adid, String tag) {
        this.status = status;
        this.adid = adid;
        this.tag = tag;
    }

    public ADMatchMessage(int status,  String msg) {
        this.status = status;
        this.msg = msg;
    }

    public ADMatchMessage(String tag) {
        this.tag = tag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
