package com.bomb.jparrott.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Map that allows bidirectional lookup between key and values.  This is based off of Apache Commons class BidiMap,
 * however OneToManyBidiMap doesn't have the restriction that values must be unique.  Doing a revers lookup on
 * the map (i.e., searching for keys based on a value) will return a HashSet of the keys.
 *
 * @param <K>
 * @param <V>
 */
public class OneToManyBidiMap<K, V> extends HashMap<K, V> implements Serializable{

    private transient final static Logger log = LogManager.getLogger(OneToManyBidiMap.class);
    private Map<V, Set<K>> valueMap;

    public OneToManyBidiMap(){
        super();
        valueMap = new HashMap<>();
    }

    public OneToManyBidiMap(Map<? extends K, ? extends V> m) {
        super(m);
        valueMap = new HashMap<>(reverseMap(m));
    }

    public Set<K> getKey(V value){
        return valueMap.get(value);
    }

    public Map<V, Set<K>> reverseMap() {
        Map<V, Set<K>> reversedMap = new OneToManyBidiMap<>();

        Iterator iterator = this.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<K, V> entry = (Map.Entry<K, V>)iterator.next();
            K key = entry.getKey();
            V value = entry.getValue();

            Set<K> keySet = reversedMap.get(value);

            keySet = keySet == null ? new HashSet<K>() : keySet;
            keySet.add(key);
            reversedMap.put(value, keySet);
        }

        return reversedMap;
    }

    //used only for the constructor with map parameter
    private Map<V, Set<K>> reverseMap(Map<? extends K, ? extends V> map) {
        Map<V, Set<K>> reversedMap = new OneToManyBidiMap<>();

        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<K, V> entry = (Map.Entry<K, V>)iterator.next();
            K key = entry.getKey();
            V value = entry.getValue();

            Set<K> keySet = reversedMap.get(value);

            keySet = keySet == null ? new HashSet<K>() : keySet;
            keySet.add(key);
            reversedMap.put(value, keySet);
        }

        return reversedMap;
    }

    @Override
    public V put(K key, V value){
        if(key == null){
            NullPointerException npe = new  NullPointerException("key cannot be null");
            log.error(npe);
            throw npe;
        }
        if(value == null){
            NullPointerException npe = new  NullPointerException("value cannot be null");
            log.error(npe);
            throw npe;
        }

        //go get the previous value
        V previousValue = super.put(key, value);

        //remove the previous value from valueMap
        Set<K> keySet = valueMap.get(previousValue);
        if(keySet != null){
            keySet.remove(key);
            if(keySet.isEmpty()){
                valueMap.remove(previousValue);
            }else{
                valueMap.put(previousValue, keySet);
            }
        }

        //add the new value in valueMap
        keySet = valueMap.get(value);
        keySet = keySet == null ? new HashSet<K>() : keySet;
        keySet.add(key);
        valueMap.put(value, keySet);

        return previousValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m){
        for (Map.Entry<? extends K,? extends V> entry : m.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            put(key, value);
        }
    }

    @Override
    public V remove(Object key){
        V value = super.remove(key);
        if(value != null){
            Set<K> keySet = valueMap.get(value);
            if(keySet != null){
                keySet.remove(key);
                if(keySet.isEmpty()){
                    valueMap.remove(value);
                }else{
                    valueMap.put(value, keySet);
                }
            }
        }
        return value;
    }

    @Override
    public void clear(){
        super.clear();
        valueMap.clear();
    }

}






