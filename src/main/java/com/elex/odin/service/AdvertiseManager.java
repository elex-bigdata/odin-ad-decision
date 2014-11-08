package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午2:38
 */
public class AdvertiseManager {

    private static final Logger LOGGER = Logger.getLogger(AdvertiseManager.class);
    public static ConcurrentHashMap<Integer, Advertise> advertise = new ConcurrentHashMap<Integer, Advertise>();
    public static ConcurrentHashMap<Integer, Advertise> oldAdverties = new ConcurrentHashMap<Integer, Advertise>();
    public static ConcurrentHashMap<String, Set<Integer>> categorys = new ConcurrentHashMap<String, Set<Integer>>();
    public static List<Integer> adIDs = new ArrayList<Integer>();

    public static void loadOldAdvertise() throws Exception {
        OdinADDao dao = new OdinADDao();
        List<Advertise> ads = dao.getAdBySlot(10004);
        for(Advertise ad : ads){
            oldAdverties.put(ad.getAdid(), ad);
        }
        LOGGER.info("load " + oldAdverties.size() + " ad ads");
    }

    public synchronized static void loadAdvertise(){
        OdinADDao dao = new OdinADDao();
        try {
            List<Advertise> ads = dao.getAdInfo();
            HashMap<Integer, Advertise> adMap = new HashMap<Integer, Advertise>();
            HashMap<String, Set<Integer>> catMap = new HashMap<String, Set<Integer>>();
            for(Advertise ad : ads){
                adMap.put(ad.getAdid(), ad);
                Set<Integer> catAds = catMap.get(ad.getFirstCategory());
                if(catAds == null){
                    catAds = new HashSet<Integer>();
                    catMap.put(ad.getCategory(), catAds);
                }
                catAds.add(ad.getAdid());
            }

            synchronized (categorys){
                categorys.clear();
                categorys.putAll(catMap);
            }
            synchronized (advertise){
                advertise.clear();
                advertise.putAll(adMap);
            }
            synchronized (adIDs){
                adIDs = new ArrayList<Integer>();
                adIDs.addAll(advertise.keySet());
            }
            LOGGER.info("load " + advertise.size() + " ads, " + categorys.size() + " categorys ");
        } catch (Exception e) {
            throw new RuntimeException("Error when update advertise", e);
        }
    }

    public static Advertise getADByID(Integer adid){
        return advertise.get(adid);
    }

    public static Set<Integer> getADIDByCategory(String category){
        return categorys.get(category);
    }
}
