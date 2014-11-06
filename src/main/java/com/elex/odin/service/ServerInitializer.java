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

            LOGGER.info("[Server Init] - load final score rule");
            ConfigurationManager.updateScoreDistanceConfig();

            LOGGER.info("[Server Init] - load ad info from odin");
            AdvertiseManager.loadAdvertise();

            LOGGER.info("[Server Init] - init redis version");
            CacheUtil.updateVersion();
            if(CacheUtil.getVersion() == 0){
                LOGGER.info("[Server Init] - init store feature model to redis");
                new FeatureModelService().updateModel();
            }

            LOGGER.info("[Server Init] - start job ");
            startJob();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static void startJob() throws SchedulerException {
        System.setProperty("", "true");
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler scheduler = schedFact.getScheduler();

        JobDetail faJob = JobBuilder.newJob(SimpleJob.class).withIdentity("fa",
                "group1").usingJobData("type", Constant.JOB_TYPE.FEATURE_ATTRIBUTE_CONF).build();
        CronTrigger faTrigger = TriggerBuilder.newTrigger().withIdentity(
                "fa", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")).build();

        JobDetail cvJob = JobBuilder.newJob(SimpleJob.class).withIdentity("cv",
                "group1").usingJobData("type",Constant.JOB_TYPE.CACHE_VSERION).build();
        CronTrigger cvTrigger = TriggerBuilder.newTrigger().withIdentity(
                "cv", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")).build();

        JobDetail aiJob = JobBuilder.newJob(SimpleJob.class).withIdentity("ai",
                "group1").usingJobData("type",Constant.JOB_TYPE.AD_INFO).build();
        CronTrigger aiTrigger = TriggerBuilder.newTrigger().withIdentity(
                "ai", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")).build();

        JobDetail dyJob = JobBuilder.newJob(SimpleJob.class).withIdentity("dy",
                "group1").usingJobData("type",Constant.JOB_TYPE.DYNAMIC_CONF).build();
        CronTrigger rdTrigger = TriggerBuilder.newTrigger().withIdentity(
                "dy", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")).build();

        JobDetail dmJob = JobBuilder.newJob(SimpleJob.class).withIdentity("dm",
                "group2").usingJobData("type",Constant.JOB_TYPE.DATA_MODEL).build();
        CronTrigger dmTrigger = TriggerBuilder.newTrigger().withIdentity(
                "dm", "group2").withSchedule(CronScheduleBuilder.cronSchedule("0 30 21 * * ?")).build();

        scheduler.scheduleJob(faJob, faTrigger);
        scheduler.scheduleJob(cvJob, cvTrigger);
        scheduler.scheduleJob(aiJob, aiTrigger);
        scheduler.scheduleJob(dyJob, rdTrigger);
//        scheduler.scheduleJob(dmJob, dmTrigger);

        scheduler.start();
        scheduler.shutdown(true);
    }


}
