package com.elex.odin.servlet;

import com.elex.odin.model.ADMatchMessage;
import com.elex.odin.model.UserFeature;
import com.elex.odin.util.WebUtil;

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
public class ADMatchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String action = req.getParameter("action");

        if(!"dec".equals(action) || !"exp".equals(action)){
            ADMatchMessage msg = new ADMatchMessage(-1,"","Invalid action type",0);
            WebUtil.writeJson(msg, resp);
            return ;
        }


        String uid = req.getParameter("uid");
        String reqid = req.getParameter("reqid");
        String pid = req.getParameter("pid");
        String ip = req.getParameter("ip");
        String nation = req.getParameter("nation");
        String browser = req.getParameter("browser");
        String os = req.getParameter("os");

        UserFeature userFeature = new UserFeature();
        userFeature.setUid(uid);
        userFeature.setReqid(reqid);
        userFeature.setPid(pid);
        userFeature.setIp(ip);
        userFeature.setNation(nation);
        userFeature.setBrowser(browser);
        userFeature.setOs(os);

    }
}
