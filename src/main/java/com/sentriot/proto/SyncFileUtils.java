package com.sentriot.proto;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class SyncFileUtils {

    private SyncFileUtils() {}

    /**
     * 同步远程文件到本地
     * @param fileSyncEnabled
     * @param hpdServer
     * @param path
     */
    public static boolean syncRemoteFileToLocal(boolean fileSyncEnabled, String hpdServer, String path) {
        if (!fileSyncEnabled || !StringUtils.endsWith(path, ".docx")) {
            return true;
        }
        log.debug("[syncRemoteFileToLocal] " + path);

        // 重试次数
        int retryCount= 3;
        int i = 0;
        JSONObject response = null;
        while (i < retryCount) {
            try {
                response = HttpClientUtils.doPost(hpdServer + "/api/file/downLoadRemoteFileToLocal" + path);
            } catch (Exception e) {
                log.error("同步远程文件到本地失败", e);
            }
            // 同步成功
            if (response != null && ObjectUtils.equals(response.getInteger("code"), 200)) {
                break;
            }

            log.error("同步远程文件到本地失败:{}", path);
            i ++;
        }

        // 最后检查并抛出异常
        if (i >= retryCount && (response == null || !ObjectUtils.equals(response.getInteger("code"), 200))) {
            return false;
        }

        return true;
    }

    /**
     * 同步本地文件到远程
     * @param fileSyncEnabled
     * @param hpdServer
     * @param path
     */
    public static boolean syncLocalFileToRemote(boolean fileSyncEnabled, String hpdServer, String path) {
        if (!fileSyncEnabled || !StringUtils.endsWith(path, ".docx")) {
            return true;
        }
        log.debug("[syncLocalFileToRemote] " + path);

        // 重试次数
        int retryCount= 3;
        int i = 0;
        JSONObject response = null;
        while (i < retryCount) {
            try {
                response = HttpClientUtils.doPost(hpdServer + "/api/file/uploadLocalFileToRemote" + path);
            } catch (Exception e) {
                log.error("同步远程文件到本地失败", e);
            }
            // 同步成功
            if (response != null && ObjectUtils.equals(response.getInteger("code"), 200)) {
                break;
            }

            log.error("同步远程文件到本地失败:{}", path);
            i ++;
        }

        // 最后检查并抛出异常
        if (i >= retryCount && (response == null || !ObjectUtils.equals(response.getInteger("code"), 200))) {
            return false;
        }

        return true;
    }

    public static String getRelativePath(final HttpServletRequest request) {
        return getRelativePath(request, false);
    }

    public static String getRelativePath(final HttpServletRequest request, final boolean allowEmptyPath) {
        final String pathInfo;

        if (request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null) {
            // For includes, get the info from the attributes
            pathInfo = (String)request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);
        }
        else {
            pathInfo = request.getPathInfo();
        }

        final StringBuilder result = new StringBuilder();
        if (pathInfo != null) {
            result.append(pathInfo);
        }
        if (result.length() == 0) {
            result.append('/');
        }

        return result.toString();
    }

}
