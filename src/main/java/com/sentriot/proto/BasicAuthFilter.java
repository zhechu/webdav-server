package com.sentriot.proto;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BasicAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if ("token".equals(name)) {
                    String value = cookie.getValue();
                    if (value != null && value.startsWith("Basic")) {
                        String base64Credentials = value.substring("Basic".length()).trim();
                        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
                        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
                        final String[] values = credentials.split(":", 2);
                        String userName = values[0];
                        String password = values[1];

                        // 用户名和密码验证
                        if (userName.equals("lyw") && password.equals("123456")) {
                            httpResponse.addCookie(new Cookie("token", value));
                            chain.doFilter(request, response);
                            return;
                        }
                    }
                }
            }
        }

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic")) {
            String base64Credentials = authHeader.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            String userName = values[0];
            String password = values[1];

            // 在这里添加您的用户名和密码验证逻辑
            if (userName.equals("lyw") && password.equals("123456")) {
                httpResponse.addCookie(new Cookie("token", authHeader));
                httpResponse.addHeader("Authorization", authHeader);
                chain.doFilter(request, response);
                return;
            }
        }

        httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"MyRealm\"");
        httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    @Override
    public void destroy() {}

}
