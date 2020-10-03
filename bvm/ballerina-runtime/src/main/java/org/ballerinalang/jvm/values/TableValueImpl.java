/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.jvm.values;

import org.ballerinalang.jvm.CycleUtils;
import org.ballerinalang.jvm.IteratorUtils;
import org.ballerinalang.jvm.TableUtils;
import org.ballerinalang.jvm.TypeChecker;
import org.ballerinalang.jvm.api.BErrorCreator;
import org.ballerinalang.jvm.api.BStringUtils;
import org.ballerinalang.jvm.api.BValueCreator;
import org.ballerinalang.jvm.api.values.BIterator;
import org.ballerinalang.jvm.api.values.BLink;
import org.ballerinalang.jvm.api.values.BString;
import org.ballerinalang.jvm.types.BField;
import org.ballerinalang.jvm.types.BMapType;
import org.ballerinalang.jvm.types.BRecordType;
import org.ballerinalang.jvm.types.BTableType;
import org.ballerinalang.jvm.types.BTupleType;
import org.ballerinalang.jvm.types.BType;
import org.ballerinalang.jvm.types.TypeTags;
import org.ballerinalang.jvm.util.exceptions.BLangFreezeException;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.ballerinalang.jvm.util.BLangConstants.TABLE_LANG_LIB;
import static org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons.INHERENT_TYPE_VIOLATION_ERROR_IDENTIFIER;
import static org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons.OPERATION_NOT_SUPPORTED_ERROR;
import static org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons.TABLE_HAS_A_VALUE_FOR_KEY_ERROR;
import static org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons.TABLE_KEY_NOT_FOUND_ERROR;
import static org.ballerinalang.jvm.util.exceptions.BallerinaErrorReasons.getModulePrefixedReason;

/**
 * The runtime representation of table.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @since 1.3.0
 */
public class TableValueImpl<K, V> implements TableValue<K, V> {

    private BTableType type;
    private BType iteratorNextReturnType;
    private ConcurrentHashMap<Long, Map.Entry<K, V>> entries;
    private LinkedHashMap<Long, V> values;
    private LinkedHashMap<Long, K> keys;
    private String[] fieldNames;
    private ValueHolder valueHolder;
    private long maxIntKey = 0;

    //These are required to achieve the iterator behavior
    private LinkedHashMap<Long, Long> indexToKeyMap;
    private LinkedHashMap<Long, Long> keyToIndexMap;
    private long noOfAddedEntries = 0;

    private boolean nextKeySupported;

    private final Map<String, Object> nativeData = new HashMap<>();

    public TableValueImpl(BTableType type) {
        this.type = type;

        this.entries = new ConcurrentHashMap<>();
        this.keys = new LinkedHashMap<>();
        this.values = new LinkedHashMap<>();
        this.keyToIndexMap = new LinkedHashMap<>();
        this.indexToKeyMap = new LinkedHashMap<>();
        this.fieldNames = type.getFieldNames();
        if (type.getFieldNames() != null) {
            this.valueHolder = new KeyHashValueHolder();
        } else {
            this.valueHolder = new ValueHolder();
        }
    }

    public TableValueImpl(BTableType type, ArrayValue data, ArrayValue fieldNames) {
        this(type);
        if (this.fieldNames == null) {
            this.fieldNames = fieldNames.getStringArray();
        }

        addData(data);
    }

    private void addData(ArrayValue data) {
        BIterator itr = data.getIterator();
        while (itr.hasNext()) {
            Object next = itr.next();
            valueHolder.addData((V) next);
        }
    }

    @Override
    public IteratorValue getIterator() {
        return new TableIterator<K, V>();
    }

    @Override
    public Object copy(Map<Object, Object> refs) {
        if (isFrozen()) {
            return this;
        }

        if (refs.containsKey(this)) {
            return refs.get(this);
        }

        TableValueImpl<K, V> clone = new TableValueImpl<>(type);
        if (fieldNames != null) {
            clone.fieldNames = fieldNames;
        }

        IteratorValue itr = getIterator();
        while (itr.hasNext()) {
            TupleValueImpl tupleValue = (TupleValueImpl) itr.next();
            Object value = tupleValue.get(1);
            value = value instanceof RefValue ? ((RefValue) value).copy(refs) : value;
            clone.add((V) value);
        }

       return clone;
    }

    @Override
    public Object frozenCopy(Map<Object, Object> refs) {
        TableValueImpl<K, V> copy = (TableValueImpl<K, V>) copy(refs);
        if (!copy.isFrozen()) {
            copy.freezeDirect();
        }
        return copy;
    }

