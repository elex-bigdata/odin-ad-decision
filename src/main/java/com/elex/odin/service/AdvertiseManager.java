package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:38
 */
public class AdvertiseManager {

    private static final Logger LOGGER = Logger.getLogger(AdvertiseManager.class);
    public static Map<Integer, Advertise> advertise = new HashMap<Integer, Advertise>();
    private static Map<Integer, Advertise> oldAdverties = new HashMap<Integer, Advertise>();
    private static Map<String, Integer> categorys = new HashMap<String, Integer>();
    public static List<Integer> adIDs = new ArrayList<Integer>();

    public static void loadOldAdvertise() throws Exception {
        OdinADDao dao = new OdinADDao();
        List<Advertise> ads = dao.getAdBySlot(10004);
        for(Advertise ad : ads){
            oldAdverties.put(ad.getAdid(), ad);
        }
        LOGGER.info("load " + oldAdverties.size() + " old ads");
    }

    public static void loadAdvertise(){
        OdinADDao dao = new OdinADDao();
        try {
            List<Advertise> ads = dao.getAdInfo();
            HashMap<Integer, Advertise> adMap = new HashMap<Integer, Advertise>();
            HashMap<String, Integer> catMap = new HashMap<String, Integer>();
            for(Advertise ad : ads){
                adMap.put(ad.getAdid(), ad);
                catMap.put(ad.getCategory().toLowerCase(), ad.getAdid());
            }
            List<Integer> ids = new ArrayList<Integer>();
            ids.addAll(advertise.keySet());
            synchronized(AdvertiseManager.class){
                categorys = catMap;
                advertise = adMap;
                adIDs = ids;
            }
            LOGGER.info("load " + advertise.size() + " ads, " + categorys.size() + " categorys ");
        } catch (Exception e) {
            throw new RuntimeException("Error when update advertise", e);
        }
    }

    public static Advertise getADByID(Integer adid){
        Advertise ad = AdvertiseManager.oldAdverties.get(adid);
        if(ad == null){
            ad = AdvertiseManager.advertise.get(adid);
        }
        return ad;
    }

    public static Advertise getADByCategory(String category){
        Integer adId = categorys.get(category.toLowerCase());
        System.out.println(category.toLowerCase() + ":" + adId);
        if(adId != null){
            return advertise.get(adId);
        }
        return null;
    }
}
