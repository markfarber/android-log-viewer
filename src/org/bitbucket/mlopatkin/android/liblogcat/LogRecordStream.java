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

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * The utility class that parses it's input stream line-by-line assuming that
 * lines are in threadtime format.
 */
public class LogRecordStream {

    private static final Logger logger = Logger.getLogger(LogRecordStream.class);

    private BufferedReader in;

    private Map<Integer, String> pidToProcess;

    public LogRecordStream(InputStream in, Map<Integer, String> pidToProcess) {
        this.pidToProcess = pidToProcess;
        this.in = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
    }

    public LogRecord next(LogRecord.Buffer kind) {
        try {
            String line = in.readLine();
            while (!isLogEnd(line)) {
                LogRecord record = LogRecordParser.parseThreadTime(kind, line, pidToProcess);
                if (record != null) {
                    return record;
                } else {
                    logger.debug("Null record: " + line);
                }
                line = in.readLine();
            }
        } catch (IOException e) {
            logger.error("Unexpected IO exception", e);
        }
        return null;
    }

    protected boolean isLogEnd(String line) {
        return line == null;
    }
}
