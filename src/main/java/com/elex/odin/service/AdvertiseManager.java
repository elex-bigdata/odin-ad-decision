package com.elex.odin.service;

import com.elex.odin.data.OdinADDao;
import com.elex.odin.entity.Advertise;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
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
    private static Map<Integer, BigDecimal> adCpc = new HashMap<Integer, BigDecimal>();

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
            for(Advertise ad : ads){
                adMap.put(ad.getAdid(), ad);
            }

            List<Integer> ids = new ArrayList<Integer>();
            ids.addAll(advertise.keySet());
            synchronized(AdvertiseManager.class){
                advertise = adMap;
            }
            LOGGER.info("load " + advertise.size() + " ads ");

            adCpc = dao.getADCpc();

            LOGGER.info("load " + adCpc.size() + " ad cpc ");
        } catch (Exception e) {
            throw new RuntimeException("Error when update advertise", e);
        }
    }

    public static Advertise getADByID(Integer adid){
        return AdvertiseManager.advertise.get(adid);
    }

    public static BigDecimal getADCpc(int adid){
        return adCpc.get(adid) == null ? new BigDecimal(0) : adCpc.get(adid);
    }

}
