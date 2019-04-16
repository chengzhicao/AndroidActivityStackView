package com.cheng.activitystack;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;

public class AdbUtil {

    public static List<DefaultMutableTreeNode> activityDumps = new ArrayList<>();
    private static Map<Integer, DefaultMutableTreeNode> nodes = new HashMap<>();
    private static DefaultMutableTreeNode currentNode;
    private static int currentIndent;

    public static void runProcess() {
        activityDumps.clear();
        Process pro;
        try {
            pro = Runtime.getRuntime().exec("adb shell dumpsys activity activities");
            out(pro.getInputStream());
//        out(pro.getErrorStream());
            pro.waitFor();
            pro.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void out(InputStream in) throws Exception {
        String line;
        BufferedReader br = new BufferedReader(
                new InputStreamReader(in));
        String temp = null;
        br.readLine();
        while ((line = br.readLine()) != null) {
            if (!line.equals("")) {
                if (line.contains("Task id #")) {
                    temp = line.trim();
                    continue;
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
                addTree(spaceCount, line);
                System.out.println((spaceCount < 10 ? ("0" + spaceCount) : spaceCount) + ">" + line);
            }
        }
    }

    private static void addTree(int indent, String content) {
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
