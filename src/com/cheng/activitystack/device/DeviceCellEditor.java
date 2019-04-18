package com.cheng.activitystack.device;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DeviceCellEditor extends AbstractCellEditor
        implements TableCellEditor,TableCellRenderer {
    private WatchPanel buttonsPanel = new WatchPanel();
    private WatchPanel buttonsPanel2 = new WatchPanel();

    public DeviceCellEditor() {
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return buttonsPanel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return buttonsPanel2;
    }
}
