package com.merrill.analyze;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/10/22 16:48
 * @Description:
 */
public class Analyze {

    public static void main(String[] args) {
        analyze("D:/code_dependency_capturer_new/analyze/db/");
    }

    private static void analyze(String dbPath) {
        Graph graph = handleData(dbPath);
        System.out.println(graph);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String s1 = scanner.nextLine();
            String s2 = scanner.nextLine();
            graph.findRelation(s1, s2)
                    .forEach((key, value) -> System.out.println(key + ": " + value.stream()
                            .map(node -> node.getClassSignature())
                            .collect(Collectors.toList())));
        }
    }

    public static Graph getGraph() {
        return handleData("D:/code_dependency_capturer_new/analyze/db/");
    }

    private static Graph handleData(String path) {
        Graph graph = new Graph();

        //fieldAccess
        String url = "jdbc:sqlite:" + path + "fieldAccess.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM " + "fieldAccess";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String calleeClassSignature = rs.getString("cSignature");
                String calleeFieldSignature = rs.getString("fSignature");
                String calleeFieldHashCode = rs.getString("fHashcode");
                String methodChain = rs.getString("methodChain");

                String beenAccessDataNode = calleeFieldSignature;
                if ("primitive".equals(calleeFieldHashCode)) {
                    beenAccessDataNode = calleeClassSignature;
                }
                graph.addChain(beenAccessDataNode, methodChain, "access");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //fieldModification
        url = "jdbc:sqlite:" + path + "fieldModification.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM " + "fieldModification";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String calleeClassSignature = rs.getString("cSignature");
                String calleeFieldSignature = rs.getString("fSignature");
                String calleeFieldHashCode = rs.getString("fHashcode");
                String methodChain = rs.getString("methodChain");

                String beenAccessDataNode = calleeFieldSignature;
                if ("primitive".equals(calleeFieldHashCode)) {
                    beenAccessDataNode = calleeClassSignature;
                }
                graph.addChain(beenAccessDataNode, methodChain, "modification");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return graph;
    }
}

class Node {
    private String classSignature;

    private Map<Node, Map<String, Integer>> accessNodes;
    private Map<Node, Map<String, Integer>> modificationNodes;
    private Map<Node, Map<String, Integer>> beenAccessNode;
    private Map<Node, Map<String, Integer>> beenModificationNode;

    public Map<Node, Map<String, Integer>> getAccessNodes() {
        return accessNodes;
    }

    public Map<Node, Map<String, Integer>> getModificationNodes() {
        return modificationNodes;
    }

    public Node(String classSignature) {
        this.classSignature = classSignature;
        accessNodes = new HashMap<>();
        modificationNodes = new HashMap<>();
        beenAccessNode = new HashMap<>();
        beenModificationNode = new HashMap<>();
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("classSignature=" + classSignature);
        stringBuffer.append("\naccessNodes:\n");
        for (Node node : accessNodes.keySet()) {
            stringBuffer.append("\t" + node.getClassSignature());
//            Map tmp = accessNodes.get(node);
        }
        stringBuffer.append("\nmodificationNodes:\n");
        for (Node node : modificationNodes.keySet()) {
            stringBuffer.append("\t" + node.getClassSignature());
        }
        stringBuffer.append("\nbeenAccessNode:\n");
        for (Node node : beenAccessNode.keySet()) {
            stringBuffer.append("\t" + node.getClassSignature());
        }
        stringBuffer.append("\nbeenModificationNode:\n");
        for (Node node : beenModificationNode.keySet()) {
            stringBuffer.append("\t" + node.getClassSignature());
        }
        return stringBuffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return new EqualsBuilder()
                .append(classSignature, node.classSignature)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(classSignature)
                .toHashCode();
    }

    public String getClassSignature() {
        return classSignature;
    }

    private void addNode(Map map, Node calleeNode, String methodSignature, String methodName) {
        if (map == null) {
            map = new HashMap<>();
        }
        String method = methodSignature + "-" + methodName;
        if (map.containsKey(calleeNode)) {
            Map<String, Integer> methodsCountMap = (Map<String, Integer>) map.get(calleeNode);
            methodsCountMap.put(method, methodsCountMap.containsKey(method) ? methodsCountMap.get(method) + 1 : 1);
        } else {
            Map methodsCountMap = new HashMap();
            methodsCountMap.put(method, 1);
            map.put(calleeNode, methodsCountMap);
        }
    }

    public void addAccessNode(Node calleeNode, String methodSignature, String methodName) {
        addNode(accessNodes, calleeNode, methodSignature, methodName);
    }

    public void addBeenAccessNode(Node callerNode, String methodSignature, String methodName) {
        addNode(beenAccessNode, callerNode, methodSignature, methodName);
    }

    public void addModificationNode(Node calleeNode, String methodSignature, String methodName) {
        addNode(modificationNodes, calleeNode, methodSignature, methodName);
    }

    public void addBeenModificationNode(Node callerNode, String methodSignature, String methodName) {
        addNode(beenModificationNode, callerNode, methodSignature, methodName);
    }

    public List<Node> getCommonAccess(Node node) {
        List list = new LinkedList();
        accessNodes.keySet().stream().filter(n -> node.isAccessNodesContains(n)).forEach(n -> list.add(n));
        return list;
    }

    public boolean isAccessNodesContains(Node node) {
        return accessNodes.containsKey(node);
    }

    public List<Node> getCommonModification(Node node) {
        List list = new LinkedList();
        modificationNodes.keySet().stream().filter(n -> node.isModificationNodesContains(n)).forEach(n -> list.add(n));
        return list;
    }

