package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.utils.Constant;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Random;

/**
 * Author: liqiang
 * Date: 14-10-24
 * Time: 下午1:51
 * 探索系统
 */
public class ExploreMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger("exp");

    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {
        long begin = System.currentTimeMillis();

        String tag = Constant.EXPLORE_RULE.getTag();
        Advertise ad = AdvertiseManager.getADByCategoryAndUID(Constant.EXPLORE_RULE.getCategory(), inputFeature.getUid());

        ADMatchMessage message = null;
        if(ad != null){
            message = new ADMatchMessage(0, String.valueOf(ad.getOrigAdid()), ad.getCode(), tag);
        }else{
            LOGGER.info("explore failed");
            message = new ADMatchMessage(-1,"explore failed");
        }

        //不打印code，太长
        //String msg = "{\"reqid\":\""+ inputFeature.getReqid()+",\"status\":" +message.getStatus()+ ",\"adid\":\""+message.getAdid()+"\"," +
        //        "\"msg\":\"" + message.getMsg() +"\",\"took\":"+(System.currentTimeMillis() - begin)+",\"tag\":\""+message.getTag()+"\"}";

        //reqid + uid + pid + nation + status + adid + tag  + spendtime
        StringBuilder sb = new StringBuilder(inputFeature.getReqid())
                .append("\t").append(inputFeature.getUid())
                .append("\t").append(inputFeature.getPid())
                .append("\t").append(inputFeature.getNation())
                .append("\t").append(message.getStatus())
                .append("\t").append(message.getAdid())
                .append("\t").append(message.getTag())
                .append("\t").append((System.currentTimeMillis() - begin));

        LOGGER.debug(sb.toString());

        return message;
    }
}
