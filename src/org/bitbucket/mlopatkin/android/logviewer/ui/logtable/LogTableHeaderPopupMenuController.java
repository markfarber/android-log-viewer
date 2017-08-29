/*
 * Copyright 2016 Mikhail Lopatkin
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

package org.bitbucket.mlopatkin.android.logviewer.ui.logtable;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;

/**
 * Controller for Popup menu of the log table header. This popup menu can be used to change displayed columns.
 */
public class LogTableHeaderPopupMenuController {

    private final LogRecordTableColumnModel columnModel;

    private class ToggleColumnAction extends AbstractAction {
        private final Column column;

        public ToggleColumnAction(Column column) {
            super(column.getColumnName());
            this.column = column;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            boolean isSelected = ((JCheckBoxMenuItem)e.getSource()).isSelected();
            columnModel.setColumnVisibility(column, isSelected);
        }

        public JCheckBoxMenuItem createMenuItem() {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(this);
            item.setSelected(columnModel.isColumnVisible(column));
            return item;
        }
    }

    public LogTableHeaderPopupMenuController(LogRecordTableColumnModel columnModel) {
        this.columnModel = columnModel;
    }

    public JPopupMenu createMenu() {
        JPopupMenu menu = new JPopupMenu();
        for (Column c : Column.values()) {
            if (c.isToggleable()) {
                menu.add(new ToggleColumnAction(c).createMenuItem());
            }
        }
        return menu;
    }
}
