package com.elex.odin.entity;

import com.elex.ssp.TimeUtils;
import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午3:09
 */
public class InputFeature {

    private String uid;
    private String reqid;
    private String pid;
    private String ip;
    private String nation;
    private String browser;
    private String hour;
    private String ampm;
    private String workOrVacation;
    private Date time;
    private String requestType;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReqid() {
        return reqid;
    }

    public void setReqid(String reqid) {
        this.reqid = reqid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getIp() {
        return ip;
    }

    //only keep the 3 level , eg: 192.168.1
    public void setIp(String ip) {
        this.ip = ip.substring(0,ip.lastIndexOf("."));
    }

    public String getNation() {
        return nation;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

    public String getWorkOrVacation() {
        return workOrVacation;
    }

    public void setWorkOrVacation(String workOrVacation) {
        this.workOrVacation = workOrVacation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time, String nation) throws ParseException {
        this.time = time;
        this.nation = nation;
        String[] dayInfo = TimeUtils.getTimeDimension(time, nation);
        this.hour = dayInfo[0];
        this.ampm = dayInfo[1];
        this.workOrVacation = dayInfo[2];
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
