package com.cheng.plugins.adb;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.ShellCommandUnresponsiveException;
import com.android.ddmlib.TimeoutException;
import org.jetbrains.android.util.AndroidOutputReceiver;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityStackCommand {
    private static List<DefaultMutableTreeNode> activityDumps = new ArrayList<>();
    private static Map<Integer, DefaultMutableTreeNode> nodes = new HashMap<>();
    private static DefaultMutableTreeNode currentNode;
    private static int currentIndent;
    private static String temp;

    public static List<DefaultMutableTreeNode> getActivityDumps2(IDevice device) {
        activityDumps.clear();
        try {
            device.executeShellCommand("dumpsys activity activities", new AndroidOutputReceiver() {
                @Override
                protected void processNewLine(@NotNull String line) {
                    if (!line.equals("")) {
                        if (line.contains("Task id #")) {
                            temp = line.trim();
                            return;
                        }
                        if (temp != null && line.contains("* TaskRecord")) {
                            line = line.replace("*", temp);
                            temp = null;
                        }
                        if (line.contains("* Hist #")) {
                            line = line.replace("* ", "");
                        }
                        int spaceCount = 0;
                        while ((spaceCount < line.length()) && (line.charAt(spaceCount) <= ' ')) {
                            spaceCount++;
                        }
                        createTree(spaceCount, line);
                        System.out.println((spaceCount < 10 ? ("0" + spaceCount) : spaceCount) + ">" + line);
                    }
                }

                @Override
                public boolean isCancelled() {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return activityDumps;
    }

    private static void createTree(int indent, String content) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(content.trim());
        if (indent == 0) {
            activityDumps.add(node);
        } else {
            if (indent > currentIndent) {
                currentNode.add(node);
            } else if (indent == currentIndent) {
                ((DefaultMutableTreeNode) currentNode.getParent()).add(node);
            } else {
                DefaultMutableTreeNode parent = getParentNode(indent);
                if (parent != null) {
                    parent.add(node);
                }
            }
        }
        currentNode = node;
        currentIndent = indent;
        nodes.put(indent, node);
    }

    private static DefaultMutableTreeNode getParentNode(int indent) {
        if (nodes.containsKey(indent)) {
            return (DefaultMutableTreeNode) nodes.get(indent).getParent();
        } else {
            while (--indent >= 0) {
                if (nodes.get(indent) != null)
                    return nodes.get(indent);
            }
        }
        return null;
    }
}
