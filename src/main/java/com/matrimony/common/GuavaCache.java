package com.matrimony.common;

import com.google.common.cache.CacheBuilder;

public class GuavaCache<K,V> implements Cache<K,V> {
    private com.google.common.cache.Cache<K,V> guavaCache;
    private int expiryDurationMin;

    public GuavaCache(int expiryDurationMin){
        this.expiryDurationMin = expiryDurationMin;
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
        guavaCache = cacheBuilder
                //.expireAfterWrite(expiryDurationMin, TimeUnit.MINUTES)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    @Override
    public void put(K key, V value) {
        if(key != null && value != null){
            guavaCache.put(key, value);
        }
    }

    @Override
    public V get(K key) {
        return guavaCache.getIfPresent(key);
    }

    @Override
    public void evict(K key) {
        guavaCache.invalidate(key);
    }

    @Override
    public void evictAll() {
        guavaCache.invalidateAll();
    }

    public int getExpiryDurationMin() {
        return expiryDurationMin;
    }

    public void setExpiryDurationMin(int expiryDurationMin) {
        this.expiryDurationMin = expiryDurationMin;
    }
}
