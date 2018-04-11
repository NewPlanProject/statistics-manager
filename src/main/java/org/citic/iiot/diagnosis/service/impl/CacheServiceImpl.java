package org.citic.iiot.diagnosis.service.impl;

import org.citic.iiot.diagnosis.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaohl on 2017/5/26.
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);

    @Autowired
    private StringRedisTemplate template;

    public String get(String key) {
        logger.info("Enter get() key={}", key);
        ValueOperations<String, String> ops = this.template.opsForValue();
        return ops.get(key);
    }

    public void set(String key, String value, long expire) {
        logger.debug("Enter set() key={}, value={}", key, value);
        ValueOperations<String, String> ops = this.template.opsForValue();
        ops.set(key, value, expire, TimeUnit.SECONDS);
    }

    public void set(String key, String value) {
        logger.info("Enter set() key={}, value={}", key, value);
        ValueOperations<String, String> ops = this.template.opsForValue();
        ops.set(key, value);
    }

    public List<String> mget(Set<String> keys) {
        logger.info("Enter mget() keys={}", keys);
        return template.opsForValue().multiGet(keys);
    }

    public void mset(Map<String, String> map) {
        logger.info("Enter mset() keys={}, value={}", map.keySet(), map.values());
        template.opsForValue().multiSet(map);
    }

    public void remove(String key) {
        logger.info("Enter remove() key={}", key);
        this.template.delete(key);
    }

    public String getHash(String key, String hashKey) {
        logger.info("Enter getHash() key={},hashKey={}", key, hashKey);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        return hashOps.get(key, hashKey);
    }

    public void setHash(String key, String hashKey, String value) {
        logger.info("Enter setHash() key={},hashKey={}, value={}", key, hashKey, value);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        hashOps.put(key, hashKey, value);
    }

    public void removeHash(String key, String hashKey) {
        logger.info("Enter removeHash() key={} hashKey={}", key, hashKey);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        hashOps.delete(key, hashKey);
    }

    public List<String> getHashValues(String key) {
        logger.info("Enter getHashValues() key={}", key);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        return hashOps.values(key);
    }

    public Map getHashEntries(String key) {
        logger.info("Enter getHashEntries() key={}", key);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        return hashOps.entries(key);
    }

    public Boolean incrementHashLong(String hashname, String itemkey, Long delta) {
        logger.info("Enter hashIncrementLong() hashname={},itemkey={},delta={}", hashname, itemkey, delta);
        HashOperations<String, String, String> hashOps = this.template.opsForHash();

        String countStr = hashOps.get(hashname, itemkey);
        if (countStr == null) {
            hashOps.put(hashname, itemkey, delta + "");
        } else {
            hashOps.increment(hashname, itemkey, delta);//增加统计量
        }
        return true;
    }

    public List<String> mgetHash(String key, Set<String> keys) {
        logger.info("Enter getHash() key={},hashKey.size={}", key, keys.size());
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        return hashOps.multiGet(key, keys);
    }

    public void msetHash(String key, Map<String, String> map) {
        logger.info("Enter getHash() key={},hash.size={}", key, map.size());
        HashOperations<String, String, String> hashOps = this.template.opsForHash();
        hashOps.putAll(key, map);
    }
}
