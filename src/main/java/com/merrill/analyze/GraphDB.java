package com.merrill.analyze;

import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

/**
 * @Author: 梅峰鑫
 * @Date: 2020/12/22 13:55
 * @Description TODO
 */
public class GraphDB implements AutoCloseable {
    private final Driver driver;
    private Graph graph;

    public GraphDB(String uri, String user, String password) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
        graph = Analyze.getGraph();
    }

    @Override
    public void close() throws Exception {
        driver.close();
    }

    public void createGraph() {
        try (Session session = driver.session()) {
            session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
                    List<Node> nodes = graph.getNodes();
                    for (int i = 0; i < nodes.size(); i++) {
                        Node node = nodes.get(i);
                        tx.run("CREATE(n:TypeNode{classSignature:$classSignature})",
                                parameters("classSignature", node.getClassSignature()));
                    }
                    for (int i = 0; i < nodes.size(); i++) {
                        Node node = nodes.get(i);
                        Map<Node, Map<String, Integer>> accessNodes = node.getAccessNodes();
                        for (Node accessNode : accessNodes.keySet()) {
//                            tx.run("MATCH (n1:TypeNode),(n2:TypeNode) " +
//                                            "WHERE n1.classSignature = $s1 AND n2.classSignature = $s2 " +
//                                            "CREATE(n1)-[r:ACCESS]->(n2)",
//                                    parameters("s1", node.getClassSignature(),
//                                            "s2", accessNode.getClassSignature()));

                            Map<String, Integer> map = accessNodes.get(accessNode);
                            List<String> attributes = new ArrayList<>();
                            for (String s : map.keySet()) {
                                attributes.add(s + ":" + map.get(s));
                                Result result = tx.run("MATCH (m:MethodType) WHERE m.methodSignature = $s1 return m",
                                        parameters("s1", s));
//                                Result result = tx.run("MATCH (m:TypeNode) WHERE m.classSignature = $s1 return m.classSignature as classSignature",
//                                        parameters("s1", "Lcom/merrill/code/common/A;"));
                                if (!result.hasNext()) {
                                    tx.run("CREATE(m:MethodType{methodSignature:$methodSignature})",
                                            parameters("methodSignature", s));
                                    tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                    "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                    "CREATE(n)-[r:ACCESS]->(m)",
                                            parameters("s1", node.getClassSignature(),
                                                    "s2", s));
                                    tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                    "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                    "CREATE(m)-[r:ACCESS]->(n)",
                                            parameters("s1", accessNode.getClassSignature(),
                                                    "s2", s));
                                } else {
                                    Result res = tx.run("match (x)-[r:ACCESS]-(y) " +
                                                    "where x.classSignature = $s1 and y.methodSignature = $s2 " +
                                                    "return r",
                                            parameters("s1", node.getClassSignature(),
                                                    "s2", s));
                                    if (!res.hasNext()) {
                                        tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                        "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                        "CREATE(n)-[r:ACCESS]->(m)",
                                                parameters("s1", node.getClassSignature(),
                                                        "s2", s));
                                        tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                        "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                        "CREATE(m)-[r:ACCESS]->(n)",
                                                parameters("s1", accessNode.getClassSignature(),
                                                        "s2", s));
                                    }
                                }
                            }


//                            //函数不能以数字开头，且Signature中不可能存在数字，所以用0作为分割
//                            String attr = attributes.stream()
//                                    .map(s -> s.replace("(", "")
//                                            .replace(")", "0")
//                                            .replace("-", "0"))
//                                    .collect(Collectors.joining(","));

//                            String nodeString = node.getClassSignature();
//                            String accessNodeString = accessNode.getClassSignature();
//                            tx.run("MATCH (n1:Node),(n2:Node) " +
//                                            "WHERE n1.classSignature = $s1 AND n2.classSignature = $s2 " +
//                                            "CREATE(n1)-[r:ACCESS]->(n2) ",
//                                    parameters("s1", nodeString.substring(nodeString.lastIndexOf("/") + 1),
//                                            "s2", accessNodeString.substring(accessNodeString.lastIndexOf("/") + 1)));

                        }
                        Map<Node, Map<String, Integer>> modificationNodes = node.getModificationNodes();
                        for (Node modificationNode : modificationNodes.keySet()) {
//                            tx.run("MATCH (n1:TypeNode),(n2:TypeNode) " +
//                                            "WHERE n1.classSignature = $s1 AND n2.classSignature = $s2 " +
//                                            "CREATE(n1)-[r:MODIFICATION]->(n2)",
//                                    parameters("s1", node.getClassSignature(),
//                                            "s2", modificationNode.getClassSignature()));

                            Map<String, Integer> map = modificationNodes.get(modificationNode);
                            List<String> attributes = new ArrayList<>();
                            for (String s : map.keySet()) {
                                attributes.add(s + ":" + map.get(s));
                                Result result = tx.run("MATCH (m:MethodType) WHERE m.methodSignature = $s1 return m",
                                        parameters("s1", s));
//                                Result result = tx.run("MATCH (m:TypeNode) WHERE m.classSignature = $s1 return m.classSignature as classSignature",
//                                        parameters("s1", "Lcom/merrill/code/common/A;"));
                                if (!result.hasNext()) {
                                    tx.run("CREATE(m:MethodType{methodSignature:$methodSignature})",
                                            parameters("methodSignature", s));
                                    tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                    "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                    "CREATE(n)-[r:MODIFICATION]->(m)",
                                            parameters("s1", node.getClassSignature(),
                                                    "s2", s));
                                    tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                    "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                    "CREATE(m)-[r:MODIFICATION]->(n)",
                                            parameters("s1", modificationNode.getClassSignature(),
                                                    "s2", s));
                                } else {
                                    Result res = tx.run("match (x)-[r:ACCESS]-(y) " +
                                                    "where x.classSignature = $s1 and y.methodSignature = $s2 " +
                                                    "return r",
                                            parameters("s1", node.getClassSignature(),
                                                    "s2", s));
                                    if (!res.hasNext()) {
                                        tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                        "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                        "CREATE(n)-[r:MODIFICATION]->(m)",
                                                parameters("s1", node.getClassSignature(),
                                                        "s2", s));
                                        tx.run("MATCH (n:TypeNode),(m:MethodType) " +
                                                        "WHERE n.classSignature = $s1 AND m.methodSignature = $s2 " +
                                                        "CREATE(m)-[r:MODIFICATION]->(n)",
                                                parameters("s1", modificationNode.getClassSignature(),
                                                        "s2", s));
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }
            });
        }
    }

    private void clear() {
        try (Session session = driver.session()) {
            session.writeTransaction(new TransactionWork<String>() {
                @Override
                public String execute(Transaction tx) {
//                    tx.run("match(n:TypeNode)-[rel]-(nn:TypeNode) delete rel");
//                    tx.run("match(n:TypeNode) delete n");
                    tx.run("MATCH (n)\n" +
                            "OPTIONAL MATCH (n)-[r]-()\n" +
                            "DELETE n,r");
                    return null;
                }
            });
        }
    }

    public static void main(String... args) throws Exception {
        try (GraphDB graphDB = new GraphDB("bolt://localhost:7687", "neo4j", "980511")) {
            graphDB.clear();
            graphDB.createGraph();
        }
    }

}
