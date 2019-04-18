package com.cheng.activitystack.device;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WatchPanel extends JPanel {

    public WatchPanel() {
        setLayout(new BorderLayout());
        JLabel deviceName = new JLabel("deviceName");
        JButton button = new JButton("Watch");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("jfoiwejigjiwoejigoweg");
            }
        });
        setBorder(JBUI.Borders.empty(10, 30));
        add(deviceName, BorderLayout.WEST);
        add(button, BorderLayout.EAST);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("2");
                }
            }
        });
    }
}