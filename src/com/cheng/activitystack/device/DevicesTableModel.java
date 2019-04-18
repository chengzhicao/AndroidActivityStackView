package com.cheng.activitystack.device;

import javax.swing.table.DefaultTableModel;

public class DevicesTableModel extends DefaultTableModel {

    public DevicesTableModel() {
    }

    @Override
    public int getColumnCount() {
        return 1;
    }

    @Override
    public int getRowCount() {
        return 100;
    }
}
