package org.example.consumer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.example.consumer.client.DemoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author chenbin
 * @date 2021/7/24
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @Resource
    private LoadBalancerClient loadBalancerClient;

    @Resource
    private RestTemplate restTemplate;

    @Value("${spring.application.name}")
    private String appName;

    @Value("${valueForNacos}")
    private String valueForNacos;

    @Resource
    private DemoClient demoClient;

    @RequestMapping("/test")
    @SentinelResource("test")
    @ResponseBody
    public Map<String, String> test() {
        String value = demoClient.getValue();
        System.out.println(valueForNacos);
        ServiceInstance serviceInstance = loadBalancerClient.choose("demo-service");
        String path = String.format("http://%s:%s/%s", serviceInstance.getHost(), serviceInstance.getPort(), "demo/test");
        Map<String, String> forObject = restTemplate.getForObject(path, Map.class);
        forObject.put("value", value);
        return forObject;
    }
}
