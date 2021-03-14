package com.leyou.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/*
@SpringBootApplication//扫描组件，配置，指定搜索注入bean
@EnableDiscoveryClient//可以连接三个注册中心，通用性更好
//@EnableHystrix//推荐使用熔断
@EnableCircuitBreaker//熔断器
可以用一个注解代替（包含多个）上面三个（eureka标配）
@SpringCloudApplication//内部已经是三合一
*/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LyCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyCartApplication.class,args);
    }
}
