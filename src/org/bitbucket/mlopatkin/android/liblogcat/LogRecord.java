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
package org.bitbucket.mlopatkin.android.liblogcat;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import java.util.Date;

/**
 * This class contains all available log record data like timestamp, tag,
 * message, etc.
 */
public class LogRecord implements Comparable<LogRecord> {

    public enum Priority {
        VERBOSE, DEBUG, INFO, WARN, ERROR, FATAL;

        public String getLetter() {
            return toString().substring(0, 1);
        }

        public static final Priority LOWEST = VERBOSE;
    }

    public enum Buffer {
        UNKNOWN, MAIN("Main"), SYSTEM("System"), RADIO("Radio"), EVENTS("Events"), CRASH("Crash");

        private final String name;

        public String getCaption() {
            return name;
        }

        Buffer() {
            name = null;
        }

        Buffer(String name) {
            this.name = name;
        }
    }

    public static final int NO_ID = -1;

    private final Date time;
    private final int pid;
    private final int tid;
    private final Priority priority;
    private final String tag;
    private final String message;
    private final Buffer buffer;
    private final String appName;

    public LogRecord(Date time, int pid, int tid, String appName, Priority priority, String tag,
            String message) {
        this(time, pid, tid, appName, priority, tag, message, Buffer.UNKNOWN);
    }

    public LogRecord(Date time, int pid, int tid, String appName, Priority priority, String tag,
            String message,
            Buffer buffer) {
        this.time = time;
        this.pid = pid;
        this.tid = tid;
        this.appName = CharMatcher.whitespace().trimFrom(Strings.nullToEmpty(appName));
        this.priority = priority;
        this.tag = tag;
        this.message = message;
        this.buffer = buffer;
    }

    public Date getTime() {
        return time;
    }

    public int getPid() {
        return pid;
    }

    public int getTid() {
        return tid;
    }

    public Priority getPriority() {
        return priority;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public String getAppName() {
        return appName;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (time != null) {
            b.append(TimeFormatUtils.convertTimeToString(time)).append('\t');
        }
        if (pid != NO_ID) {
            b.append(pid).append('\t');
        }
        if (tid != NO_ID) {
            b.append(tid).append('\t');
        }
        if (priority != null) {
            b.append(priority.getLetter()).append('\t');
        }
        if (tag != null) {
            b.append(tag).append('\t');
        }
        if (message != null) {
            b.append(message);
        }
        return b.toString();
    }

    /**
     * Performs timestamp comparison in ascending order.
     */
    @Override
    public int compareTo(LogRecord o) {
        int timeCompare = getTime().compareTo(o.getTime());
        if (timeCompare == 0) {
            return getBuffer().compareTo(o.getBuffer());
        } else {
            return timeCompare;
        }
    }

}
