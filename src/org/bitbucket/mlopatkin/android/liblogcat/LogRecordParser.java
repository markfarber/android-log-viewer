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

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord.Buffer;
import org.bitbucket.mlopatkin.android.liblogcat.LogRecord.Priority;
import org.bitbucket.mlopatkin.utils.MyStringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse log record lines in different formats.
 */
public class LogRecordParser {

    private LogRecordParser() {
    }

    private static final String TIMESTAMP_REGEX = "(\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d)(?:\\d\\d\\d)?";
    private static final String ID_REGEX = "(\\d+)";
    private static final String PID_REGEX = ID_REGEX;
    private static final String PID_BRACKETS = "\\(\\s*" + PID_REGEX + "\\)";

    private static final String TID_REGEX = ID_REGEX;
    private static final String TAG_REGEX = "(.*?)";
    private static final String PRIORITY_REGEX = "([AVDIWEF])";
    private static final String MESSAGE_REGEX = "(.*)";
    private static final String SEP = "\\s+";
    private static final String SEP_OPT = "\\s*";

    private static class ThreadTime {

        private static final String TAG = TAG_REGEX + "\\s*: ";
        private static final String[] LOG_RECORD_FIELDS = {TIMESTAMP_REGEX, SEP, PID_REGEX, SEP,
                                                           TID_REGEX, SEP, PRIORITY_REGEX, SEP, TAG, MESSAGE_REGEX};
        private static final Pattern threadTimeRecordPattern =
                Pattern.compile("^" + MyStringUtils.join(LOG_RECORD_FIELDS) + "$");

        static Matcher matchLine(String line) {
            return threadTimeRecordPattern.matcher(line);
        }

        static LogRecord createFromGroups(Buffer buffer, Matcher m,
                                          Map<Integer, String> pidToProcess) {
            if (!m.matches()) {
                return null;
            }
            try {
                Date dateTime = TimeFormatUtils.getTimeFromString(m.group(1));
                int pid = Integer.parseInt(m.group(2));
                int tid = Integer.parseInt(m.group(3));
                Priority priority = getPriorityFromChar(m.group(4));
                String tag = m.group(5);
                String message = m.group(6);
                return new LogRecord(dateTime, pid, tid, pidToProcess.get(pid), priority, tag,
                                     message, buffer);
            } catch (ParseException e) {
                return new LogRecord(new Date(), -1, -1, "", Priority.ERROR, "Parse Error",
                                     m.group());
            }
        }
    }

    private static class Brief {

        private static final String[] LOG_RECORD_FIELDS = {PRIORITY_REGEX, "/", TAG_REGEX,
                                                           SEP_OPT, PID_BRACKETS, ": ", MESSAGE_REGEX};
        private static final Pattern briefRecordPattern =
                Pattern.compile("^" + MyStringUtils.join(LOG_RECORD_FIELDS) + "$");

        static Matcher matchLine(String line) {
            return briefRecordPattern.matcher(line);
        }

        static LogRecord createFromGroups(Buffer buffer, Matcher m,
                                          Map<Integer, String> pidToProcess) {
            if (!m.matches()) {
                return null;
            }
            Priority priority = getPriorityFromChar(m.group(1));
            String tag = m.group(2);
            int pid = Integer.parseInt(m.group(3));
            String message = m.group(4);

            return new LogRecord(null, pid, LogRecord.NO_ID, pidToProcess.get(pid), priority, tag,
                                 message, buffer);
        }
    }

    private static class Process {

        private static final String TAG_BRACKETS = "\\(" + TAG_REGEX + "\\)";
        private static final String[] LOG_RECORD_FIELDS = {PRIORITY_REGEX, PID_BRACKETS, " ",
                                                           MESSAGE_REGEX, "  ", TAG_BRACKETS};
        private static final Pattern processRecordPattern =
                Pattern.compile("^" + MyStringUtils.join(LOG_RECORD_FIELDS) + "$");

        static Matcher matchLine(String line) {
            return processRecordPattern.matcher(line);
        }

