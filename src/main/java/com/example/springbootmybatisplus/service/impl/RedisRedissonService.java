package com.example.springbootmybatisplus.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Guoxinyu
 * @description: TODO
 * @date 2025-01-17 15:39
 * @email gxy06x@qq.com
 */
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RedisRedissonService {


    private final RedissonClient redissonClient;

    // ============================= String类型操作 ============================
        
    /**
     * 将值存储到Redis中
     *
     * @param key   键
     * @param value 值
     */
    public <T> void setString(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 将值存储到Redis中
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void setString(String key, T value, long timeout, TimeUnit timeUnit) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        bucket.set(value, timeout, timeUnit);
    }

    /**
     * 根据键获取Redis中的值
     *
     * @param key 键
     * @return 值
     */
    public <T> T getString(String key) {
        RBucket<T> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    // ============================= Hash类型操作 ============================
        
    /**
     * 将值存储到Redis中
     *
     * @param key   键
     * @param field hash键
     * @param value 值
     */
    public <T> boolean addToHash(String key, Object field, T value) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        return hash.fastPut(field, value);
    }

    /**
     * 将值存储到Redis中
     *
     * @param key      键
     * @param field    hash键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> boolean addToHash(String key, Object field, T value, long timeout, ChronoUnit timeUnit) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        boolean fastPut = hash.fastPut(field, value);
        boolean expire = hash.expire(Instant.now().plus(timeout, timeUnit));
        return fastPut && expire;
    }

    /**
     * 根据键和Hash键获取Redis中的值
     *
     * @param key   键
     * @param field hash键
     * @return 值
     */
    public <T> T getFromHash(String key, Object field) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        return hash.get(field);
    }

    /**
     * 根据键获取Redis中的值
     *
     * @param key 键
     * @return 值
     */
    public <T> Map<Object, T> getFromHash(String key) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        return hash.readAllMap();
    }

    /**
     * 根据键和Hash键更新Redis中的值
     *
     * @param key   键
     * @param field hash键
     * @param value 值
     * @return 更新成功返回true，否则返回false
     */
    public <T> boolean updateToHash(String key, Object field, T value) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        return hash.fastReplace(field, value);
    }

    /**
     * 根据Key，删除Hash类型的数据
     *
     * @param key      键
     * @param hashKeys hash键
     * @return 删除成功的数量
     */
    public <T> long removeFromHash(String key, T... hashKeys) {
        RMap<Object, T> hash = redissonClient.getMap(key);
        return hash.fastRemove(hashKeys);
    }

    // ============================= List类型操作 ============================
        
    /**
     * 向List数据类型中添加值
     *
     * @param key   键
     * @param value 值
     */
    public <T> boolean addToList(String key, T value) {
        RList<T> list = redissonClient.getList(key);
        return list.add(value);
    }

    /**
     * 向List数据类型中添加值
     *
     * @param key   键
     * @param value 值
     */
    public <T> boolean addToList(String key, List<T> value) {
        RList<T> list = redissonClient.getList(key);
        return list.addAll(value);
    }

    /**
     * 向List数据类型中添加值
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> boolean addToList(String key, T value, long timeout, ChronoUnit timeUnit) {
        RList<T> list = redissonClient.getList(key);
        list.add(value);
        return list.expire(Instant.now().plus(timeout, timeUnit));
    }

    /**
     * 从List数据类型中获取值
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置
     * @return 值
     */
    public <T> List<T> getFromList(String key, int start, int end) {
        RList<T> list = redissonClient.getList(key);
        return list.range(start, end);
    }

    /**
     * 获取List数据类型中的所有值
     *
     * @param key 键
     * @return 值
     */
    public <T> List<T> getFromList(String key) {
        RList<T> list = redissonClient.getList(key);
        return list.readAll();
    }

        
    /**
     * 移除集合左侧第一个元素
     *
     * @param key 键
     */
    public void removeListLeft(String key) {
        RList<Object> list = redissonClient.getList(key);
        list.fastRemove(0);
    }

    /**
     * 移除集合右侧第一个元素
     *
     * @param key 键
     */
    public void removeListRight(String key) {
        RList<Object> list = redissonClient.getList(key);
        list.fastRemove(list.size() - 1);
    }

    /**
     * 移除集合指定位置元素
     *
     * @param key   键
     * @param index 索引
     */
    public void removeFromList(String key, int index) {
        RList<Object> list = redissonClient.getList(key);
        list.fastRemove(index);
    }

    /**
     * 移除集合指定元素
     *
     * @param key   键
     * @param value 值
     */
    public <T> boolean removeFromList(String key, T value) {
        RList<T> list = redissonClient.getList(key);
        return list.removeIf(o -> o.equals(value));
    }

        // ============================= Set类型操作 ============================
        
    /**
     * 添加值到Set数据类型中
     *
     * @param key   键
     * @param value 值
     */
    public <T> boolean addToSet(String key, T value) {
        RSet<T> set = redissonClient.getSet(key);
        return set.add(value);
    }

    /**
     * 添加值到Set数据类型中
     *
     * @param key      键
     * @param value    值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否成功
     */
    public <T> boolean addToSet(String key, T value, long timeout, ChronoUnit timeUnit) {
        RSet<T> set = redissonClient.getSet(key);
        boolean add = set.add(value);
        boolean expire = set.expire(Instant.now().plus(timeout, timeUnit));
        return add && expire;
    }

    /**
     * 添加值到Set数据类型中
     *
     * @param key    键
     * @param values 值
     * @return 是否成功
     */
    public <T> boolean addToSet(String key, List<T> values) {
        RSet<T> set = redissonClient.getSet(key);
        return set.addAll(values);
    }

    /**
     * 添加值到Set数据类型中
     *
     * @param key      键
     * @param values   值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 是否成功
     */
    public <T> boolean addToSet(String key, List<T> values, long timeout, ChronoUnit timeUnit) {
        RSet<T> set = redissonClient.getSet(key);
        set.addAllCounted(values);
        return set.expire(Instant.now().plus(timeout, timeUnit));
    }

        
    /**
     * 获取Set的所有元素。
     *
     * @param key 键
     * @return 所有值
     */
    public <T> Set<T> getFromSet(String key) {
        RSet<T> set = redissonClient.getSet(key);
        return set.readAll();
    }

    /**
     * 从Set数据类型中删除值
     *
     * @param key    键
     * @param values 值
     */
    public <T> void removeFromSet(String key, List<T> values) {
        RSet<T> set = redissonClient.getSet(key);
        values.forEach(set::remove);
    }

    /**
     * 从Set数据类型中删除值
     *
     * @param key   键
     * @param value 值
     */
    public <T> boolean removeFromSet(String key, T value) {
        RSet<T> set = redissonClient.getSet(key);
        return set.remove(value);
    }

        // ============================= ZSet类型操作 ============================
        
    /**
     * 添加值到ZSet数据类型中
     *
     * @param key   键
     * @param value 值
     * @param score 分值
     */
    public <T> void addToZSet(String key, T value, double score) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.add(score, value);
    }

    /**
     * 在ZSet数据类型中添加值
     *
     * @param key      键
     * @param value    值
     * @param score    分值
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     */
    public <T> void addToZSet(String key, T value, double score, long timeout, ChronoUnit timeUnit) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.add(score, value);
        sortedSet.expire(Instant.now().plus(timeout, timeUnit));
    }

    /**
     * 获取ZSet的范围元素。
     *
     * @param key   键
     * @param start 起始位置
     * @param end   结束位置
     * @return Set类型的值
     */
    public <T> Set<Object> getFromZSet(String key, int start, int end) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        return new HashSet<>(sortedSet.valueRange(start, end));
    }

    /**
     * 删除ZSet数据类型中的值
     *
     * @param key    键
     * @param values 值
     */
    public <T> void removeFromZSet(String key, List<T> values) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.removeAll(values);
    }

    /**
     * 删除ZSet数据类型中的值
     *
     * @param key   键
     * @param value 值
     */
    public <T> void removeFromZSet(String key, T value) {
        RScoredSortedSet<T> sortedSet = redissonClient.getScoredSortedSet(key);
        sortedSet.remove(value);
    }

        // ============================= Common ============================
        
    /**
     * 判断Key是否存在
     *
     * @param key 键
     * @return 存在返回true，否则返回false
     */
    public boolean exists(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * 删除Key
     *
     * @param key 键
     */
    public boolean remove(String key) {
        long delete = redissonClient.getKeys().delete(key);
        return delete > 0;
    }

    /**
     * 设置Key的过期时间
     *
     * @param key      键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return 设置成功返回true，否则返回false
     */
    public boolean expire(String key, long timeout, ChronoUnit timeUnit) {
        return redissonClient.getBucket(key).expire(Instant.now().plus(timeout, timeUnit));
    }

    /**
     * 获取Key的过期时间
     *
     * @param key 键
     * @return 过期时间
     */
    public Long getExpire(String key) {
        return redissonClient.getBucket(key).getExpireTime();
    }

    /**
     * 递增操作
     *
     * @param key   键
     * @param delta 增加的值
     * @return 递增后的值，如果键不存在，则返回-1
     */
    public long increment(String key, long delta) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.addAndGet(delta);
    }

    /**
     * 递减操作
     *
     * @param key   键
     * @param delta 减少的值
     * @return 递减后的值，如果键不存在，则返回-1
     */
    public long decrement(String key, long delta) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        return atomicLong.decrementAndGet();
    }

    /**
     * 递增操作
     *
     * @param key   键
     * @param delta 增加的值
     * @return 递增后的值，如果键不存在，则返回-1
     */
    public double increment(String key, double delta) {
        RAtomicDouble atomicDouble = redissonClient.getAtomicDouble(key);
        return atomicDouble.addAndGet(delta);
    }

    /**
     * 递减操作
     *
     * @param key   键
     * @param delta 减少的值
     * @return 递减后的值，如果键不存在，则返回-1
     */
    public double decrement(String key, double delta) {
        RAtomicDouble atomicDouble = redissonClient.getAtomicDouble(key);
        return atomicDouble.decrementAndGet();
    }

    /***
     * @description: 获取锁：返回一个互斥锁（RLock），适用于需要排他访问的场景
     * @author Guoxinyu
     */
    public RLock getLock(String lockName) {
        return redissonClient.getLock(lockName);
    } /**
     * @description: 获取读写锁：返回一个包含读锁和写锁的复合锁（RReadWriteLock），用于读多写少的场景，允许多读单写。
     * @param lockName 锁的名称
     * @return RReadWriteLock
     * @author Guoxinyu
     */
    public RReadWriteLock getReadWriteLock(String lockName) {
        return redissonClient.getReadWriteLock(lockName);
    }

    /**
     * @description: 从复合锁(读写锁)中获取写锁部分，用于需要独占访问的写操作。
     * @param lockName 锁的名称
     * @return RLock
     * @author Guoxinyu
     */
    public RLock getWriteLock(String lockName) {
        return redissonClient.getReadWriteLock(lockName).writeLock();
    }

    /**
     * @description: 从复合锁(读写锁)中获取写锁部分，用于需要独占访问的写操作。
     * @param lockName 锁的名称
     * @return RLock
     * @author Guoxinyu
     */
    public RLock getReadLock(String lockName) {
        return redissonClient.getReadWriteLock(lockName).readLock();
    }

    /**
     * @description: 使用写锁执行操作场景：假设你有一个应用程序，其中包含一个全局可访问的配置信息集，这些信息可能包括应用的设置、用户偏好等。配置信息需要在不同时间点由不同的管理员进行更新，以响应外部变化或内部需求。
     * @param lockName 锁的名称
     * @param action 要执行的操作
     * @author Guoxinyu
     */
    public void performWriteAction(String lockName, Runnable action) {
        RLock writeLock = getWriteLock(lockName);
        try {
            // 尝试获取写锁，最多等待10秒，锁持有时间为30秒
            boolean isLocked = writeLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    action.run();
                } finally {
                    // 释放写锁
                    if (writeLock.isHeldByCurrentThread()) {
                        writeLock.unlock();
                    }
                }
            } else {
                log.error("获取写锁失败: {}", lockName);
            }
        } catch (InterruptedException e) {
            log.error("线程中断: {}", e.getMessage());
        }
    }

    /**
     * @description: 使用读锁执行操作场景： 考虑一个应用程序，它维护了关于用户行为的统计数据，这些数据被多个服务频繁读取用于生成报告或实时显示仪表板。这些统计数据不经常更新，但当更新发生时，必须确保数据的一致性。
     * @param lockName 锁的名称
     * @param action 要执行的操作
     * @author Guoxinyu
     */
    public void performReadAction(String lockName, Runnable action) {
        RLock readLock = getReadLock(lockName);
        try {
            // 尝试获取读锁，最多等待10秒，锁持有时间为30秒
            boolean isLocked = readLock.tryLock(10, 30, TimeUnit.SECONDS);
            if (isLocked) {
                try {
                    action.run();
                } finally {
                    // 释放读锁
                    if (readLock.isHeldByCurrentThread()) {
                        readLock.unlock();
                    }
                }
            } else {
                log.error("获取读锁失败: {}", lockName);
            }
        } catch (InterruptedException e) {
            log.error("线程中断: {}", e.getMessage());
        }
    }

}
