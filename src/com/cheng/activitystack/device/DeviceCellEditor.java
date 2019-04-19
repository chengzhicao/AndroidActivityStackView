package com.cheng.activitystack.device;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DeviceCellEditor extends AbstractCellEditor
        implements TableCellEditor, TableCellRenderer {

    public DeviceCellEditor(WatchListener watchListener) {
        this.watchListener = watchListener;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return new WatchPanel(((Device) value).getName());
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return new WatchPanel(((Device) value).getName());
    }

    private class WatchPanel extends JPanel {
        private WatchPanel(String deviceName) {
            setLayout(new BorderLayout());
            JLabel labDevice = new JLabel(deviceName);
            JButton btnWatch = new JButton("Watch");
            btnWatch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (watchListener != null) {
                        watchListener.watch();
                    }
                }
            });
            setBorder(JBUI.Borders.empty(10, 30));
            add(labDevice, BorderLayout.WEST);
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
