package com.swjtu.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 李天峒
 * @date 2019/6/15 23:00
 */
@RestController
@Slf4j
public class ConfigController {

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @PostMapping("/monitor")
    public void busRefresh(){
        ServiceInstance serviceInstance = loadBalancerClient.choose("CONFIG");
        String url = String.format("http://%s:%s/%s", serviceInstance.getHost(), serviceInstance.getPort(), "actuator/bus-refresh");
        log.info("【地址】url={}",url);
        try {
            URL targetUrl = new URL(url);
            java.net.HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("POST");
            //设置是否HttpURLConnection输出，因为为post请求，所以参数要放在请求体内，故这里设置为true（默认为false）
            connection.setDoInput(true);
            //设置是否从HttpURLConnection读入，默认情况下是true
            connection.setDoOutput(true);
            //设置不使用缓存，post请求不能使用缓存
            connection.setUseCaches(false);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            inputStream.close();
            log.info("【请求成功】");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
