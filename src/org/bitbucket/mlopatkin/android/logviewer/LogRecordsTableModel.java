package org.bitbucket.mlopatkin.android.logviewer;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.bitbucket.mlopatkin.android.liblogcat.LogRecord;
import org.bitbucket.mlopatkin.android.liblogcat.LogRecord.Priority;

public class LogRecordsTableModel extends AbstractTableModel {

    private List<LogRecord> records;
    
    private static final int COLUMNS_COUNT = 6;
    
    public static final int COLUMN_PRIORITY = 3;
    public static final int COLUMN_TIME = 0;
    
    public LogRecordsTableModel(List<LogRecord> records) {
        this.records = records;
    }

    @Override
    public int getColumnCount() {
        return COLUMNS_COUNT;
    }

    @Override
    public int getRowCount() {
        return records.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        LogRecord record = records.get(rowIndex);
        switch (columnIndex) {
        case COLUMN_TIME:
            return record.getTime();
        case 1:
            return record.getPid();
        case 2:
            return record.getTid();
        case COLUMN_PRIORITY:
            return record.getPriority();
        case 4:
            return record.getTag();
        case 5:
            return record.getMessage();
        default:
            throw new IllegalArgumentException("Incorrect column number");
        }        
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case COLUMN_TIME:
            return Date.class;
        case COLUMN_PRIORITY:
            return Priority.class;
        default:
            return super.getColumnClass(columnIndex);
        }        
    }
}
