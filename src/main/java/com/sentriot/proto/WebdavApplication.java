package com.sentriot.proto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import java.io.File;

@SpringBootApplication
public class WebdavApplication {

    @Value("${document.root}")
    private String documentRoot;

    @Value("${file.sync.enabled:false}")
    private boolean fileSyncEnabled;

    @Value("${hpd.server}")
    private String hpdServer;

    public static void main(final String[] args)
    {
        SpringApplication.run(WebdavApplication.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer()
    {
        final TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
        factory.setDocumentRoot(new File(documentRoot));
        return factory;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean()
    {
        WebdavServlet webdavServlet = new WebdavServlet();
        webdavServlet.setFileSyncEnabled(fileSyncEnabled);
        webdavServlet.setHpdServer(hpdServer);
        return new ServletRegistrationBean(webdavServlet, "/webdav/*");
    }

    @Bean
    public FilterRegistrationBean syncFileFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        SyncFileFilter syncFileFilter = new SyncFileFilter();
        syncFileFilter.setFileSyncEnabled(fileSyncEnabled);
        syncFileFilter.setHpdServer(hpdServer);
        registration.setFilter(syncFileFilter);
        registration.addUrlPatterns("/webdav/*");
        registration.setOrder(1);
        return registration;
    }

//    @Bean
//    public FilterRegistrationBean basicAuthFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new BasicAuthFilter());
//        registration.addUrlPatterns("/webdav/*");
//        registration.setOrder(1);
//        return registration;
//    }

}
