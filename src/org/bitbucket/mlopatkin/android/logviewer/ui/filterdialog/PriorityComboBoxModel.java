/*
 * Copyright 2014 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.android.logviewer.ui.filterdialog;

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

class PriorityComboBoxModel extends AbstractListModel<LogRecord.Priority> implements ComboBoxModel<LogRecord.Priority> {

    private Object selected;

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    @Override
    public void setSelectedItem(Object anItem) {
        selected = anItem;
    }

    @Override
    public LogRecord.Priority getElementAt(int index) {
        if (index == 0) {
            return null;
        }
        return LogRecord.Priority.values()[index - 1];
    }

    @Override
    public int getSize() {
        return LogRecord.Priority.values().length + 1;
    }
}
