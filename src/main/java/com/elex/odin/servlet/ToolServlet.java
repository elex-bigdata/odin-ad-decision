package com.elex.odin.servlet;

import com.elex.odin.entity.ADMatchMessage;
import com.elex.odin.entity.InputFeature;
import com.elex.odin.service.ExploreMatcher;
import com.elex.odin.service.MemFeatureModelService;
import com.elex.odin.service.StrategyMatcher;
import com.elex.odin.utils.Constant;
import com.elex.odin.utils.WebUtil;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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
        String action = req.getParameter("updatememmodel");
        boolean success = false;
        try {
            new MemFeatureModelService().updateModel();
            success = true;
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Action " + action + "failed", e);
        }
        WebUtil.writeStr(String.valueOf(success), resp);
    }

}
