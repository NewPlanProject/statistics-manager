package org.citic.iiot.diagnosis.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * config of druid datasource
 *
 * @author guyj3@citic.com
 * @create 2017-06-27 13:30
 **/
@Slf4j
@Configuration
public class DruidConfiguration {
    @Bean
    public DataSource druidDataSource(@Value("${spring.datasource.driver-class-name}") String driver,
                                      @Value("${spring.datasource.url}") String url, @Value("${spring.datasource.username}") String username,
                                      @Value("${spring.datasource.password}") String password, @Value("${spring.datasource.maxActive}") int maxActive){
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(driver);
        druidDataSource.setUrl(url);
        druidDataSource.setUsername(username);
        druidDataSource.setPassword(password);
        druidDataSource.setMaxActive(maxActive);
        try{
            druidDataSource.setFilters("stat, wall");
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return druidDataSource;
    }
}
