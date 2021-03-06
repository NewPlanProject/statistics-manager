package org.heran.edu.statistics;

import org.citic.iiot.app.core.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Hello world!
 *
 */

@SpringBootApplication(scanBasePackages = {"org.heran.edu.statistics",
        "org.heran.edu.app.core.mybaits",
        "org.heran.edu.app.core.session"})
@EnableTransactionManagement
@ServletComponentScan(basePackages={"org.heran.edu.statistics"})
@EnableAsync
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients(basePackages = {"org.heran.edu.statistics.feignclient"})
public class Application
{
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        SpringContextUtil.setApplicationContext(ctx);
        String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
        for (String profile : activeProfiles) {
            System.out.println("Spring boot 运行的环境为:" + profile);
        }
    }
}
