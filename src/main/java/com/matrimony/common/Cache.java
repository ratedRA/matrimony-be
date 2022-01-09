package com.matrimony.common;

public interface Cache<K, V> {

    void put(K key, V value);

    V get(K key);

    void evict(K key);

    void evictAll();
}
