/*
 * Copyright (c) 2006 - 2010 LinogistiX GmbH
 * 
 *  www.linogistix.com
 *  
 *  Project myWMS-LOS
 */
package de.linogistix.mobile.common.filter;

/**
 *
 * @author artur
 */
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionExpiredFilter implements Filter {


    /*public void doFilter2(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {    
    HttpServletRequest req = (HttpServletRequest)request;
    HttpServletResponse res = (HttpServletResponse)response;      
    HttpSession session = req.getSession();
    RequestDispatcher rd = null;
    
    // New Session so forward to login.jsp
    System.out.println("url = "+req.getPathInfo()+"  "+req.getServletPath()+"  "+req.getRequestURL().toString());
    if (session.isNew()){
    System.out.println("Session is new");
    //     rd = request.getRequestDispatcher("/pages/processes/controller/gui/component/CenterPanel.jsp");
    //     rd.forward(request, response);
    chain.doFilter(request, response);
    }
    
    // Not a new session so continue to the requested resource
    else{
    chain.doFilter(request, response);
    }
    }*/
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        doFilter3(request, response, chain);
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        

//        System.out.println("URL = "+ req.getRequestURL().toString());
        
//            if (session.getAttribute("AUTHORIZED") == null && request.getParameter("j_username2") != null) {
            if (request.getParameter("j_username2") != null) {                        
                HttpSession session = req.getSession(false);
                //Make sure that ghost session will be closed. (for secure reason)
                if (session == null) {
                    session = req.getSession();
                    session.invalidate();
                    session = req.getSession();
                }
                if (session.getAttribute("AUTHORIZED") == null) {
                    String url = "j_security_check?j_username=" + java.net.URLEncoder.encode(request.getParameter("j_username2"), "UTF-8") + "&j_password=" + java.net.URLEncoder.encode(request.getParameter("j_password2"), "UTF-8");
                    session.setAttribute("AUTHORIZED", "TRUE");
                    session.setAttribute("AUTHORIZED_FIRST", "TRUE");
                    res.sendRedirect(res.encodeRedirectURL(url));
                }    
                return;
            }
            chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        /*        if (filterConfig.getInitParameter("page") != null) {
        page = filterConfig.getInitParameter("page");
        }*/
    }

    public void destroy() {
        Thread.currentThread().interrupt();
    }
}
