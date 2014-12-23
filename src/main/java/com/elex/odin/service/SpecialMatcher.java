package com.elex.odin.service;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.Advertise;
import com.elex.odin.entity.InputFeature;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * 一些临时的特殊的试验策略,都写死
 * Author: liqiang
 * Date: 14-11-17
 * Time: 下午4:50
 */
public class SpecialMatcher implements ADMatcher {

    private static final Logger LOGGER = Logger.getLogger("exp");
    private static Random random = new Random();
    private static int mainid = 50996;
    private static List<Integer> expID = new ArrayList<Integer>();
    static{
//        expID.add(50997); //APN ALL1
        expID.add(50998); //MAT ALL1
        //expID.add(50993); //Ybrant All
        expID.add(50994); //Criteo All
        expID.add(51002); //Marimedia
    }

    @Override
    public ADMatchMessage match(InputFeature inputFeature) throws Exception {
        long begin = System.currentTimeMillis();
        String tag = "exp_main";
        int adid = mainid;
        if("NA".equals(inputFeature.getUid())){
            tag = "na";
            adid = 51000; //Criteo none uid , UID为空的情况
        }else{
            int r = random.nextInt(100);
            if(r < 12){
                adid = expID.get(random.nextInt(expID.size()));
                tag = "exp_cyma";
            }
        }

        Advertise ad = AdvertiseManager.getADByID(adid);

        if(ad != null){
            ADMatchMessage message = new ADMatchMessage(0, String.valueOf(adid), ad.getCode(), tag);
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

        return null;
    }
}
