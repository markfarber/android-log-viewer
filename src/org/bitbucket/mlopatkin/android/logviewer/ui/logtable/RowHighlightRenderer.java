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
package org.bitbucket.mlopatkin.android.logviewer.ui.logtable;

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;
import org.bitbucket.mlopatkin.android.logviewer.config.Configuration;
import org.bitbucket.mlopatkin.android.logviewer.widgets.DecoratingCellRenderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class RowHighlightRenderer implements DecoratingCellRenderer {

    private TableCellRenderer inner;
    private Color backgroundColor = Configuration.ui.backgroundColor();
    private LogModelFilter colorer;

    public RowHighlightRenderer(LogModelFilter colorer) {
        this.colorer = colorer;
    }

    @Override
    public void setInnerRenderer(TableCellRenderer renderer) {
        inner = renderer;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component result = inner.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        if (!isSelected) {
            LogRecordTableModel tableModel = (LogRecordTableModel) table.getModel();
            int modelRow = table.convertRowIndexToModel(row);
            LogRecord record = tableModel.getRowData(modelRow);
            Color targetColor = colorer.getHighlightColor(record);
            if (targetColor != null) {
                result.setBackground(targetColor);
            } else {
                result.setBackground(backgroundColor);
            }
        }
        return result;
    }

}
