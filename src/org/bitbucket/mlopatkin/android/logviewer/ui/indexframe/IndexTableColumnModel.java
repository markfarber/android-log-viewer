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
package org.bitbucket.mlopatkin.android.logviewer.ui.indexframe;

import org.bitbucket.mlopatkin.android.logviewer.PidToProcessMapper;
import org.bitbucket.mlopatkin.android.logviewer.ui.logtable.Column;
import org.bitbucket.mlopatkin.android.logviewer.ui.logtable.LogRecordTableColumnModel;

import java.util.Arrays;

public class IndexTableColumnModel extends LogRecordTableColumnModel {

    private IndexTableColumnModel(Builder b) {
        super(b, Arrays.asList(Column.INDEX, Column.TIME, Column.PID, Column.PRIORITY, Column.TAG, Column.MESSAGE));
    }

    public static IndexTableColumnModel create(PidToProcessMapper mapper) {
        Builder b = makeDefaultBuilder(mapper);
        b.addTextColumn(Column.INDEX).setWidth(30).setMaxWidth(50);

        return new IndexTableColumnModel(b);
    }
}