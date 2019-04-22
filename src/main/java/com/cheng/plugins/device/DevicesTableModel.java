package com.cheng.plugins.device;

import com.android.ddmlib.IDevice;

import javax.swing.table.AbstractTableModel;

public class DevicesTableModel extends AbstractTableModel {
    private IDevice[] devices = new IDevice[0];

    public void updateDevice(IDevice[] devices) {
        this.devices = devices;
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
        return devices.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        return devices[row];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }
}
