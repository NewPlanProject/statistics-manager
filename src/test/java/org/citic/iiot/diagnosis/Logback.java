package org.citic.iiot.diagnosis;

import lombok.extern.slf4j.Slf4j;

/**
 * logback使用示例
 *
 * @author guyj3@citic.com
 * @create 2017-06-14 10:17
 **/
@Slf4j
public class Logback {
    public static void main(String[] args) {
        //TRACE DEBUG INFO WARN ERROR

        log.trace("trace......");
        log.info("info......");
        log.debug("debug......");
        log.warn("warn......");
        log.error("error......");

    }
}
