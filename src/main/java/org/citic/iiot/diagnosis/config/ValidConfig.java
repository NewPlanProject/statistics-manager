package org.citic.iiot.diagnosis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * 加载验证框架的配置文件valid.xml
 * @author guyj3@citic.com
 * @create 2017-08-24 11:14
 **/
@Configuration
@ImportResource(locations={"classpath:valid.xml"})
//@ImportResource(locations={"classpath:validator.xml"})
public class ValidConfig {
}
