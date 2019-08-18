/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.crashinvaders.vfx.utils;

import com.badlogic.gdx.utils.Array;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Almost like regular ArrayMap, but supports value sorting and access to values by indices.
 */
public class ValueArrayMap<K, V> {
    private final Map<K, V> map;
    private final Array<V> values;

    private final Array<K> tmpKeyArray;

    public ValueArrayMap() {
        this(16);
    }

    public ValueArrayMap(int capacity) {
        map = new HashMap<>(capacity);
        values = new Array<>(true, capacity);

        tmpKeyArray = new Array<>(capacity);
    }

    public void put(K key, V value) {
        map.put(key, value);
        values.add(value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V getValueAt(int valueIndex) {
        return values.get(valueIndex);
    }

    public V remove(K key) {
        V value = map.remove(key);
        if (value != null) {
            values.removeValue(value, true);
        }
        return value;
    }

    public V removeByValue(V value) {
        K key = findKey(value);
        return remove(key);
    }

    public K findKey(V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void clear() {
        map.clear();
        values.clear();
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public void sort(Comparator<V> comparator) {
        values.sort(comparator);
    }

    public Array<V> getValues() {
        return values;
    }

    /** Warning: returned array will be reused! */
    public Array<K> getKeys() {
        Array<K> result = tmpKeyArray;
        result.clear();

        for (K key : map.keySet()) {
            result.add(key);
        }
        return result;
    }

    @Override
    public String toString() {
        return values.toString();
    }

    public String toString(String separator) {
        return values.toString(separator);
    }
}
