package com.cheng.plugins.device;

import com.android.ddmlib.IDevice;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeviceCellEditor extends AbstractCellEditor
        implements TableCellEditor, TableCellRenderer {

    public DeviceCellEditor(WatchListener watchListener) {
        this.watchListener = watchListener;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return new WatchPanel((IDevice) value);
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new WatchPanel((IDevice) value);
    }

    private class WatchPanel extends JPanel {
        private WatchPanel(IDevice device) {
            setLayout(new BorderLayout());
            JPanel jPanel = new JPanel(new BorderLayout());
            String name;
            if (device.isEmulator()) {
                name = device.getAvdName();
            } else {
                name = device.getProperty(IDevice.PROP_DEVICE_MODEL);
            }
            if (!device.isOnline()) {
                name = String.format("<html>%s <nobr style=\"font-weight:bold;\">[%s]</nobr></html>", name, device.getState());
            }
            JLabel labModel = new JLabel(name);
            jPanel.add(labModel, BorderLayout.NORTH);
            JLabel labSerialNumber = new JLabel(device.getSerialNumber());
            labSerialNumber.setForeground(JBColor.GRAY);
            jPanel.add(labSerialNumber, BorderLayout.SOUTH);
            JButton btnWatch = new JButton("Watch");
            btnWatch.addActionListener(e -> {
                if (watchListener != null) {
                    watchListener.watch();
                }
            });
            setBorder(JBUI.Borders.empty(10, 30));
            add(jPanel, BorderLayout.WEST);
            add(btnWatch, BorderLayout.EAST);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && watchListener != null) {
                        watchListener.watch();
                    }
                }
            });
        }
    }

    private WatchListener watchListener;

    public interface WatchListener {
        void watch();
    }
}
