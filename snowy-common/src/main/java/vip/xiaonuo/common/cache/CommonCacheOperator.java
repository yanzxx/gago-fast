
package vip.xiaonuo.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 通用Redis缓存操作器
 *
 * @author xuyuxiang
 * @date 2022/6/21 16:00
 **/
@Slf4j
@Component
public class CommonCacheOperator {

    /** 所有缓存Key的前缀 */
    private static final String CACHE_KEY_PREFIX = "Cache:";

    private static final long NEVER_EXPIRE = -1L;

    /** Redis异常时，本地缓存兜底，避免关键流程直接500 */
    private final Map<String, Object> localCache = new ConcurrentHashMap<>();

    private final Map<String, Long> localExpireTime = new ConcurrentHashMap<>();

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            redisTemplate.boundValueOps(fullKey).set(value);
        } catch (Exception e) {
            log.warn("Redis写入失败，降级本地缓存，key={}", fullKey, e);
        }
        putLocal(fullKey, value, NEVER_EXPIRE);
    }

    public void put(String key, Object value, long timeoutSeconds) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            redisTemplate.boundValueOps(fullKey).set(value, timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Redis写入(含过期)失败，降级本地缓存，key={}", fullKey, e);
        }
        putLocal(fullKey, value, timeoutSeconds);
    }

    public void putMap(String key, Map<String, Object> cacheMap) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            redisTemplate.boundHashOps(fullKey).putAll(cacheMap);
        } catch (Exception e) {
            log.warn("Redis写入Map失败，降级本地缓存，key={}", fullKey, e);
        }
        putLocal(fullKey, new HashMap<>(cacheMap), NEVER_EXPIRE);
    }

    public void putMapValue(String key, String hKey, Object hValue) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            redisTemplate.boundHashOps(fullKey).put(hKey, hValue);
        } catch (Exception e) {
            log.warn("Redis写入Map字段失败，降级本地缓存，key={}, hKey={}", fullKey, hKey, e);
        }
        Object existObj = getLocal(fullKey);
        Map<String, Object> localMap = existObj instanceof Map ? (Map<String, Object>) existObj : new HashMap<>();
        localMap.put(hKey, hValue);
        putLocal(fullKey, localMap, NEVER_EXPIRE);
    }

    public Map<Object, Object> getMap(String key) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            return redisTemplate.boundHashOps(fullKey).entries();
        } catch (Exception e) {
            log.warn("Redis读取Map失败，使用本地缓存，key={}", fullKey, e);
            Object localObj = getLocal(fullKey);
            if(localObj instanceof Map) {
                return new HashMap<>((Map<?, ?>) localObj);
            }
            return new HashMap<>();
        }
    }

    public Object get(String key) {
        String fullKey = CACHE_KEY_PREFIX + key;
        try {
            Object redisValue = redisTemplate.boundValueOps(fullKey).get();
            if(ObjectUtil.isNotEmpty(redisValue)) {
                return redisValue;
            }
        } catch (Exception e) {
            log.warn("Redis读取失败，使用本地缓存，key={}", fullKey, e);
        }
        return getLocal(fullKey);
    }

    public void remove(String... key) {
        ArrayList<String> keys = CollectionUtil.toList(key);
        List<String> withPrefixKeys = keys.stream().map(i -> CACHE_KEY_PREFIX + i).collect(Collectors.toList());
        try {
            redisTemplate.delete(withPrefixKeys);
        } catch (Exception e) {
            log.warn("Redis删除失败，降级删除本地缓存，keys={}", withPrefixKeys, e);
        }
        withPrefixKeys.forEach(this::removeLocal);
    }

    public Collection<String> getAllKeys() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null) {
                // 去掉缓存key的common prefix前缀
                return keys.stream().map(key -> StrUtil.removePrefix(key, CACHE_KEY_PREFIX)).collect(Collectors.toSet());
            }
        } catch (Exception e) {
            log.warn("Redis查询全部Key失败，使用本地缓存", e);
        }
        cleanupExpiredLocalCache();
        return localCache.keySet().stream().map(key -> StrUtil.removePrefix(key, CACHE_KEY_PREFIX)).collect(Collectors.toSet());
    }

    public Collection<Object> getAllValues() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
            if (keys != null) {
                return redisTemplate.opsForValue().multiGet(keys);
            }
        } catch (Exception e) {
            log.warn("Redis查询全部Value失败，使用本地缓存", e);
        }
        cleanupExpiredLocalCache();
        return new ArrayList<>(localCache.values());
    }

    public Map<String, Object> getAllKeyValues() {
        Collection<String> allKeys = this.getAllKeys();
        HashMap<String, Object> results = MapUtil.newHashMap();
        for (String key : allKeys) {
            results.put(key, this.get(key));
        }
        return results;
    }

    private void putLocal(String key, Object value, long timeoutSeconds) {
        localCache.put(key, value);
        if(timeoutSeconds > 0) {
            localExpireTime.put(key, System.currentTimeMillis() + timeoutSeconds * 1000);
        } else {
            localExpireTime.put(key, NEVER_EXPIRE);
        }
    }

    private Object getLocal(String key) {
        Long expireAt = localExpireTime.get(key);
        if(expireAt == null) {
            return null;
        }
        if(expireAt != NEVER_EXPIRE && expireAt <= System.currentTimeMillis()) {
            removeLocal(key);
            return null;
        }
        return localCache.get(key);
    }

    private void removeLocal(String key) {
        localCache.remove(key);
        localExpireTime.remove(key);
    }

    private void cleanupExpiredLocalCache() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : localExpireTime.entrySet()) {
            Long expireAt = entry.getValue();
            if(expireAt != null && expireAt != NEVER_EXPIRE && expireAt <= now) {
                removeLocal(entry.getKey());
            }
        }
    }

}
