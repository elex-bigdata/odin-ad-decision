package com.elex.odin.model;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午2:50
 */
public class ADMatchMessage {

    private int status;
    private String adid = "";
    private String msg = "";
    private long took;

    public ADMatchMessage(int status, String adid, String msg, long took) {
        this.status = status;
        this.adid = adid;
        this.msg = msg;
        this.took = took;
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
}
