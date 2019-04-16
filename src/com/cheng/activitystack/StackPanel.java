package com.cheng.activitystack;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
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

public class StackPanel extends SimpleToolWindowPanel {
    private JPanel noDevicesJPanel;
    private boolean filter;
    private DefaultMutableTreeNode top;
    private DefaultMutableTreeNode filterTop;
    private List<DefaultMutableTreeNode> filterNode = new ArrayList<>();

    public StackPanel(@NotNull Project project) {
        super(true, true);
        setToolbar(createToolbarPanel());
        updateTree();
    }

    private void updateTree() {
        AdbUtil.runProcess();
        if (AdbUtil.activityDumps.size() == 0) {
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
        if (top == null) {
            top = new DefaultMutableTreeNode("top");
        }
        top.removeAllChildren();
        for (DefaultMutableTreeNode node : AdbUtil.activityDumps) {
            top.add(node);
        }
        switchTreeView();
    }

    private void showFilter() throws IOException, ClassNotFoundException {
        if (filterTop == null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(top);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            filterTop = (DefaultMutableTreeNode) ois.readObject();
            Enumeration enumeration = filterTop.preorderEnumeration();
            while (enumeration.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
                System.out.println(node.toString());
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
        Tree tree = new Tree(filterTop);
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }

    private void showAll() {
        Tree tree = new Tree(top);
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }

    private JPanel createToolbarPanel() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ActivityRefreshAction());
        group.add(new ShowAllTargetsAction());
        ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("fwegwef", group, true);
        return JBUI.Panels.simplePanel(actionToolBar.getComponent());
    }


    private class ActivityRefreshAction extends AnAction {
        ActivityRefreshAction() {
            super("refresh activity", "refresh activity stack", AllIcons.Actions.Refresh);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            filterTop = null;
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