    protected void handleFrozenTableValue() {
        synchronized (this) {
            try {
                if (this.type.isReadOnly()) {
                    ReadOnlyUtils.handleInvalidUpdate(TABLE_LANG_LIB);
                }
            } catch (BLangFreezeException e) {
                throw BErrorCreator.createError(BStringUtils.fromString(e.getMessage()),
                                                BStringUtils.fromString(e.getDetail()));
            }
        }
    }

    @Override
    public V get(Object key) {
        return valueHolder.getData((K) key);
    }

    //Generates the key from the given data
    public V put(V value) {
        handleFrozenTableValue();
        return valueHolder.putData(value);
    }

    @Override
    public V put(K key, V value) {
        handleFrozenTableValue();
        return valueHolder.putData(key, value);
    }

    @Override
    public void add(V data) {
        handleFrozenTableValue();
        valueHolder.addData(data);
    }

    @Override
    public V remove(Object key) {
        handleFrozenTableValue();
        return valueHolder.remove((K) key);
    }

    @Override
    public boolean containsKey(Object key) {
        return valueHolder.containsKey((K) key);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new LinkedHashSet<>(entries.values());
    }

    @Override
    public Collection<V> values() {
        return values.values();
    }

    @Override
    public void clear() {
        handleFrozenTableValue();
        entries.clear();
        keys.clear();
        values.clear();
        keyToIndexMap.clear();
        indexToKeyMap.clear();
        noOfAddedEntries = 0;
    }

    @Override
    public V getOrThrow(Object key) {
        if (!containsKey(key)) {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }
        return this.get(key);
    }

    public V removeOrThrow(Object key) {
        handleFrozenTableValue();
        if (!containsKey(key)) {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }
        return this.remove(key);
    }

    public long getNextKey() {
        if (!nextKeySupported) {
            throw BErrorCreator.createError(OPERATION_NOT_SUPPORTED_ERROR,
                                            BStringUtils
                                                    .fromString("Defined key sequence is not supported with nextKey(). "
                                                                        + "The key sequence should only have an " +
                                                                           "Integer field."));
        }
        return keys.size() == 0 ? 0 : (this.maxIntKey + 1);
    }

    public BType getKeyType() {
        return this.valueHolder.getKeyType();
    }

    @Override
    public V fillAndGet(Object key) {
        if (containsKey(key)) {
            return this.get(key);
        }

        BType expectedType = (this.type).getConstrainedType();

        if (!TypeChecker.hasFillerValue(expectedType)) {
            // Panic if the field does not have a filler value.
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }

        Object value = expectedType.getZeroValue();
        this.put((K) key, (V) value);
        return (V) value;
    }

