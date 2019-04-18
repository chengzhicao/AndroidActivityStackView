package com.cheng.activitystack;

import com.cheng.activitystack.adb.ActivityStackCommand;
import com.cheng.activitystack.adb.DeviceService;
import com.cheng.activitystack.device.Device;
import com.cheng.activitystack.device.DevicesTableModel;
import com.cheng.activitystack.device.DeviceCellEditor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class StackPanel extends SimpleToolWindowPanel implements DeviceService.DevicesListener {
    private JPanel noDevicesJPanel;
    private boolean filter;
    private DefaultMutableTreeNode allTree;
    private DefaultMutableTreeNode filterTree;
    private List<DefaultMutableTreeNode> filterNode = new ArrayList<>();

    public StackPanel(@NotNull Project project) {
        super(true, true);
        setToolbar(createToolbarPanel());
        DeviceService deviceService = new DeviceService();
        deviceService.setDevicesListener(this);
        deviceService.startService();
        DevicesTableModel devicesTableModel = new DevicesTableModel();
        JBTable jTable = new JBTable(devicesTableModel);
        DeviceCellEditor deviceCellEditor = new DeviceCellEditor();
        jTable.setDefaultRenderer(Object.class, deviceCellEditor);
        jTable.setDefaultEditor(Object.class, deviceCellEditor);
        JScrollPane scrollPane1 = ScrollPaneFactory.createScrollPane(jTable);
//        setContent(scrollPane1);
        updateTree();
    }

    private void updateTree() {
        List<DefaultMutableTreeNode> activityStack = ActivityStackCommand.getActivityDumps();
        if (activityStack.size() == 0) {
            if (noDevicesJPanel == null) {
                noDevicesJPanel = new JPanel(new BorderLayout());
                noDevicesJPanel.add(new JLabel("no find devices", JLabel.CENTER), BorderLayout.CENTER);
            }
            if (getContent() == null) {
                setContent(noDevicesJPanel);
            } else {
                if (getContent() != noDevicesJPanel) {
                    setContent(noDevicesJPanel);
                }
            }
            return;
        }
        if (allTree == null) {
            allTree = new DefaultMutableTreeNode("top");
        }
        allTree.removeAllChildren();
        for (DefaultMutableTreeNode node : activityStack) {
            allTree.add(node);
        }
        switchTreeView();
    }

    private void showFilter() throws IOException, ClassNotFoundException {
        if (filterTree == null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(allTree);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            filterTree = (DefaultMutableTreeNode) ois.readObject();
            Enumeration enumeration = filterTree.preorderEnumeration();
            while (enumeration.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                for (String filterProperty : FilterNode.ALL) {
                    if (node.toString().startsWith(filterProperty)) {
                        filterNode.add(node);
                    }
                }
            }
            for (DefaultMutableTreeNode node : filterNode) {
                node.removeFromParent();
            }
        }
        Tree tree = new Tree(filterTree);
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }

    private void showAll() {
        Tree tree = new Tree(allTree);
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }

    private JPanel createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ActivityRefreshAction());
        group.add(new ShowAllTargetsAction());
        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("fwegwef", group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    @Override
    public void findDevices(List<Device> devices) {
//        System.out.println(Thread.currentThread());
    }

    @Override
    public void noDevices() {

    }

    private class ActivityRefreshAction extends AnAction {
        ActivityRefreshAction() {
            super("refresh activity", "refresh activity stack", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            filterTree = null;
            updateTree();
        }
    }

    private class ShowAllTargetsAction extends ToggleAction {
        ShowAllTargetsAction() {
            super("filter activity", "filter activity view", AllIcons.General.Filter);
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent event) {
            return filter;
        }

        @Override
        public void setSelected(@NotNull AnActionEvent event, boolean flag) {
            System.out.println("setSelected..." + flag);
            filter = flag;
            switchTreeView();
        }
    }

    private void switchTreeView() {
        if (filter) {
            try {
                showFilter();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            showAll();
        }
    }
}
