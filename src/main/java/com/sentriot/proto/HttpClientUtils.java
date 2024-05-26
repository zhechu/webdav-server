package com.sentriot.proto;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Author: ZhouJie
 * CreateTime: 2023-11-24  11:08
 * Description: http请求工具类
 * Version: 1.0
 */
@Slf4j
public class HttpClientUtils {

    /**
     * 向指定 URL 发送GET方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     */
    public static String doGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        String urlNameString = url;
        try {
            if (param != null || param != "") {
                urlNameString = urlNameString + "?" + param;
            }
            URL realUrl = new URL(urlNameString);
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException | ConnectTimeoutException e) {
            e.printStackTrace();
            //SocketTimeoutException：是Java包下抛出的异常，这定义了Socket读数据的超时时间，即从server获取响应数据须要等待的时间；当读取或者接收Socket超时会抛出SocketTimeoutException
            System.out.println("sendGet SocketTimeoutException, url=" + url + ",param=" + param);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("sendGet IOException, url=" + url + ",param=" + param);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("sendGet Exception, url=" + url + ",param=" + param);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("sendGet Exception, url=" + url + ",param=" + param);
            }
        }
        return result.toString();
    }



    /**
     * post请求
     * @param path url地址
     * @return
     * @throws IOException
     */
    public static JSONObject doPost(String path) {
        return doPost(path, new JSONObject(), null);
    }

    /**
     * post请求
     * @param path url地址
     * @param Info jsonobject 参数
     * @return
     * @throws IOException
     */
    public static JSONObject doPost(String path,JSONObject Info,String authorization) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(path);
        if (StringUtils.isNotBlank(authorization)){
            post.addHeader("Authorization", authorization);
        }
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        //jnpf平台接口认证必要参数
        post.setHeader("YmDate", "1");
        String result = "";

        try {
            StringEntity s = new StringEntity(Info.toString(), "utf-8");
            s.setContentType("application/json");
            post.setEntity(s);

            // 发送请求
            HttpResponse httpResponse = client.execute(post);

            // 获取响应输入流
            InputStream inStream = httpResponse.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "utf-8"));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
                strber.append(line + "\n");
            inStream.close();

            result = strber.toString();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                log.info("请求服务器成功，做相应处理，result:{}", result);
            } else {
                log.error("请求服务端失败，result:{}", result);
            }
        } catch (Exception e) {
            log.error("远程调用异常", e);
            throw new RuntimeException(e);
        }

        return JSONObject.parseObject(result);
    }

}
