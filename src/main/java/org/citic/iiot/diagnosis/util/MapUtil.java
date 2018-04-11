package org.citic.iiot.diagnosis.util;

/**
 * map table utils
 * @author guyj3@citic.com
 * @create 2017-07-18 11:10
 **/
public class MapUtil {
    /**
     *  return map inital capactiy
     * @param loadSize
     * @return
     */
    public static int initMapCapacity(int loadSize){
        return (int) ((float) loadSize / 0.75F + 1.0F);
    }
}
