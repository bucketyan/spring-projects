package com.fuck.service.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * DESCRIPTION:
 *
 * @author zouyan
 * @create 2017/7/12 16:50 Created by fuck~ on 2017/7/12.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ServiceProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceProviderApplication.class, args);
	}
}
