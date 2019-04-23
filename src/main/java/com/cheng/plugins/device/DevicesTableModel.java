/*
 * Copyright 2019 ChengzhiCao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
