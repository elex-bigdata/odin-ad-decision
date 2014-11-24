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

    private static final Logger LOGGER = Logger.getLogger("dec");
    private static Random random = new Random();

    private static List<Integer> topRpmAds = new ArrayList<Integer>();
    private static Integer all = 3860421;
    static{
        topRpmAds.add(3910227);
        topRpmAds.add(3910234);
        topRpmAds.add(3910249);
        topRpmAds.add(3910267);
    }

    @Override
    public ADMatchMessage match(InputFeature userFeature) throws Exception {
        Advertise ad;
        String tag ;

        if(random.nextInt(30) < 20){ //20% for top rpm CC
            int index = random.nextInt(topRpmAds.size());
            ad = AdvertiseManager.getADByID(topRpmAds.get(index));
            tag = "dec_tcr";
        }else{
            ad = AdvertiseManager.getADByID(all);
            tag = "dec_aa";
        }

        if(ad != null){
            ADMatchMessage message = new ADMatchMessage(0, String.valueOf(ad.getOrigAdid()), ad.getCode(), tag);
            return message;
        }


        return null;
    }
}
