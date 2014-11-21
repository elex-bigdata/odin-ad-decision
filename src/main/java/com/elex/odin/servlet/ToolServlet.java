package com.elex.odin.servlet;

import com.elex.odin.service.AdvertiseManager;
import com.elex.odin.service.ConfigurationManager;
import com.elex.odin.service.MemoryFeatureModelService;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.WebUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: liqiang
 * Date: 14-10-23
 * Time: 下午2:43
 */
public class ToolServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ToolServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String msg = "";
        try {
            if(Constant.JOB_TYPE.FEATURE_ATTRIBUTE_CONF.equals(action)){
                ConfigurationManager.updateFeatureAttribute();
            }else if(Constant.JOB_TYPE.AD_INFO.equals(action)){
                ConfigurationManager.updateExploreRule();
                AdvertiseManager.loadAdvertise();
            }else if(Constant.JOB_TYPE.DATA_MODEL.equals(action)){
                String day =  req.getParameter("day");
                new MemoryFeatureModelService().updateModel(day);
            }else if(Constant.JOB_TYPE.DYNAMIC_CONF.equals(action)){
                ConfigurationManager.updateRequestDispatchConfig();
                ConfigurationManager.updateScoreDistanceConfig();
            }
            msg = "success";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "failed " + e.getMessage();
            LOGGER.error("Action " + action + "failed", e);
        }
        WebUtil.writeStr(msg, resp);
    }

}
