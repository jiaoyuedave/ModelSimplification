package com.example.modelsimplification;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class ObjectModel {

    private static final String TAG = "ObjectModel";

    private List<Vertex> vertexList = new ArrayList<>();          // 顶点列表
    private List<Face> faceList = new ArrayList<>();              // 三角面列表

    private String basePath;                                      // 基准路径

    public ObjectModel(String fileName) {
        try {
            loadFromObjFile(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从.obj 文件加载三维模型
     *
     * @param fileName 文件路径
     * @return this
     * @throws FileNotFoundException
     */
    public ObjectModel loadFromObjFile(String fileName) throws FileNotFoundException {
        setBasePathFromFilename(fileName);

        Reader reader = new BufferedReader(new FileReader(fileName));
        return load(reader);
    }

    public ObjectModel load(Reader reader) throws FileNotFoundException {
        // ObjectFileParser does lexical analysis
        ObjectFileParser st = new ObjectFileParser(reader);

        readFile(st);
        return this;
    }

    /**
     * 读取文件
     *
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
     *
     * @param st 解析器对象
     * @throws ParsingErrorException
     */
    void readVertex(ObjectFileParser st) throws ParsingErrorException {
        Vertex p = new Vertex();

        st.getNumber();
        p.x = (float) st.nval;
        st.getNumber();
        p.y = (float) st.nval;
        st.getNumber();
        p.z = (float) st.nval;

        st.skipToNextLine();

        // Add this vertex to the array
        vertexList.add(p);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "readVertex: " + p);
        }
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

        faceList.add(new Face(points.get(0), points.get(1), points.get(2)));
        int faceIndex = faceList.size() - 1;

        for (int i = 0; i < points.size(); i++) {
            Vertex vertex1 = vertexList.get(points.get(i));
            vertex1.addFace(faceIndex);

            for (int j = i + 1; j < points.size(); j++) {
                Vertex vertex2 = vertexList.get(points.get(j));
                vertex1.addNeighbor(points.get(j));
                vertex2.addNeighbor(points.get(i));
            }
        }
        st.skipToNextLine();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "readFace: " + faceList.get(faceIndex));
        }
    } // End of readFace

    /**
     * 设置基本路径
     *
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
     *
     * @param fileName 文件名
     */
    private void setBasePathFromFilename(String fileName) {
        if (fileName.lastIndexOf(java.io.File.separator) == -1) {
            // No path given - current directory
            setBasePath("." + java.io.File.separator);
        } else {
            setBasePath(fileName.substring(0, fileName.lastIndexOf(java.io.File.separator)));
        }
    }


    public static class Vertex {

        public float x;
        public float y;
        public float z;

        private List<Integer> adjacentVerticesIndex = new ArrayList<>();         // 相邻顶点的索引
        private List<Integer> adjacentFacesIndex = new ArrayList<>();            // 相邻面的索引

        public Vertex() {}

        public Vertex(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public void addNeighbor(int vertexIndex) {
            adjacentVerticesIndex.add(vertexIndex);
        }

        public void addFace(int faceIndex) {
            adjacentFacesIndex.add(faceIndex);
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + "," + z + ")";
        }
    }

//    public static class Edge {
//        private Vector3 v1;
//        private Vector3 v2;
//    }

    public static class Face {
        private int[] verticesIndex = new int[3];

        public Face(int vIndex1, int vIndex2, int vIndex3) {
            verticesIndex[0] = vIndex1;
            verticesIndex[1] = vIndex2;
            verticesIndex[2] = vIndex3;
        }

        @Override
        public String toString() {
            return "(" + verticesIndex[0] + "," + verticesIndex[1] + "," + verticesIndex[2] + ")";
        }
    }


    public static final void main(String[] args) {
        String filename = args[0];
        ObjectModel om = new ObjectModel(filename);
    }
}
