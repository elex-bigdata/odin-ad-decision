package com.elex.odin.servlet;

import com.elex.odin.service.ServerInitializer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Author: liqiang
 * Date: 14-11-2
 * Time: 下午3:20
 */
public class InitServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        ServerInitializer.init();
    }
}