    public boolean isModificationNodesContains(Node node) {
        return modificationNodes.containsKey(node);
    }

    public List<Node> getCommonBeenAccess(Node node) {
        List list = new LinkedList();
        beenAccessNode.keySet().stream().filter(n -> node.isBeenAccessNodesContains(n)).forEach(n -> list.add(n));
        return list;
    }

    public boolean isBeenAccessNodesContains(Node node) {
        return beenAccessNode.containsKey(node);
    }

    public List<Node> getCommonBeenModification(Node node) {
        List list = new LinkedList();
        beenModificationNode.keySet().stream().filter(n -> node.isBeenModificationNodesContains(n)).forEach(n -> list.add(n));
        return list;
    }

    public boolean isBeenModificationNodesContains(Node node) {
        return beenModificationNode.containsKey(node);
    }
}

class Graph {
    private List<Node> nodes;

    public List<Node> getNodes() {
        return nodes;
    }

    public Graph() {
        nodes = new LinkedList<>();
    }

    @Override
    public String toString() {
        String res = "";
        for (Node node : nodes) {
            res += node;
            res += "\n\n";
        }
        return res;
    }

    private String[][] handleChain(String methodChain) {
        String[] methods = methodChain.split("->");
        String[][] info = new String[methods.length][];
        for (int i = 0; i < info.length; i++) {
            info[i] = methods[i].split("-");
        }
        return info;
    }

    private Node getCalleeNode(String beenHandleDataNode) {
        List<Node> list = nodes.stream().filter(node -> node.getClassSignature().equals(beenHandleDataNode)).collect(Collectors.toList());
        Node calleeNode;
        if (list.size() == 0) {
            calleeNode = new Node(beenHandleDataNode);
            nodes.add(calleeNode);
        } else {
            calleeNode = list.get(0);
        }
        return calleeNode;
    }

    public void addChain(String beenHandleDataNode, String methodChain, String cmd) {
        String[][] info = handleChain(methodChain);

        Node calleeNode = getCalleeNode(beenHandleDataNode);

        for (int i = info.length - 1; i >= 0; i--) {
            String[] infoArray = info[i];
            List<Node> list = nodes.stream().filter(node -> node.getClassSignature().equals(infoArray[0])).collect(Collectors.toList());
            Node node;
            if (list.size() == 0) {
                node = new Node(infoArray[0]);
                nodes.add(node);
            } else {
                node = list.get(0);
            }
            if ("access".equals(cmd)) {
                node.addAccessNode(calleeNode, infoArray[1], infoArray[2]);
                calleeNode.addBeenAccessNode(node, infoArray[1], infoArray[2]);
            } else {
                node.addModificationNode(calleeNode, infoArray[1], infoArray[2]);
                calleeNode.addBeenModificationNode(node, infoArray[1], infoArray[2]);
            }
            if (infoArray[1].endsWith(")V") && !"<init>".equals(infoArray[2])) {
                return;
            }
        }
    }

    public void addChainWithParamAndReturn(String beenHandleDataNode, String methodChain, String cmd) {
        String[][] info = handleChain(methodChain);

        Node calleeNode = getCalleeNode(beenHandleDataNode);
        String calleeNodeClassSignature = calleeNode.getClassSignature();

        //处理info[][1]中的参数和返回值
        for (int i = info.length - 1; i >= 0; i--) {
            String[] infoArray = info[i];
            List<Node> list = nodes.stream().filter(node -> node.getClassSignature().equals(infoArray[0])).collect(Collectors.toList());

            Node node;
            if (list.size() == 0) {
                node = new Node(infoArray[0]);
                nodes.add(node);
            } else {
                node = list.get(0);
            }

            if ("access".equals(cmd)) {
                //access中作为返回值返回视作数据依赖
                if (infoArray[1].contains(calleeNodeClassSignature)) {
                    node.addAccessNode(calleeNode, infoArray[1], infoArray[2]);
                    calleeNode.addBeenAccessNode(node, infoArray[1], infoArray[2]);
                } else {
                    return;
                }
            } else {
                //modification中作为参数传递视作数据依赖
                if (infoArray[1].endsWith(calleeNodeClassSignature)) {
                    node.addModificationNode(calleeNode, infoArray[1], infoArray[2]);
                    calleeNode.addBeenModificationNode(node, infoArray[1], infoArray[2]);
                } else {
                    return;
                }
            }

            if (infoArray[1].endsWith(")V") && !"<init>".equals(infoArray[2])) {
                return;
            }
        }
    }

    public List<String> getNodeSignatures() {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            res.add(nodes.get(i).getClassSignature());
        }
        return res;
    }

    public Map<String, List<Node>> findRelation(String classSignature1, String classSignature2) {
        Node node1 = findNodeByClassSignature(classSignature1);
        Node node2 = findNodeByClassSignature(classSignature2);
        if (node1 == null || node2 == null) {
            return null;
        }
        Map res = new HashMap();
        res.put("commonAccess", node1.getCommonAccess(node2));
        res.put("commonModification", node1.getCommonModification(node2));
        res.put("commonBeenAccess", node1.getCommonBeenAccess(node2));
        res.put("commonBeenModification", node1.getCommonBeenModification(node2));
        return res;
    }

    public Node findNodeByClassSignature(String classSignature) {
        Optional<Node> nodeOptional = nodes.stream().filter(node -> node.getClassSignature().equals(classSignature)).findFirst();
        return nodeOptional.isPresent() ? nodeOptional.get() : null;
    }
}