        static LogRecord createFromGroups(Buffer buffer, Matcher m,
                                          Map<Integer, String> pidToProcess) {
            if (!m.matches()) {
                return null;
            }
            Priority priority = getPriorityFromChar(m.group(1));
            int pid = Integer.parseInt(m.group(2));
            String message = m.group(3);
            String tag = m.group(4);

            return new LogRecord(null, pid, LogRecord.NO_ID, pidToProcess.get(pid), priority, tag,
                                 message, buffer);
        }
    }

    private static class Tag {

        private static final String[] LOG_RECORD_FIELDS = {PRIORITY_REGEX, "/", TAG_REGEX, ": ",
                                                           MESSAGE_REGEX};
        private static final Pattern tagRecordPattern =
                Pattern.compile("^" + MyStringUtils.join(LOG_RECORD_FIELDS) + "$");

        static Matcher matchLine(String line) {
            return tagRecordPattern.matcher(line);
        }

        static LogRecord createFromGroups(Buffer buffer, Matcher m) {
            if (!m.matches()) {
                return null;
            }
            Priority priority = getPriorityFromChar(m.group(1));
            String tag = m.group(2);
            String message = m.group(3);

            return new LogRecord(null, LogRecord.NO_ID, LogRecord.NO_ID, "", priority, tag, message,
                                 buffer);
        }
    }

    private static class Time {

        private static final String[] LOG_RECORD_FIELDS = {TIMESTAMP_REGEX, SEP, PRIORITY_REGEX,
                                                           "/", TAG_REGEX, SEP_OPT, PID_BRACKETS, ": ", MESSAGE_REGEX};
        private static final Pattern timeRecordPattern =
                Pattern.compile("^" + MyStringUtils.join(LOG_RECORD_FIELDS) + "$");

        static Matcher matchLine(String line) {
            return timeRecordPattern.matcher(line);
        }

        static LogRecord createFromGroups(Buffer buffer, Matcher m,
                                          Map<Integer, String> pidToProcess) {
            if (!m.matches()) {
                return null;
            }
            try {
                Date dateTime = TimeFormatUtils.getTimeFromString(m.group(1));
                Priority priority = getPriorityFromChar(m.group(2));
                String tag = m.group(3);
                int pid = Integer.parseInt(m.group(4));
                String message = m.group(5);
                return new LogRecord(dateTime, pid, LogRecord.NO_ID, pidToProcess.get(pid),
                                     priority, tag, message, buffer);
            } catch (ParseException e) {
                return new LogRecord(new Date(), -1, -1, "", Priority.ERROR, "Parse Error",
                                     m.group());
            }
        }
    }

    private static Priority getPriorityFromChar(String next) {
        next = next.trim();
        for (Priority val : Priority.values()) {
            if (val.getLetter().equalsIgnoreCase(next)) {
                return val;
            }
        }
        throw new IllegalArgumentException("Symbol '" + next
                                                   + "' doesn't correspond to valid priority value");
    }

    public static LogRecord parseThreadTime(Buffer buffer, String line,
                                            Map<Integer, String> pidToProcess) {
        return ThreadTime.createFromGroups(buffer, ThreadTime.matchLine(line), pidToProcess);
    }

    public static LogRecord parseBrief(Buffer buffer, String line,
                                       Map<Integer, String> pidToProcess) {
        return Brief.createFromGroups(buffer, Brief.matchLine(line), pidToProcess);
    }

    public static LogRecord parseProcess(Buffer buffer, String line,
                                         Map<Integer, String> pidToProcess) {
        return Process.createFromGroups(buffer, Process.matchLine(line), pidToProcess);
    }

    public static LogRecord parseTag(Buffer buffer, String line) {
        return Tag.createFromGroups(buffer, Tag.matchLine(line));
    }

    private static final String LOG_BEGIN = "--------- beginning of ";

    public static boolean isLogBeginningLine(String line) {
        return (line != null) && line.startsWith(LOG_BEGIN);
    }

    public static LogRecord parseTime(Buffer buffer, String line,
                                      Map<Integer, String> pidToProcess) {
        return Time.createFromGroups(buffer, Time.matchLine(line), pidToProcess);
    }

}
