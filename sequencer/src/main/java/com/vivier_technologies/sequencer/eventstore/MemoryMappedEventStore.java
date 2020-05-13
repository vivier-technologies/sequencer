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

package com.vivier_technologies.sequencer.eventstore;

import com.vivier_technologies.events.Event;
import com.vivier_technologies.utils.Logger;
import org.apache.commons.configuration2.Configuration;

import javax.inject.Inject;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MemoryMappedEventStore implements EventStore {

    private static byte[] _loggingKey = Logger.generateLoggingKey("MMEVTSTORE");
    private static Logger _logger;

    private FileChannel _channel;
    private MappedByteBuffer _buffer;

    @Inject
    public MemoryMappedEventStore(Configuration config, Logger logger) {
        _logger = logger;
    }

    @Override
    public final void open() throws IOException {
        _channel = new RandomAccessFile("events.dat", "rw").getChannel();
        System.out.println(_channel.size());
        _buffer = _channel.map(FileChannel.MapMode.READ_WRITE, 0, Integer.MAX_VALUE);
        System.out.println(_channel.size());
        System.out.println(_buffer.position());
        _buffer.order(ByteOrder.LITTLE_ENDIAN);
        System.out.println(_channel.size());
        System.out.println(_buffer.position());
        _buffer.putInt(12);
        System.out.println(_buffer.position());
        _buffer.force();
        System.out.println(_channel.size());
        _channel.force(true);
        System.out.println(_channel.size());
        Files.delete(Path.of("events.dat"));
        System.out.println(Integer.MAX_VALUE / 12);
        //Files.by
    }

    @Override
    public final void close() {
        try {
            _buffer.force();
            _channel.force(true);
            _channel.close();
            _buffer = null;
        } catch (IOException e) {
            _logger.error(_loggingKey, "Unable to close socket");
        }
    }

    @Override
    public final boolean store(Event event) {
        return false;
    }

    @Override
    public final Events retrieve(long start, long end) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
