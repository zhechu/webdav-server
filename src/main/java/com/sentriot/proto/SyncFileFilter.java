package com.sentriot.proto;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class SyncFileFilter implements Filter {

    /**
     * 是否需同步文件
     */
    boolean fileSyncEnabled = false;

    /**
     * 慧评达服务地址
     */
    String hpdServer;

    public boolean isFileSyncEnabled() {
        return fileSyncEnabled;
    }

    public void setFileSyncEnabled(boolean fileSyncEnabled) {
        this.fileSyncEnabled = fileSyncEnabled;
    }

    public String getHpdServer() {
        return hpdServer;
    }

    public void setHpdServer(String hpdServer) {
        this.hpdServer = hpdServer;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String method = httpRequest.getMethod();
        String path = SyncFileUtils.getRelativePath(httpRequest);
        log.info("[SyncFileFilter] " + method + ":" + path);
        if (StringUtils.equalsIgnoreCase(method, "HEAD")) {
            SyncFileUtils.syncRemoteFileToLocal(this.isFileSyncEnabled(), this.getHpdServer(), path);
        }

        chain.doFilter(request, response);

        if (StringUtils.equalsIgnoreCase(method, "PUT")) {
            SyncFileUtils.syncLocalFileToRemote(this.isFileSyncEnabled(), this.getHpdServer(), path);
        }
    }

    @Override
    public void destroy() {}

}
