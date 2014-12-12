package com.elex.odin.service;

import com.elex.odin.job.SimpleJob;
import com.elex.odin.utils.CacheUtil;
import com.elex.odin.utils.Constant;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Author: liqiang
 * Date: 14-10-31
 * Time: 下午5:00
 */
public class ServerInitializer {

    private static final Logger LOGGER = Logger.getLogger(ServerInitializer.class);

    public static void init()  {
        try{
            LOGGER.info("[Server Init] - load feature match attributes");
            ConfigurationManager.updateFeatureAttribute();

            LOGGER.info("[Server Init] - load request dispatch rule");
            ConfigurationManager.updateRequestDispatchConfig();

            LOGGER.info("[Server Init] - load explore rule");
            ConfigurationManager.updateExploreRule();

            LOGGER.info("[Server Init] - load final score rule");
            ConfigurationManager.updateScoreDistanceConfig();

            LOGGER.info("[Server Init] - load ad info from odin");
            AdvertiseManager.loadOldAdvertise();
            AdvertiseManager.loadAdvertise();

/*            LOGGER.info("[Server Init] - init redis version");
            CacheUtil.updateVersion();
            if(CacheUtil.getVersion() == 0){
                LOGGER.info("[Server Init] - init store feature model to redis");
                new RedisFeatureModelService().updateModel();
            }*/

            LOGGER.info("[Server Init] - init cache");
//            new MemoryFeatureModelService().updateModel();

            LOGGER.info("[Server Init] - start job ");
            startJob();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void startJob() throws SchedulerException {
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();

        JobDetail faJob = JobBuilder.newJob(SimpleJob.class).withIdentity("fa",
                "group1").usingJobData("type", Constant.JOB_TYPE.FEATURE_ATTRIBUTE_CONF).build();
        CronTrigger faTrigger = TriggerBuilder.newTrigger().withIdentity(
                "fa", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?")).build();

        JobDetail cvJob = JobBuilder.newJob(SimpleJob.class).withIdentity("cv",
                "group1").usingJobData("type",Constant.JOB_TYPE.CACHE_VSERION).build();
        CronTrigger cvTrigger = TriggerBuilder.newTrigger().withIdentity(
                "cv", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?")).build();

        JobDetail aiJob = JobBuilder.newJob(SimpleJob.class).withIdentity("ai",
                "group1").usingJobData("type",Constant.JOB_TYPE.AD_INFO).build();
        CronTrigger aiTrigger = TriggerBuilder.newTrigger().withIdentity(
                "ai", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?")).build();

        JobDetail dcJob = JobBuilder.newJob(SimpleJob.class).withIdentity("dc",
                "group1").usingJobData("type",Constant.JOB_TYPE.DYNAMIC_CONF).build();
        CronTrigger dcTrigger = TriggerBuilder.newTrigger().withIdentity(
                "dc", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?")).build();

        JobDetail erJob = JobBuilder.newJob(SimpleJob.class).withIdentity("er",
                "group1").usingJobData("type",Constant.JOB_TYPE.EXP_RULE).build();
        CronTrigger erTrigger = TriggerBuilder.newTrigger().withIdentity(
                "er", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0,30 * * * ?")).build();

        scheduler.scheduleJob(faJob, faTrigger);
//        scheduler.scheduleJob(cvJob, cvTrigger);
//        scheduler.scheduleJob(aiJob, aiTrigger);
        scheduler.scheduleJob(dcJob, dcTrigger);
        scheduler.scheduleJob(erJob, erTrigger);

//        scheduler.start();
    }


}