    @Override
    public K[] getKeys() {
        return (K[]) keys.values().toArray(new Object[]{});
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public void addNativeData(String key, Object data) {
        nativeData.put(key, data);
    }

    @Override
    public Object getNativeData(String key) {
        return nativeData.get(key);
    }

    @Override
    public void freezeDirect() {
        if (isFrozen()) {
            return;
        }

        this.type = (BTableType) ReadOnlyUtils.setImmutableTypeAndGetEffectiveType(this.type);
        //we know that values are always RefValues
        this.values().forEach(val -> ((RefValue) val).freezeDirect());
    }

    public String stringValue(BLink parent) {
        Iterator<Map.Entry<Long, V>> itr = values.entrySet().iterator();
        return createStringValueDataEntry(itr, parent);
    }

    @Override
    public String informalStringValue(BLink parent) {
        return stringValue(parent);
    }

    @Override
    public String expressionStringValue(BLink parent) {
        Iterator<Map.Entry<Long, V>> itr = values.entrySet().iterator();
        return createExpressionStringValueDataEntry(itr, parent);
    }

    private String createStringValueDataEntry(Iterator<Map.Entry<Long, V>> itr, BLink parent) {
        StringJoiner sj = new StringJoiner(",");
        while (itr.hasNext()) {
            Map.Entry<Long, V> struct = itr.next();
            sj.add(BStringUtils.getStringValue(struct.getValue(),
                    new CycleUtils.Node(this, parent)));
        }
        return "[" + sj.toString() + "]";
    }

    private String createExpressionStringValueDataEntry(Iterator<Map.Entry<Long, V>> itr, BLink parent) {
        StringJoiner sj = new StringJoiner(",");
        StringJoiner keyJoiner = new StringJoiner(",");
        if (type.getFieldNames() != null) {
            String[] keysList = type.getFieldNames();
            for (int i = 0; i < keysList.length; i++) {
                keyJoiner.add(keysList[i]);
            }
        }
        while (itr.hasNext()) {
            Map.Entry<Long, V> struct = itr.next();
            sj.add(BStringUtils.getExpressionStringValue(struct.getValue(),
                    new CycleUtils.Node(this, parent)));
        }
        return "table key(" + keyJoiner.toString() + ") [" + sj.toString() + "]";
    }

    private BType getTableConstraintField(BType constraintType, String fieldName) {
        if (constraintType.getTag() == TypeTags.RECORD_TYPE_TAG) {
            Map<String, BField> fieldList = ((BRecordType) constraintType).getFields();
            return fieldList.get(fieldName).getFieldType();
        } else if (constraintType.getTag() == TypeTags.MAP_TAG) {
            return ((BMapType) constraintType).getConstrainedType();
        }
        return null;
    }

    @Override
    public BType getType() {
        return this.type;
    }

    public BType getIteratorNextReturnType() {
        if (iteratorNextReturnType == null) {
            iteratorNextReturnType = IteratorUtils.createIteratorNextReturnType(type.getConstrainedType());
        }

        return iteratorNextReturnType;
    }

    private class TableIterator<K, V> implements IteratorValue {
        private long cursor;

        TableIterator() {
            this.cursor = 0;
        }

        @Override
        public Object next() {
            Long hash = indexToKeyMap.get(cursor);
            if (hash != null) {
                Map.Entry<K, V> next = (Map.Entry<K, V>) entries.get(hash);
                V value = next.getValue();
                K key = next.getKey();

                List<BType> types = new ArrayList<>();
                types.add(TypeChecker.getType(key));
                types.add(TypeChecker.getType(value));
                BTupleType tupleType = new BTupleType(types);

                TupleValueImpl tuple = new TupleValueImpl(tupleType);
                tuple.add(0, key);
                tuple.add(1, value);
                cursor++;
                return tuple;
            } else {
                cursor++;
                return next();
            }
        }

        @Override
        public boolean hasNext() {
           return cursor < noOfAddedEntries;
        }
    }

    private class ValueHolder {

        public void addData(V data) {
            putData(data);
        }

        public V getData(K key) {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }

        public V putData(K key, V data) {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }

        public V putData(V data) {
            checkInherentTypeViolation((MapValue) data, type);
            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry(data, data);
            UUID uuid = UUID.randomUUID();
            entries.put((long) uuid.hashCode(), entry);
            updateIndexKeyMappings((long) uuid.hashCode());
            return values.put((long) uuid.hashCode(), data);
        }

        public V remove(K key) {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("cannot find key '" + key + "'"));
        }

        public boolean containsKey(K key) {
            return false;
        }

        public BType getKeyType() {
            throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR,
                                            BStringUtils.fromString("keys are not defined"));
        }
    }

    private class KeyHashValueHolder extends ValueHolder {
        private DefaultKeyWrapper keyWrapper;
        private BType keyType;

        public KeyHashValueHolder() {
            super();
            if (fieldNames.length > 1) {
                keyWrapper = new MultiKeyWrapper();
            } else {
                keyWrapper = new DefaultKeyWrapper();
            }
        }

        public void addData(V data) {
            MapValue dataMap = (MapValue) data;
            checkInherentTypeViolation(dataMap, type);
            K key = this.keyWrapper.wrapKey(dataMap);

            if (containsKey((K) key)) {
                throw BErrorCreator.createError(TABLE_HAS_A_VALUE_FOR_KEY_ERROR,
                                                BStringUtils.fromString("A value " + "found for key '" + key + "'"));
            }

            if (nextKeySupported && (keys.size() == 0 || maxIntKey < TypeChecker.anyToInt(key))) {
                maxIntKey = ((Long) TypeChecker.anyToInt(key)).intValue();
            }

            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry(key, data);
            Long hash = TableUtils.hash(key, null);
            putData(key, data, entry, hash);
        }

        public V getData(K key) {
            return values.get(TableUtils.hash(key, null));
        }

        public V putData(K key, V data) {
            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<>(key, data);
            Object actualKey = this.keyWrapper.wrapKey((MapValue) data);
            Long actualHash = TableUtils.hash(actualKey, null);
            Long hash = TableUtils.hash(key, null);

            if (!hash.equals(actualHash)) {
                throw BErrorCreator.createError(TABLE_KEY_NOT_FOUND_ERROR, BStringUtils.fromString("The key '" +
                        key + "' not found in value " + data.toString()));
            }

            return putData(key, data, entry, hash);
        }

        private V putData(K key, V data, Map.Entry<K, V> entry, Long hash) {
            entries.put(hash, entry);
            keys.put(hash, key);
            updateIndexKeyMappings(hash);
            return values.put(hash, data);
        }

        public V putData(V data) {
            MapValue dataMap = (MapValue) data;
            checkInherentTypeViolation(dataMap, type);
            K key = this.keyWrapper.wrapKey(dataMap);
            Map.Entry<K, V> entry = new AbstractMap.SimpleEntry<>(key, data);
            Long hash = TableUtils.hash(key, null);
            return putData((K) key, data, entry, hash);
        }

        public V remove(K key) {
            Long hash = TableUtils.hash(key, null);
            entries.remove(hash);
            keys.remove(hash);
            Long index = keyToIndexMap.remove(hash);
            indexToKeyMap.remove(index);
            if (index != null && index == noOfAddedEntries - 1) {
                noOfAddedEntries--;
            }
            return values.remove(hash);
        }

        public boolean containsKey(K key) {
            return keys.containsKey(TableUtils.hash(key, null));
        }

        public BType getKeyType() {
            return keyType;
        }

        private class DefaultKeyWrapper {

            public DefaultKeyWrapper() {
                if (fieldNames.length == 1) {
                    keyType = getTableConstraintField(type.getConstrainedType(), fieldNames[0]);
                    if (keyType != null && keyType.getTag() == TypeTags.INT_TAG) {
                        nextKeySupported = true;
                    }
                }
            }

            public K wrapKey(MapValue data) {
                return (K) data.get(BStringUtils.fromString(fieldNames[0]));
            }
        }

        private class MultiKeyWrapper extends DefaultKeyWrapper {

            public MultiKeyWrapper() {
                super();
                List<BType> keyTypes = new ArrayList<>();
                BType constraintType = type.getConstrainedType();
                if (constraintType.getTag() == TypeTags.RECORD_TYPE_TAG) {
                    BRecordType recordType = (BRecordType) constraintType;
                    Arrays.stream(fieldNames).forEach(field -> keyTypes.add(recordType.getFields().get(field).type));
                } else if (constraintType.getTag() == TypeTags.MAP_TAG) {
                    BMapType mapType = (BMapType) constraintType;
                    Arrays.stream(fieldNames).forEach(field -> keyTypes.add(mapType.getConstrainedType()));
                }
                keyType = new BTupleType(keyTypes);
            }

            public K wrapKey(MapValue data) {
                TupleValueImpl arr = (TupleValueImpl) BValueCreator
                        .createTupleValue((BTupleType) keyType);
                for (int i = 0; i < fieldNames.length; i++) {
                    arr.add(i, data.get(BStringUtils.fromString(fieldNames[i])));
                }
                return (K) arr;
            }
        }
    }

    // This method updates the indexes and the order required by the iterators
    private void updateIndexKeyMappings(Long hash) {
        if (!keyToIndexMap.containsKey(hash)) {
            keyToIndexMap.put(hash, noOfAddedEntries);
            indexToKeyMap.put(noOfAddedEntries, hash);
            noOfAddedEntries++;
        }
    }

    // This method checks for inherent table type violation
    private void checkInherentTypeViolation(MapValue dataMap, BTableType type) {
        if (!TypeChecker.checkIsType(dataMap.getType(), type.getConstrainedType())) {
            BString reason = getModulePrefixedReason(TABLE_LANG_LIB, INHERENT_TYPE_VIOLATION_ERROR_IDENTIFIER);
            BString detail = BStringUtils.fromString("value type '" + dataMap.getType() + "' inconsistent with the " +
                                                            "inherent table type '" + type + "'");
            throw BErrorCreator.createError(reason, detail);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableValueImpl<?, ?> tableValue = (TableValueImpl<?, ?>) o;

        if (tableValue.type.getTag() != this.type.getTag()) {
            return false;
        }

        if (this.entrySet().size() != tableValue.entrySet().size()) {
            return false;
        }

        return entrySet().equals(tableValue.entrySet());
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    @Override
    public Long getIntValue(BString key) {
        return (Long) get(key);
    }

    @Override
    public Double getFloatValue(BString key) {
        return (Double) get(key);
    }

    @Override
    public BString getStringValue(BString key) {
        return (BString) get(key);
    }

    @Override
    public Boolean getBooleanValue(BString key) {
        return (Boolean) get(key);
    }

    @Override
    public MapValueImpl<?, ?> getMapValue(BString key) {
        return (MapValueImpl<?, ?>) get(key);
    }

    @Override
    public ObjectValue getObjectValue(BString key) {
        return (ObjectValue) get(key);
    }

    @Override
    public ArrayValue getArrayValue(BString key) {
        return (ArrayValue) get(key);
    }
}
