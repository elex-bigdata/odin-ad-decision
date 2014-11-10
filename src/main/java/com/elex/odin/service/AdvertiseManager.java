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
    public static Map<Integer, Advertise> oldAdverties = new HashMap<Integer, Advertise>();
    public static Map<String, Set<Integer>> categorys = new HashMap<String, Set<Integer>>();
    public static List<Integer> adIDs = new ArrayList<Integer>();

    public static void loadOldAdvertise() throws Exception {
        OdinADDao dao = new OdinADDao();
        List<Advertise> ads = dao.getAdBySlot(10004);
        for(Advertise ad : ads){
            oldAdverties.put(ad.getAdid(), ad);
        }
        LOGGER.info("load " + oldAdverties.size() + " old ads");
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
            List<Integer> ids = new ArrayList<Integer>();
            ids.addAll(advertise.keySet());

            categorys = catMap;
            advertise = adMap;
            adIDs = ids;

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
