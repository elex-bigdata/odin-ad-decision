package com.elex.odin.job;

import com.elex.odin.service.*;
import com.elex.odin.utils.CacheUtil;
import com.elex.odin.utils.Constant;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午4:10
 */
public class SimpleJob implements Job{

    private static final Logger LOGGER = Logger.getLogger(SimpleJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String type = String.valueOf(jobExecutionContext.getMergedJobDataMap().get("type"));
        try{
            if(Constant.JOB_TYPE.FEATURE_ATTRIBUTE_CONF.equals(type)){
                ConfigurationManager.updateFeatureAttribute();
            }else if(Constant.JOB_TYPE.AD_INFO.equals(type)){
                AdvertiseManager.loadAdvertise();
            }else if(Constant.JOB_TYPE.CACHE_VSERION.equals(type)){
                CacheUtil.updateVersion();
            }else if(Constant.JOB_TYPE.DATA_MODEL.equals(type)){
                new MemoryFeatureModelService().updateModel();
            }else if(Constant.JOB_TYPE.DYNAMIC_CONF.equals(type)){
                ConfigurationManager.updateRequestDispatchConfig();
                ConfigurationManager.updateScoreDistanceConfig();
            }else if(Constant.JOB_TYPE.EXP_RULE.equals(type)){
                ConfigurationManager.updateExploreRule();
            }
        }catch (Exception e){
            e.printStackTrace();
            MailManager.getInstance().sendEmail("Job " + type + " error", "error", e);
        }

    }
}
