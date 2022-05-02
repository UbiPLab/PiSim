package com.pisim.nsp.utils;

import java.util.*;

//无向加权图，采用邻接表进行存储
public class Graph {

    private List<Vertex> vertexList;   //图的顶点集
    private Map<Vertex, List<Edge>> vertexEdgesListMap;  //图的每个顶点对应的无向边
    public static int iterations = 20;   //过滤恶意节点时迭代计算的次数
    private static double cutoffThreshold = 0.02; //节点信任值的截止阈值

    public Graph(List<Vertex> vertexList, Map<Vertex, List<Edge>> VertexEdgesListMap) {
        super();
        this.vertexList = vertexList;
        this.vertexEdgesListMap = VertexEdgesListMap;
    }

    public List<Vertex> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<Vertex> vertexList) {
        this.vertexList = vertexList;
    }

    public Map<Vertex, List<Edge>> getVertexEdgesListMap() {
        return vertexEdgesListMap;
    }

    public void setVertexEdgesListMap(Map<Vertex, List<Edge>> VertexEdgesListMap) {
        this.vertexEdgesListMap = VertexEdgesListMap;
    }

    //获取图中所有的边
    public List<Edge> getAllEdges() {
        List<Edge> edgeList = new ArrayList<>();

        for (Map.Entry<Vertex, List<Edge>> map : vertexEdgesListMap.entrySet()) {
            Vertex vertex = map.getKey();
            List<Edge> vertexEdgeList = map.getValue();
            for (Edge edge: vertexEdgeList) {
                //这里的contains方法判断时使用了equals方法,比较逻辑为两条边的节点是否一样(id是否相同)
                if (!edgeList.contains(edge)) {
                    edgeList.add(edge);
                }
            }
        }

        return edgeList;
    }

    //求给定节点对应的邻接边集
    public List<Edge> getVertexEdges(Vertex vertex) {
        return vertexEdgesListMap.get(vertex);
    }

    //求给定节点所有邻接边的权重之和
    public int getWeightSumOfAdjacentEdges(Vertex v) {
        int sum = 0;    //邻接边的权重和
        for (Map.Entry<Vertex, List<Edge>> entry : vertexEdgesListMap.entrySet()) {
            Vertex vertex = entry.getKey();
            //如果找到给定节点
            if (vertex.equals(v)) {
                //求出对应的邻接边集
                List<Edge> edgeList = entry.getValue();
                for (Edge edge : edgeList) {
                    sum = sum + edge.getWeight();
                }
            }
        }
        return sum;
    }

    //删除节点
    public void removeVertex(Vertex v) {
        //从节点集中删除该节点
        vertexList.remove(v);
        //从节点对应的边集中删除对应的边
        Iterator<Map.Entry<Vertex, List<Edge>>> iterator = vertexEdgesListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vertex, List<Edge>> itEntry = iterator.next();
            //首先删除该节点对应的邻接表
            if (itEntry.getKey().equals(v)) {
                iterator.remove();
                continue;
            }
            //接着删除其他节点的邻接表中包含删除节点的边
            List<Edge> edgeList = itEntry.getValue();
            for (Edge e : edgeList) {
                //如果其他节点对应的邻接表中的边包含待删除节点，则将该边删除
                if (e.getOneVertex().equals(v) || e.getAnotherVertex().equals(v)) {
                    edgeList.remove(e);
                }
            }
            itEntry.setValue(edgeList);
        }
    }

    //判断图中是否有对应的边
    public boolean hasEdge(Edge edge) {
        List<Edge> allEdgesList = getAllEdges();
        for (Edge e: allEdgesList) {
            if(edge.equals(e)) {
                return true;
            }
        }
        return false;
    }

    //将初始的信任种子均分给指定的信任节点
    public void setVertexTrustValue(double trustSeed, List<Vertex> verList) {
        int number = verList.size();    //可信节点的数量
        //将初始的信任种子均分给所有的可信节点作为其信任值
        for (Graph.Vertex vertex : verList) {
            vertex.setTrustValue(trustSeed / number);
        }
        //遍历图的节点集，如果有节点与给定的节点具有相同的id（重写的equals方法判断两个节点相等的逻辑），则用给定的节点替换对应的节点集中的节点
        //起到为图中指定的多个节点设置信任值的作用
        for (Vertex v : verList) {
            if (vertexList.contains(v)) {
                int index = vertexList.indexOf(v);
                vertexList.set(index, v);
            }
        }
    }

    //当节点集中的节点信息更新后，需更新节点与边映射的关系的相关信息，在setVertexTrustValue方法使用后调用此方法更新节点-边映射关系
    public void updateVertexEdgesListMap() {
        Map<Vertex, List<Edge>> newVertexEdgesListMap = new HashMap<>();    //由于Map在遍历时无法直接修改key值，因此创建一个新的Map
        Iterator<Map.Entry<Vertex, List<Edge>>> iterator = vertexEdgesListMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Vertex, List<Edge>> itEntry = iterator.next();
            //取出key值，也即节点
            Vertex vertex = itEntry.getKey();
            //取出value值，也即节点对应的邻接边的集合
            List<Edge> edgeList = itEntry.getValue();
            //找出节点在节点集中的索引，并根据索引找出对应的节点集中的节点（已更新了信息）
            int index = vertexList.indexOf(vertex); //indexOf判断时依赖equals方法
            Vertex newVertex = vertexList.get(index);   //更新后的节点
            List<Edge> newEdgeList = new ArrayList<>(); //更新后的节点的邻接边
            for (Edge edge : edgeList) {
                Vertex oldOneVertex = edge.oneVertex;   //待更新的边的一个节点
                int indexOfOldOneVertex = vertexList.indexOf(oldOneVertex); //该节点在节点列表中的索引
                Vertex oldAnotherVertex = edge.anotherVertex;   //待更新的边的另一个节点
                int indexOfOldAnotherVertex = vertexList.indexOf(oldAnotherVertex); //该节点在节点列表中的索引
                int weight = edge.weight;   //该边对应的权重
                Vertex newOneVertex = vertexList.get(indexOfOldOneVertex);  //更新后的边的一个节点
                Vertex newAnotherVertex = vertexList.get(indexOfOldAnotherVertex);  //更新后的边的另一个节点
                Edge newEdge = new Edge(newOneVertex, newAnotherVertex, weight);    //根据更新后的节点构造新边
                newEdgeList.add(newEdge);
            }
            newVertexEdgesListMap.put(newVertex, newEdgeList);
        }
        setVertexEdgesListMap(newVertexEdgesListMap);
    }

    //过滤掉恶意节点迭代计算中的一轮
    public void filterMaliciousVertexOneRound() {
        //保存i-1轮所有节点的信任值
        Map<Vertex, Double> lastIterationTrustValue = new HashMap<Vertex, Double>();
        for (Vertex vertex : vertexList) {
            lastIterationTrustValue.put(vertex, vertex.getTrustValue());
        }

        for (Map.Entry<Vertex, List<Edge>> entry : vertexEdgesListMap.entrySet()) {
            Vertex v = entry.getKey();
            List<Edge> vEdgeList = entry.getValue();   //节点v的所有邻接边

            //对于节点v的邻接边集中的任一条邻接边edge
            double tempResult = 0.0;    //累加的中间结果
            for (Edge edge : vEdgeList) {
                //首先找出edge中与节点v互异的那个节点
                Vertex anotherVertex = edge.getOneVertex();
                if (anotherVertex.equals(v)) {
                    anotherVertex = edge.getAnotherVertex();
                }
                double anotherVertexTrustValue = lastIterationTrustValue.get(anotherVertex);    //该节点上一轮迭代计算的信任值
                int weightSumOfAnotherVertexAdjacentEdges = getWeightSumOfAdjacentEdges(anotherVertex); //该节点所有邻接边的权重和
                tempResult = tempResult + (anotherVertexTrustValue * edge.getWeight()/weightSumOfAnotherVertexAdjacentEdges);   //累加的中间项
            }
            v.setTrustValue(tempResult);    //更新节点本轮迭代计算后的信任值
        }
    }

    //过滤掉恶意节点(信任值trustValue低于设定阈值)
    public void filterMaliciousVertex() {
        for (int i = 0; i < iterations; i++) {
            filterMaliciousVertexOneRound();
            //用于测试,便于观察输出，具体使用时可以考虑注释掉此部分
            //System.out.println("第" + (i+1) + "轮迭代计算结果如下：");
            //printVertexTrustValue();
        }
        //遍历图的节点集，过滤信任值低于设定阈值的恶意节点
        for (Vertex v : vertexList) {
            if (v.getTrustValue() < cutoffThreshold) {
                removeVertex(v);
            }
        }
    }

    public void printVertexTrustValue() {
        for (Vertex v : vertexList) {
            System.out.println(v.getId() + " trustValue=" + v.getTrustValue());
        }
    }

    //重写toString方法，便于测试观察输出
    //@Override
    //public String toString() {
    //    String resultString = "";
    //    for (Map.Entry<Vertex, List<Edge>> entry : vertexEdgesListMap.entrySet()) {
    //        Vertex v = entry.getKey();
    //        resultString = resultString + v.name + "(trustValue:" + v.trustValue + ")" + ": ";
    //        for (Edge e : entry.getValue()) {
    //            resultString = resultString + "(" + e.getOneVertex().name + "," + e.getAnotherVertex().name + "," + e.getWeight() + "), ";
    //        }
    //        resultString = resultString + "\n";
    //    }
    //    return resultString;
    //}

    @Override
    public String toString() {
        String resultString = "";
        for (Map.Entry<Vertex, List<Edge>> entry : vertexEdgesListMap.entrySet()) {
            Vertex v = entry.getKey();
            resultString = resultString + v.id + "(trustValue:" + v.trustValue + ")" + ": ";
            for (Edge e : entry.getValue()) {
                resultString = resultString + "(" + e.getOneVertex().id + "," + e.getAnotherVertex().id + "," + e.getWeight() + "), ";
            }
            resultString = resultString + "\n";
        }
        return resultString;
    }

    public static class Edge {
        private Vertex oneVertex;  //此无向边的一个顶点
        private Vertex anotherVertex;  //此无向边的另一个顶点
        private int weight;  //此无向边的权值

        public Edge(Vertex oneVertex, Vertex anotherVertex, int weight) {
            super();
            this.oneVertex = oneVertex;
            this.anotherVertex = anotherVertex;
            this.weight = weight;
        }

        public Edge(Vertex oneVertex, Vertex anotherVertex)
        {
            this.oneVertex = oneVertex;
            this.anotherVertex = anotherVertex;
            this.weight = 0;
        }

        public Vertex getOneVertex() {
            return oneVertex;
        }

        public void setOneVertex(Vertex oneVertex) {
            this.oneVertex = oneVertex;
        }

        public Vertex getAnotherVertex() {
            return anotherVertex;
        }

        public void setAnotherVertex(Vertex anotherVertex) {
            this.anotherVertex = anotherVertex;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        //当两条边对应的节点相同时认为是同一条边
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Edge)) {
                throw new ClassCastException("an object to compare with a Edge must be Edge");
            }
            if (this.oneVertex==null || this.anotherVertex==null) {
                throw new NullPointerException("vertex of Edge to be compared cannot be null");
            }

            return (this.oneVertex.equals(((Edge) obj).oneVertex) && this.anotherVertex.equals(((Edge) obj).anotherVertex))
                    || (this.oneVertex.equals(((Edge) obj).anotherVertex) && this.anotherVertex.equals(((Edge) obj).oneVertex));
        }

        //重写toString方法便于观察输出
        //@Override
        //public String toString() {
        //    return "(" + this.oneVertex.name + ", " + this.anotherVertex.name + ", " + this.weight + ")";
        //}

        @Override
        public String toString() {
            return "(" + this.oneVertex.id + ", " + this.anotherVertex.id + ", " + this.weight + ")";
        }
    }

    public static class Vertex {
        //private double cutoffThreshold; //节点信任值的截止阈值

        private String name;  //节点名字
        private double trustValue;  //节点的信任值
        private String id;  //节点的id

        public Vertex(String name, String id)
        {
            this.name = name;
            this.id = id;
            this.trustValue = 0;
        }

        //public Vertex(String name, double trustValue)
        //{
        //    this.name = name;
        //    this.trustValue = trustValue;
        //}

        public Vertex(String id) {
            this.id = id;
            this.trustValue = 0;
        }

        public Vertex(String id, double trustValue) {
            this.id = id;
            this.trustValue = trustValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public double getTrustValue() {
            return trustValue;
        }

        public void setTrustValue(double trustValue) {
            this.trustValue = trustValue;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        /**
         * 重写Object父类的equals方法
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Vertex)) {
                throw new ClassCastException("an object to compare with a Vertext must be Vertex");
            }

            if (this.id == null) {
                throw new NullPointerException("id of Vertex to be compared cannot be null");
            }

            return this.id.equals(((Vertex) obj).id);
        }

        ///**
        // * 重写Object父类的equals方法
        // */
        //@Override
        //public boolean equals(Object obj) {
        //    if (!(obj instanceof Vertex)) {
        //        throw new ClassCastException("an object to compare with a Vertext must be Vertex");
        //    }
        //
        //    if (this.id==null) {
        //        throw new NullPointerException("id of Vertex to be compared cannot be null");
        //    }
        //
        //    return Arrays.equals(this.id, ((Vertex) obj).id);
        //}
    }

}