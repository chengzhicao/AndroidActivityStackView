package com.cheng.plugins.device;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class DevicesTableModel extends AbstractTableModel {
    private List<Device> devices = new ArrayList<>();

    public void updateDevice(List<Device> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return null;
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return devices.size();
    }

    @Override
    public Object getValueAt(int row, int column) {
        return devices.get(row);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
}
