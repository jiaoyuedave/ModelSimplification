package com.example.modelsimplification.objects;

import android.opengl.Matrix;
import android.util.Log;

import com.example.modelsimplification.BuildConfig;
import com.example.modelsimplification.data.IndexMinPQ;
import com.example.modelsimplification.data.Vector;
import com.example.modelsimplification.util.LoggerConfig;
import com.example.modelsimplification.util.MatrixHelper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/4/14.
 */

public class ObjectModel {

    private static final String TAG = "ObjectModel";

    private final List<Vertex> vertexList = new ArrayList<>();          // 顶点列表
    private final List<Face> faceList = new ArrayList<>();              // 三角面列表
    private IndexMinPQ<Float> costHeap;                               // 折叠代价的优先队列
    private int vN;                                                   // 模型中顶点的数目
    private int fN;                                                   // 模型中三角面的数目

    private String basePath;                                      // 基准路径

    public ObjectModel(String fileName) {
        try {
            loadFromObjFile(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ObjectModel(Reader reader) {
        try {
            load(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从.obj 文件加载三维模型，适用于PC端
     * @param fileName 文件路径
     * @return this
     * @throws FileNotFoundException
     */
    public ObjectModel loadFromObjFile(String fileName) throws FileNotFoundException {
        setBasePathFromFilename(fileName);

        Reader reader = new BufferedReader(new FileReader(fileName));
        return load(reader);
    }

    /**
     * 从输入流读取三维模型，android端使用此方法
     * @param reader 输入流
     * @return this
     * @throws FileNotFoundException
     */
    public ObjectModel load(Reader reader) throws FileNotFoundException {
        // ObjectFileParser does lexical analysis
        ObjectFileParser st = new ObjectFileParser(reader);

        readFile(st);

        // 读完所有的面后才能计算顶点的平均法向量
        for (Vertex v : vertexList) {
            v.computeNormal();
        }
        vN = vertexList.size();
        fN = faceList.size();
        return this;
    }

    public void simplifiedTo(int vertexNum) {
        costHeap = new IndexMinPQ<>(vertexList.size());
        computeAllCost();

        while (vN > vertexNum) {
            if (BuildConfig.DEBUG && LoggerConfig.ANDROID_DEBUG) {
                Log.d(TAG, "vertex index:" + costHeap.minIndex() + "\t" + "cost:" + costHeap.min());
            }
            int vIndex = costHeap.delMin();
            collapse(vIndex);
        }
    }

    public GLObject toGLObject() {
        float[] vertexArray = new float[fN * 9];          // 顶点数组
        float[] normalArray = new float[fN * 9];          // 法向量数组
        for (int i = 0, j = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            if (face == null) {
                continue;
            }
            Vertex vertex1 = vertexList.get(face.verticesIndex[0]);
            vertexArray[j] = vertex1.position.x;
            vertexArray[j + 1] = vertex1.position.y;
            vertexArray[j + 2] = vertex1.position.z;
            Vertex vertex2 = vertexList.get(face.verticesIndex[1]);
            vertexArray[j + 3] = vertex2.position.x;
            vertexArray[j + 4] = vertex2.position.y;
            vertexArray[j + 5] = vertex2.position.z;
            Vertex vertex3 = vertexList.get(face.verticesIndex[2]);
            vertexArray[j + 6] = vertex3.position.x;
            vertexArray[j + 7] = vertex3.position.y;
            vertexArray[j + 8] = vertex3.position.z;

            // 这里使用面向量法计算顶点的法向量
            normalArray[j] = face.normal.x;
            normalArray[j + 1] = face.normal.y;
            normalArray[j + 2] = face.normal.z;
            normalArray[j + 3] = face.normal.x;
            normalArray[j + 4] = face.normal.y;
            normalArray[j + 5] = face.normal.z;
            normalArray[j + 6] = face.normal.x;
            normalArray[j + 7] = face.normal.y;
            normalArray[j + 8] = face.normal.z;

            j = j + 9;
        }
        return new GLObject(vertexArray, normalArray);
    }

    /**
     * 生成使用索引数组绘制的OpenGL ES 对象，用于绘制OpenGL 图形
     * @return IndexedGLObject 对象
     */
    public IndexedGLObject toIndexedGLObject() {
        int[] tempArray = new int[vertexList.size()];         // 用来记录顶点索引位置的数组
        float[] vertexArray = new float[vN * 3];
        float[] normalArray = new float[vN * 3];
        for (int i = 0, j = 0; i < vertexList.size(); i++) {
            Vertex vertex = vertexList.get(i);
            if (vertex == null) {
                tempArray[i] = -1;
                continue;
            }
            tempArray[i] = j;

            vertexArray[j * 3] = vertex.position.x;
            vertexArray[j * 3 + 1] = vertex.position.y;
            vertexArray[j * 3 + 2] = vertex.position.z;

            normalArray[j * 3] = vertex.normal.x;
            normalArray[j * 3 + 1] = vertex.normal.y;
            normalArray[j * 3 + 2] = vertex.normal.z;

            j++;
        }

        int[] indexArray = new int[fN * 3];
        for (int i = 0, j = 0; i < faceList.size(); i++) {
            Face face = faceList.get(i);
            if (face == null) {
                continue;
            }

            indexArray[j * 3] = tempArray[face.verticesIndex[0]];
            indexArray[j * 3 + 1] = tempArray[face.verticesIndex[1]];
            indexArray[j * 3 + 2] = tempArray[face.verticesIndex[2]];
            j++;
        }

        return new IndexedGLObject(vertexArray, normalArray, indexArray);
    }

    /**
     * 读取文件
     * @param st 解析器对象
     * @throws ParsingErrorException
     */
    void readFile(ObjectFileParser st) throws ParsingErrorException {

        st.getToken();
        while (st.ttype != ObjectFileParser.TT_EOF) {
            if (st.ttype == ObjectFileParser.TT_WORD) {
                if (st.sval.equals("v")) {
                    readVertex(st);
                } else if (st.sval.equals("f")) {
                    readFace(st);
                } else {
                    // st.skipToNextLine();
                    // throw new
                    // ParsingErrorException("Unrecognized token, line " +
                    // st.lineno());
                }
            }
            st.skipToNextLine();
            // Get next token
            st.getToken();
        }
    }

    /**
     * 读取顶点
     * @param st 解析器对象
     * @throws ParsingErrorException
     */
    void readVertex(ObjectFileParser st) throws ParsingErrorException {
        Vertex p = new Vertex();

        st.getNumber();
        p.position.x = (float) st.nval;
        st.getNumber();
        p.position.y = (float) st.nval;
        st.getNumber();
        p.position.z = (float) st.nval;

        st.skipToNextLine();

        // Add this vertex to the array
        vertexList.add(p);

//        if (BuildConfig.DEBUG) {
//            System.out.println("read vertex:" + p);
//        }
    } // End of readVertex

    void readFace(ObjectFileParser st) throws ParsingErrorException {
        ArrayList<Integer> points = new ArrayList<>();                         // 面的三个顶点的索引列表

        while (st.ttype != StreamTokenizer.TT_EOL) {
            st.getNumber();
            points.add((int) st.nval - 1);
//			st.getNumber();
            st.getToken();
            if(st.ttype==StreamTokenizer.TT_EOL)break;else st.pushBack();
        }

        assert (points.size() == 3);

        Face face = new Face(points.get(0), points.get(1), points.get(2));
        faceList.add(face);
        int faceIndex = faceList.size() - 1;

        // 读取面的同时，完善相关顶点的属性
        for (int i = 0; i < points.size(); i++) {
            Vertex vertex1 = vertexList.get(points.get(i));
            vertex1.adjacentFacesIndex.add(faceIndex);
//            vertex1.normal = face.normal;

            for (int j = i + 1; j < points.size(); j++) {
                Vertex vertex2 = vertexList.get(points.get(j));
                vertex1.adjacentVerticesIndex.add(points.get(j));
                vertex2.adjacentVerticesIndex.add(points.get(i));
            }
        }
        st.skipToNextLine();
    } // End of readFace

    /**
     * 设置基本路径
     * @param pathName 路径名
     */
    private void setBasePath(String pathName) {
        basePath = pathName;
        if (basePath == null || basePath == "")
            // 使用当前路径
            basePath = "." + java.io.File.separator;
        basePath = basePath.replace('/', java.io.File.separatorChar);
        basePath = basePath.replace('\\', java.io.File.separatorChar);
        if (!basePath.endsWith(java.io.File.separator))
            basePath = basePath + java.io.File.separator;
    }

    /**
     * 设置包含文件名的文件路径
     *d @param fileName 文件名
     */
    private void setBasePathFromFilename(String fileName) {
        if (fileName.lastIndexOf(java.io.File.separator) == -1) {
            // No path given - current directory
            setBasePath("." + java.io.File.separator);
        } else {
            setBasePath(fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
        }
    }

    private void computeAllCost() {
        for (Face f : faceList) {
            f.computeK();
        }
        for (Vertex v : vertexList) {
            v.computeQ();
        }
        for (int i = 0; i < vertexList.size(); i++) {
            Vertex v = vertexList.get(i);
            v.computeCostAndCandidate();
            costHeap.insert(i, v.cost);
        }
    }

    private void collapse(int vIndex) {
        // 待收缩的两个点
        Vertex v0 = vertexList.get(vIndex);
        Vertex v1 = vertexList.get(v0.candidateIndex);

        // 待收缩的两个点的索引
        final int v0Index = vIndex;
        final int v1Index = v0.candidateIndex;

        // 如果是孤立的点，则直接删除
        if (v0.cost == 0) {
            vertexList.set(v0Index, null);
            costHeap.delete(v0Index);
            return;
        }

        // 获取v0,v1相邻的面列表，并删除共有的面
        Set<Integer> fIndices = new HashSet<>();
        fIndices.addAll(v0.adjacentFacesIndex);
        fIndices.addAll(v1.adjacentFacesIndex);
        for (int vfIndex : v0.adjacentFacesIndex) {
            Face f = faceList.get(vfIndex);
            if (f.hasVertex(v1Index)) {
                // 删除共有的面，不使用remove() 方法是因为不能改变列表的索引
                faceList.set(vfIndex, null);
                fIndices.remove(vfIndex);

                // 更新另外一个顶点的面索引
                for (int i : f.verticesIndex) {
                    if (i != v0Index && i != v1Index) {
                        Vertex v = vertexList.get(i);
                        v.adjacentFacesIndex.remove(vfIndex);
                    }
                }
            }
        }

        // 获取v0,v1相邻的顶点列表，不包括v0和v1
        Set<Integer> vIndices = new HashSet<>();
        vIndices.addAll(v0.adjacentVerticesIndex);
        vIndices.addAll(v1.adjacentVerticesIndex);
        vIndices.remove(v0Index);
        vIndices.remove(v1Index);

        // 获取新顶点，为了缩减队列的长度，将新顶点放在原顶点v0的位置，删除v1（置为null）
        Vertex newVertex = new Vertex(v0.bestPosition);
        vertexList.set(v0Index, newVertex);
        vertexList.set(v1Index, null);

        // 更新相邻的面
        for (int i : fIndices) {
            Face f = faceList.get(i);
            f.replaceVertex(v1Index, v0Index);     // 只有v1相邻的面需要更新顶点位置
            f.update();                            // 所有的面都需要更新法向量和基础二次方误差矩阵
            newVertex.adjacentFacesIndex.add(i);
        }

        // 更新相邻的顶点
        for (int i : vIndices) {
            Vertex v = vertexList.get(i);
            v.adjacentVerticesIndex.remove(v1Index);
            v.adjacentVerticesIndex.add(v0Index);
            newVertex.adjacentVerticesIndex.add(i);
        }

        // 重新计算相关顶点的法向量、二次误差矩阵和消耗
        for (int i : vIndices) {
            vertexList.get(i).computeNormal();
            vertexList.get(i).computeQ();
        }
        newVertex.computeNormal();
        newVertex.computeQ();
        for (int i : vIndices) {
            Vertex v = vertexList.get(i);
            v.computeCostAndCandidate();
            costHeap.change(i, v.cost);
        }
        newVertex.computeCostAndCandidate();
        costHeap.insert(v0Index, newVertex.cost);
        costHeap.delete(v1Index);

        // 顶点数量减1
        vN = vN - 1;
        // 三角面数量减2
        fN = fN - 2;
    }


    public class Vertex {

        public Vector position;
        public Vector normal;

        public float[] Q;         // error quardic
        public int candidateIndex;            // 收缩的另一个端点的索引
        public float[] bestPosition;        // 代价最小的收缩顶点的位置
        public float cost;              // 最小收缩代价

        // 为了减少程序的复杂性，加快简化速度，不允许相邻点和相邻面的列表中出现重复或者无效的元素索引，牺牲空间为代价
        public Set<Integer> adjacentVerticesIndex = new HashSet<>();         // 相邻顶点的索引
        public Set<Integer> adjacentFacesIndex = new HashSet<>();            // 相邻面的索引

        public Vertex() {
            position = new Vector();
        }

        public Vertex(float[] a) {
            position = new Vector(a);
        }

//        public Vertex(float x, float y, float z) {
//            position = new Vector();
//            position.x = x;
//            position.y = y;
//            position.z = z;
//        }
//
//        public Vertex(Vector v) {
//            if (v == null) {
//                throw new NullPointerException();
//            }
//            position = v;
//        }

        /**
         * 使用平均向量法计算顶点的法向量
         */
        public void computeNormal() {
            float[] sum = new float[]{0, 0, 0};
            for (int fIndex : adjacentFacesIndex) {
                Face f = faceList.get(fIndex);
                sum[0] += f.normal.x;
                sum[1] += f.normal.y;
                sum[2] += f.normal.z;
            }
            sum[0] /= adjacentFacesIndex.size();
            sum[1] /= adjacentFacesIndex.size();
            sum[2] /= adjacentFacesIndex.size();
            normal = new Vector(sum);
        }

        /**
         * 计算每个顶点的二次方误差矩阵，必须先调用Face 的computeK() 方法计算面的基础二次方误差矩阵
         */
        public void computeQ() {
            // Q = sum(K)
            Q = new float[16];
            for (int fIndex : adjacentFacesIndex) {
                Face temp = faceList.get(fIndex);
                for (int i = 0; i < 16; i++) {
                    Q[i] += temp.K[i];
                }
            }
        }

        /**
         * 计算该点最小的折叠代价以及最佳的折叠位置
         */
        public void computeCostAndCandidate() {
            cost = Float.MAX_VALUE;
            if (adjacentVerticesIndex.size() == 0) {
                // 如果该点为孤立的点，则优先收缩
                cost = 0;
            }
            for (int vIndex : adjacentVerticesIndex) {
                float[] tempPosition = new float[4];
                float tempCost = computeCostCollapseTo(vertexList.get(vIndex), tempPosition);
                if (tempCost < cost) {
                    candidateIndex = vIndex;
                    cost = tempCost;
                    bestPosition = tempPosition;
                }
            }
        }

        private float computeCostCollapseTo(Vertex v, float[] newPosition) {
            // Qe = Q1 + Q2
            float[] Qe = new float[16];
            for (int i = 0; i < 16; i++) {
                Qe[i] = this.Q[i] + v.Q[i];
            }

            float[] t = Arrays.copyOf(Qe, Qe.length);
            t[3] = t[7] = t[11] = 0;
            t[15] = 1;
            float[] Qe_v = new float[16];
            float cost = Float.MAX_VALUE;
            if (Matrix.invertM(Qe_v, 0, t, 0)) {
                // 计算新顶点的位置
                Matrix.multiplyMV(newPosition, 0, Qe_v, 0, new float[]{0, 0, 0, 1}, 0);

                float[] temp = new float[4];
                Matrix.multiplyMV(temp, 0, Qe, 0, newPosition, 0);
                cost = MatrixHelper.dotProduct(newPosition, temp);
            } else{
                // 矩阵不可逆，则选择收缩点在两个端点或者中点
                // 收缩点选在此端点
                float[] v1 = this.position.toFloatArray();
                float[] temp = new float[4];
                Matrix.multiplyMV(temp, 0, Qe, 0, v1, 0);
                float cost1 = MatrixHelper.dotProduct(v1, temp);
                if (cost1 < cost) {
                    cost = cost1;
                    System.arraycopy(v1, 0, newPosition, 0, 4);
                }

                // 收缩点为另一端点
                float[] v2 = v.position.toFloatArray();
                Matrix.multiplyMV(temp, 0, Qe, 0, v2, 0);
                float cost2 = MatrixHelper.dotProduct(v2, temp);
                if (cost2 < cost) {
                    cost = cost2;
                    System.arraycopy(v2, 0, newPosition, 0, 4);
                }

                // 收缩点为中点
                float[] v3 = new float[]{
                        (this.position.x + v.position.x) / 2,
                        (this.position.y + v.position.y) / 2,
                        (this.position.z + v.position.z) / 2,
                        1};
                Matrix.multiplyMV(temp, 0, Qe, 0, v3, 0);
                float cost3 = MatrixHelper.dotProduct(v3, temp);
                if (cost3 < cost) {
                    cost = cost3;
                    System.arraycopy(v3, 0, newPosition, 0, 4);
                }
            }

/*            // 矩阵不可逆，则选择收缩点在两个端点或者中点
            // 收缩点选在此端点
            float[] v1 = this.position.toFloatArray();
            float[] temp = new float[4];
            Matrix.multiplyMV(temp, 0, Qe, 0, v1, 0);
            float cost1 = MatrixHelper.dotProduct(v1, temp);
            if (cost1 < cost) {
                cost = cost1;
                System.arraycopy(v1, 0, newPosition, 0, 4);
            }

            // 收缩点为另一端点
            float[] v2 = v.position.toFloatArray();
            Matrix.multiplyMV(temp, 0, Qe, 0, v2, 0);
            float cost2 = MatrixHelper.dotProduct(v2, temp);
            if (cost2 < cost) {
                cost = cost2;
                System.arraycopy(v2, 0, newPosition, 0, 4);
            }

            // 收缩点为中点
            float[] v3 = new float[]{
                    (this.position.x + v.position.x) / 2,
                    (this.position.y + v.position.y) / 2,
                    (this.position.z + v.position.z) / 2,
                    1};
            Matrix.multiplyMV(temp, 0, Qe, 0, v3, 0);
            float cost3 = MatrixHelper.dotProduct(v3, temp);
            if (cost3 < cost) {
                cost = cost3;
                System.arraycopy(v3, 0, newPosition, 0, 4);
            }*/

            return cost;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Position:" + position + "\n");
            stringBuilder.append("Normal:" + normal + "\n");
            stringBuilder.append("Neighbors: ");
            for (int vIndex : adjacentVerticesIndex) {
                stringBuilder.append(vIndex + " ");
            }
            stringBuilder.append("\nFaces: ");
            for (int fIndex : adjacentFacesIndex) {
                stringBuilder.append(fIndex + " ");
            }
            return stringBuilder.toString();
        }
    }

    private class Face {

        public final int[] verticesIndex = new int[3];
        public Vector normal;      // 面的单位法向量
        public float[] K;          // fundamental error quadric


        public Face(int vIndex1, int vIndex2, int vIndex3) {
            verticesIndex[0] = vIndex1;
            verticesIndex[1] = vIndex2;
            verticesIndex[2] = vIndex3;

            normal = computeNormal();
        }

        public boolean hasVertex(int vIndex) {
            for (int i : verticesIndex) {
                if (i == vIndex) {
                    return true;
                }
            }
            return false;
        }

        public boolean replaceVertex(int oldIndex, int newIndex) {
            for (int i = 0; i < 3; i++) {
                if (verticesIndex[i] == oldIndex) {
                    verticesIndex[i] = newIndex;
                    return true;
                }
            }
            return false;
        }

        /**
         * 更新法向量及二次方误差矩阵，当面的顶点改变时需调用此方法
         */
        public void update() {
            normal = computeNormal();
            computeK();
        }

        /**
         * 计算每个面的基础二次方误差矩阵
         */
        public void computeK() {
            // suppose the plane(face) is denoted as p = [a, b, c, d]^T
            Vertex oneVertex = vertexList.get(verticesIndex[0]);            // 任取面的一个顶点
            float d = - normal.dotProduct(oneVertex.position);
            float[] p = new float[]{normal.x, normal.y, normal.z, d};

            // K = p * p^T
            K = new float[16];
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    // 注意，这里为了与OpenGL 兼容，使用列向量的方式组织矩阵
                    K[i + j * 4] = p[i] * p[j];
                }
            }
        }

        private Vector computeNormal() {
            Vertex p1 = vertexList.get(verticesIndex[0]);
            Vertex p2 = vertexList.get(verticesIndex[1]);
            Vertex p3 = vertexList.get(verticesIndex[2]);
            return Vector.substract(p1.position, p2.position).crossProduct(Vector.substract
                    (p3.position, p2.position)).normalize();
        }

        @Override
        public String toString() {
            return "(" + verticesIndex[0] + "," + verticesIndex[1] + "," + verticesIndex[2] + ")";
        }
    }


    /**
     * 单元测试代码
     * @param args
     */
    public static final void main(String[] args) {
        String filename = "C:\\Users\\Administrator\\Desktop\\dinosaur.2k.obj";
        ObjectModel om = new ObjectModel(filename);
        System.out.println(om.basePath);
        for (Vertex v : om.vertexList) {
            System.out.println("Vertex:" + v);
        }
        for (Face f : om.faceList) {
            System.out.println("Face:" + f + " Normal:" + f.normal);
        }
    }
}
