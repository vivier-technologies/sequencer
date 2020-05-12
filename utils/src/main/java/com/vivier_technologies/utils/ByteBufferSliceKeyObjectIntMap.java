/*
 * Copyright 2020  vivier technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vivier_technologies.utils;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.nio.ByteBuffer;

/**
 * Reasonably efficient targeted map implementation using a section of the bytebuffer as a key for lookup
 *
 * Will only create objects on first key insert, not on subsequent lookups - object keys are not precreated
 * but could be in another implementation if desirable
 *
 * A less object centric version could be worthy of consideration assuming the keys can be constrained to be of
 * constant size
 */
public class ByteBufferSliceKeyObjectIntMap implements ByteBufferSliceKeyIntMap {

    /**
     * Wrapper key to avoid object generation to compare keys - which is the common case
     */
    private class ByteBufferSliceKey {
        private ByteBuffer _buffer;
        private int _position = -1;
        private int _length = -1;
        private int _hashCode = -1;

        /**
         * Use this constructor if you are creating a key to store that is immutable
         *
         * This will also preset the hashcode
         *
         * @param buffer source buffer
         * @param position position in source buffer - passing as don't want to disturb source buffer pos
         * @param length length of key
         */
        private ByteBufferSliceKey(ByteBuffer buffer, int position, int length) {
            _buffer = ByteBufferFactory.nativeAllocate(length);
            ByteArrayUtils.copy(buffer, position, _buffer.array(), 0, length);
            // slightly faster as no additional checks but probably not that meaningful
            _hashCode = _buffer.hashCode();
        }

        /**
         * Use this constructor if you are using this key as a reusable wrapper and will be setting
         * the key data later on
         */
        private ByteBufferSliceKey() {

        }

        @Override
        public int hashCode() {
            /**
             * setting hashcode as know for the usage of this class we will always be calling it
             * and the contents will either be immutable or the hashcode will be recomputed when the buffer
             * is set
             */
            return _hashCode;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ByteBufferSliceKey))
                return false;
            ByteBufferSliceKey that = (ByteBufferSliceKey)obj;
            // the bytebuffer equals method has some magic for speed of comparison which is nice but hidden in java internals
            // so to get hold of it need to make sure position and limit are good - bit ugly
            boolean equals;
            if(_position != -1) {
                int currentPos = _buffer.position();
                int currentLimit = _buffer.limit();
                _buffer.limit(_position+_length).position(_position);
                equals = _buffer.equals(that._buffer);
                _buffer.limit(currentLimit).position(currentPos);
            } else {
                equals = _buffer.equals(that._buffer);
            }
            return equals;
        }

        private void setBuffer(ByteBuffer buffer, int position, int length) {
            _buffer = buffer;
            _position = position;
            _length = length;

            int currentPos = buffer.position();
            int currentLimit = buffer.limit();

            // only want hash for part of the buffer we're interested in
            _hashCode = buffer.limit(position+length).position(position).hashCode();

            buffer.limit(currentLimit).position(currentPos);
        }
    }

    private final ByteBufferSliceKey _tempKey = new ByteBufferSliceKey();

    private final Object2IntOpenHashMap<ByteBufferSliceKey> _map;

    /**
     * Instantiate the map using initial capacity - very much recommended to set
     * a value that you think will exceed the maximum key size to avoid growth and garbage
     *
     * @param capacity initial capacity
     */
    public ByteBufferSliceKeyObjectIntMap(int capacity) {
        _map = new Object2IntOpenHashMap<>(capacity);
    }

    @Override
    public boolean compareAndSetIfIncrement(ByteBuffer key, int keyStart, int keyLength, int value) {
        _tempKey.setBuffer(key, keyStart, keyLength);
        int currentValue = _map.getOrDefault(_tempKey, 0);
        if (currentValue + 1 == value) {
            if (currentValue == 0) {
                _map.put(new ByteBufferSliceKey(key, keyStart, keyLength), value);
            } else {
                _map.addTo(_tempKey, 1);
            }
            return true;
        } else {
            return false;
        }
    }

}
