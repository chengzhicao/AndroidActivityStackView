package com.cheng.plugins;

import com.android.ddmlib.IDevice;
import com.cheng.plugins.adb.ActivityStackCommand;
import com.cheng.plugins.adb.DeviceService;
import com.cheng.plugins.device.DeviceCellEditor;
import com.cheng.plugins.device.DevicesTableModel;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class StackPanel extends SimpleToolWindowPanel implements DeviceService.DevicesListener, DeviceCellEditor.WatchListener {
    private JPanel noDevicesPanel;
    private boolean filter;
    private final DefaultMutableTreeNode allTree = new DefaultMutableTreeNode();
    private DefaultMutableTreeNode filterTree;
    private final List<DefaultMutableTreeNode> filterNode = new ArrayList<>();
    private final DevicesTableModel devicesTableModel = new DevicesTableModel();
    private final JBTable jTable = new JBTable(devicesTableModel);
    private JScrollPane devicePanel;
    private boolean shouldShowDevice = true;
    private IDevice[] devices;
    private IDevice currentDevice;
    private final BackAction backAction = new BackAction();
    private final RefreshAction refreshAction = new RefreshAction();
    private final FilterAction filterAction = new FilterAction();

    StackPanel(@NotNull Project project) {
        super(true, true);
        setToolbar(createToolbarPanel());
        scanDevices(project);
        createDevicePanel();
        createNoDevicePanel();
    }

    private void scanDevices(Project project) {
        DeviceService deviceService = new DeviceService(project);
        deviceService.setDevicesListener(this);
        deviceService.startService();
    }

    private void createDevicePanel() {
        DeviceCellEditor deviceCellEditor = new DeviceCellEditor(this);
        jTable.getColumnModel().getColumn(0).setCellRenderer(deviceCellEditor);
        jTable.getColumnModel().getColumn(0).setCellEditor(deviceCellEditor);
        devicePanel = ScrollPaneFactory.createScrollPane(jTable);
    }

    private void createNoDevicePanel() {
        noDevicesPanel = new JPanel(new BorderLayout());
        noDevicesPanel.add(new JLabel("no find devices", JLabel.CENTER), BorderLayout.CENTER);
    }

    private void refreshTree() {
        List<DefaultMutableTreeNode> activityStack = ActivityStackCommand.getActivityDumps2(currentDevice);
        allTree.setUserObject(currentDevice.isEmulator() ? currentDevice.getAvdName() : currentDevice.getProperty(IDevice.PROP_DEVICE_MODEL));
        allTree.removeAllChildren();
        for (DefaultMutableTreeNode node : activityStack) {
            allTree.add(node);
        }
        switchTreeView();
    }

    private void showFilter() throws IOException, ClassNotFoundException {
        if (filterTree == null || !filterTree.getUserObject().equals(allTree.getUserObject())) {
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
//        tree.setCellRenderer((tree1, value, selected, expanded, leaf, row, hasFocus) -> new JLabel(value.toString()));
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }

    private JPanel createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(backAction);
        group.add(refreshAction);
        group.add(filterAction);
        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("fwegwef", group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }

    @Override
    public void findDevices(IDevice[] devices) {
        this.devices = devices;
        devicesTableModel.updateDevice(devices);
        if (devices.length > 0) {
            if (shouldShowDevice) {
                if (devicePanel != getContent()) {
                    setContent(devicePanel);
                }
            } else {
                boolean haveDevice = false;
                for (IDevice device : devices) {
                    if (device.getSerialNumber().equals(currentDevice.getSerialNumber())) {
                        haveDevice = true;
                        break;
                    }
                }
                if (!haveDevice) {
                    shouldShowDevice = true;
                    setContent(devicePanel);
                }
            }
        } else {
            if (noDevicesPanel != getContent()) {
                setContent(noDevicesPanel);
            }
        }
    }

    @Override
    public void watch() {
        shouldShowDevice = false;
        currentDevice = devices[jTable.getEditingRow()];
        if (currentDevice.isOnline()) {
            refreshTree();
        }
    }

    private class BackAction extends AnAction {
        BackAction() {
            super("back", "back", AllIcons.Actions.Back);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            setContent(devicePanel);
            shouldShowDevice = true;
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(!shouldShowDevice);
        }
    }

    private class RefreshAction extends AnAction {
        RefreshAction() {
            super("refresh activity", "refresh activity stack", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            filterTree = null;
            refreshTree();
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(!shouldShowDevice);
        }
    }

    private class FilterAction extends ToggleAction {
        FilterAction() {
            super("filter activity", "filter activity view", AllIcons.General.Filter);
        }

        @Override
        public boolean isSelected(@NotNull AnActionEvent event) {
            return filter;
        }

        @Override
        public void setSelected(@NotNull AnActionEvent event, boolean flag) {
            filter = flag;
            switchTreeView();
        }

        @Override
        public void update(@NotNull AnActionEvent e) {
            e.getPresentation().setEnabled(!shouldShowDevice);
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
