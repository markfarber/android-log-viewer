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

import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class LogRecordParserTest {

    static final String BRIEF_RECORD = "D/MediaScanner(417): postscan return";
    static final String BRIEF_RECORD_PAD = "D/MediaScanner   (  417): postscan return";
    static final String PROCESS_RECORD = "D(  417) postscan return  (MediaScanner)";
    static final String TAG_RECORD = "D/MediaScanner: postscan return";
    static final String THREAD_RECORD = "D(  417:0x494) postscan return";
    static final String THREADTIME_RECORD = "08-18 13:40:59.546   417  1172 D MediaScanner: postscan return";
    static final String THREADTIME_RECORD_WITH_MCS =
            "08-18 13:40:59.546789   417  1172 D MediaScanner: postscan return";
    static final String RAW_RECORD = "postscan return";
    static final String TIME_RECORD = "08-18 13:40:59.546 D/MediaScanner(417): postscan return";
    static final String TIME_RECORD_WITH_MCS = "08-18 13:40:59.546789 D/MediaScanner(417): postscan return";
    static final String TIME_RECORD_PAD = "08-18 13:40:59.546 D/MediaScanner   (  417): postscan return";
    static final String LONG_RECORD = "[ 08-18 13:40:59.546   417:0x494 D/MediaScanner ]\n"
            + "postscan return";

    static final String TAG = "MediaScanner";
    static final String MESSAGE = "postscan return";
    static final int PID = 417;
    static final int TID = 1172;
    static final LogRecord.Priority PRIORITY = LogRecord.Priority.DEBUG;
    static final Date DATE = getDate(getCurrentYear(), 8, 18, 13, 40, 59, 546);
    static final LogRecord.Buffer BUFFER = LogRecord.Buffer.MAIN;

    static final String LOG_BEGINNING_LINE = "--------- beginning of /dev/log/system";
    static final String BLANK_LINE = "";

    private static final int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private static Date getDate(int year, int month, int day, int hour, int min, int sec, int msec) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, min, sec);
        calendar.set(Calendar.MILLISECOND, msec);
        return calendar.getTime();
    }

    private static boolean isSameDate(Date expected, Date actual) {
        if (expected.equals(actual)) {
            return true;
        }
        Calendar calExpected = Calendar.getInstance();
        calExpected.setTime(expected);
        Calendar calActual = Calendar.getInstance();
        calActual.setTime(actual);
        calExpected.set(Calendar.YEAR, calActual.get(Calendar.YEAR));
        expected = calExpected.getTime();
        return expected.equals(actual);
    }

    @Test
    public void testLogRecordParserThreadTime() {
        LogRecord record =
                LogRecordParser.parseThreadTime(BUFFER, THREADTIME_RECORD, Collections.<Integer, String>emptyMap());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertEquals(TID, record.getTid());
        assertTrue(isSameDate(DATE, record.getTime()));
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserBrief() {
        LogRecord record = LogRecordParser.parseBrief(BUFFER, BRIEF_RECORD, Collections.<Integer, String>emptyMap());

        assertNull(record.getTime());
        assertEquals(LogRecord.NO_ID, record.getTid());

        assertEquals(PID, record.getPid());
        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserBriefPad() {
        LogRecord record =
                LogRecordParser.parseBrief(BUFFER, BRIEF_RECORD_PAD, Collections.<Integer, String>emptyMap());

        assertNull(record.getTime());
        assertEquals(LogRecord.NO_ID, record.getTid());

        assertEquals(PID, record.getPid());
        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserProcess() {
        LogRecord record =
                LogRecordParser.parseProcess(BUFFER, PROCESS_RECORD, Collections.<Integer, String>emptyMap());

        assertEquals(LogRecord.NO_ID, record.getTid());
        assertNull(record.getTime());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserTag() {
        LogRecord record = LogRecordParser.parseTag(BUFFER, TAG_RECORD);

        assertEquals(LogRecord.NO_ID, record.getTid());
        assertNull(record.getTime());
        assertEquals(LogRecord.NO_ID, record.getPid());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserTime() {
        LogRecord record = LogRecordParser.parseTime(BUFFER, TIME_RECORD, Collections.<Integer, String>emptyMap());

        assertEquals(LogRecord.NO_ID, record.getTid());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertTrue(isSameDate(DATE, record.getTime()));
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testLogRecordParserTimePad() {
        LogRecord record = LogRecordParser.parseTime(BUFFER, TIME_RECORD_PAD, Collections.<Integer, String>emptyMap());

        assertEquals(LogRecord.NO_ID, record.getTid());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertTrue(isSameDate(DATE, record.getTime()));
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testIsLogBeginningLine() {
        // positive test
        assertTrue(LogRecordParser.isLogBeginningLine(LOG_BEGINNING_LINE));
        // negative tests
        assertFalse(LogRecordParser.isLogBeginningLine(BLANK_LINE));
        assertFalse(LogRecordParser.isLogBeginningLine(BRIEF_RECORD));
        assertFalse(LogRecordParser.isLogBeginningLine(THREADTIME_RECORD));
    }

    @Test
    public void testTimeLogWithMicrosecondsTimestamp() {
        LogRecord record = LogRecordParser.parseTime(BUFFER, TIME_RECORD_WITH_MCS, Collections.emptyMap());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertTrue(isSameDate(DATE, record.getTime()));
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }

    @Test
    public void testThreadTimeLogWithMicrosecondsTimestamp() {
        LogRecord record =
                LogRecordParser.parseThreadTime(BUFFER, THREADTIME_RECORD_WITH_MCS, Collections.emptyMap());

        assertEquals(TAG, record.getTag());
        assertEquals(MESSAGE, record.getMessage());
        assertEquals(PID, record.getPid());
        assertEquals(TID, record.getTid());
        assertTrue(isSameDate(DATE, record.getTime()));
        assertEquals(PRIORITY, record.getPriority());
        assertEquals(BUFFER, record.getBuffer());
    }
}
