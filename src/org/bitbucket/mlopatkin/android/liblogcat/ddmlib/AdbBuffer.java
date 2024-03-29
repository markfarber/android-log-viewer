/*
 * Copyright 2011 Mikhail Lopatkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitbucket.mlopatkin.android.liblogcat.ddmlib;

import com.android.ddmlib.IDevice;

import org.apache.log4j.Logger;
import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;
import org.bitbucket.mlopatkin.android.liblogcat.LogRecordStream;

import java.util.Map;

/**
 * This class retrieves log records from the device using a background thread
 * and pushes them back to creator.
 */
class AdbBuffer {

    public interface BufferReceiver {
        void pushRecord(LogRecord record);
    }

    private static final Logger logger = Logger.getLogger(AdbBuffer.class);

    private final BufferReceiver receiver;
    private final ShellInputStream shellInput = new ShellInputStream();
    private final LogRecordStream in;
    private final LogRecord.Buffer buffer;
    private final PollingThread pollingThread;
    private final Thread shellExecutor;

    public AdbBuffer(BufferReceiver receiver, IDevice device, LogRecord.Buffer buffer,
            String commandLine, Map<Integer, String> pidToProcess) {
        this.receiver = receiver;
        this.buffer = buffer;
        in = new LogRecordStream(shellInput, pidToProcess);
        shellExecutor = new Thread(new AutoClosingAdbShellCommand(device, commandLine, shellInput),
                "Shell-reader-" + buffer);
        pollingThread = new PollingThread();
        shellExecutor.start();
        pollingThread.start();
    }

    void close() {
        pollingThread.close();
        shellInput.close();
    }

    private class PollingThread extends Thread {

        public PollingThread() {
            super("ADB-polling-" + buffer);
        }

        @Override
        public void run() {
            LogRecord record = in.next(buffer);
            while (!closed && record != null) {
                receiver.pushRecord(record);
                record = in.next(buffer);
            }
            if (closed) {
                logger.debug(getName() + " successfully ended");
            } else {
                logger.warn(getName() + " ends due to null record");
            }

        }

        private volatile boolean closed = false;

        public void close() {
            closed = true;
        }
    }

}
