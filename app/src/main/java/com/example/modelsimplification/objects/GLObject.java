package com.example.modelsimplification.objects;

import android.opengl.Matrix;
import android.util.Log;

import com.example.modelsimplification.BuildConfig;
import com.example.modelsimplification.GlobalState;
import com.example.modelsimplification.data.MatrixStack;
import com.example.modelsimplification.data.VertexArray;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;
import com.example.modelsimplification.util.LoggerConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.*;

/**
 * 使用顶点数组方法绘制的OpenGL ES 对象
 * Created by Jiao Yue on 2017/4/24.
 */

public class GLObject {

    private static final String TAG = "GLObject";

    protected static final int POSITION_COMPONENT_COUNT = 3;
    protected static final int NORMAL_COMPONENT_COUNT = 3;

    protected final VertexArray vertexArray;             // 顶点数组
    protected final VertexArray normalArray;             // 法向量数组

    protected LoadedObjectShaderProgram mProgram;

    private int vCount;                            // 顶点数目

    /**
     * 模型矩阵，用于保存物体的位置状态
     */
    protected float[] MMatrix;
    /**
     * 物体颜色
     */
    protected float[] color;

    protected MatrixStack matrixStack = new MatrixStack();

    public GLObject(float[] vertices, float[] normals) {
        MMatrix = new float[16];
        Matrix.setRotateM(MMatrix, 0, 0, 1, 0, 0);
        color = new float[]{1, 1, 1, 1};

        // Initialize vertex array
        vCount = vertices.length / 3;
        vertexArray = new VertexArray(vertices);

        // Initialize normal array
        normalArray = new VertexArray(normals);

        if (BuildConfig.DEBUG && LoggerConfig.SYS_DEBUG) {
            System.out.println("Vertices:------------------");
            for (int i = 0; i < vertices.length; i = i + 3) {
                System.out.println(vertices[i] + " " + vertices[i + 1] + " " + vertices[i + 2]);
            }
            System.out.println();
            System.out.println("Normals:--------------------");
            for (int i = 0; i < normals.length; i = i + 3) {
                System.out.println(normals[i] + " " + normals[i + 1] + " " + normals[i + 2]);
            }
            System.out.println();
        }
    }

    public GLObject(InputStream is) {
        MMatrix = new float[16];
        Matrix.setRotateM(MMatrix, 0, 0, 1, 0, 0);
        color = new float[]{1, 1, 1, 1};

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        float[] vertices = new float[5000];
        float[] normals = new float[5000];
        try {
            String str;
            while ((str = reader.readLine()) != null) {
                Log.d(TAG, "readLine:" + str);
                if (vCount + 8 >= vertices.length) {
                    // 数组长度加倍
                    float[] tempv = new float[vertices.length * 2];
                    float[] tempn = new float[normals.length * 2];
                    for (int i = 0; i < vCount; i++) {
                        tempv[i] = vertices[i];
                        tempn[i] = normals[i];
                    }
                    vertices = tempv;
                    normals = tempn;
                }

                String[] ss = str.split(" ");
                vertices[vCount    ] = Float.parseFloat(ss[0]);
                vertices[vCount + 1] = Float.parseFloat(ss[1]);
                vertices[vCount + 2] = Float.parseFloat(ss[2]);
                vertices[vCount + 3] = Float.parseFloat(ss[3]);
                vertices[vCount + 4] = Float.parseFloat(ss[4]);
                vertices[vCount + 5] = Float.parseFloat(ss[5]);
                vertices[vCount + 6] = Float.parseFloat(ss[6]);
                vertices[vCount + 7] = Float.parseFloat(ss[7]);
                vertices[vCount + 8] = Float.parseFloat(ss[8]);

                normals[vCount    ] = Float.parseFloat(ss[9]);
                normals[vCount + 1] = Float.parseFloat(ss[10]);
                normals[vCount + 2] = Float.parseFloat(ss[11]);
                normals[vCount + 3] = Float.parseFloat(ss[9]);
                normals[vCount + 4] = Float.parseFloat(ss[10]);
                normals[vCount + 5] = Float.parseFloat(ss[11]);
                normals[vCount + 6] = Float.parseFloat(ss[9]);
                normals[vCount + 7] = Float.parseFloat(ss[10]);
                normals[vCount + 8] = Float.parseFloat(ss[11]);

                vCount  = vCount + 9;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        vCount  = vCount / 3;

        // Initialize vertex array
        vertexArray = new VertexArray(vertices);

        // Initialize normal array
        normalArray = new VertexArray(normals);
    }

    public void bindProgram(LoadedObjectShaderProgram program) {
        mProgram = program;
        mProgram.useProgram();
    }

    public void update() {
        mProgram.setUniforms(GlobalState.getFinalMatrix(MMatrix), MMatrix, GlobalState
                .getLightDirection(), GlobalState.getCameraLocation(), color);
    }

    public void draw() {
        update();
        vertexArray.setVertexAttribPointer(0, mProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
        normalArray.setVertexAttribPointer(0, mProgram.getNormalLocation(),
                NORMAL_COMPONENT_COUNT, 0);

        glDrawArrays(GL_TRIANGLES, 0, vCount);
    }

    public void setColor(float r, float g, float b, float a) {
        color[0] = r;
        color[1] = g;
        color[2] = b;
        color[3] = a;
    }

    public float[] getColor() {
        return color;
    }

    /**
     * 重置物体的位置
     */
    public void resetState() {
        Matrix.setRotateM(MMatrix, 0, 0, 1, 0, 0);
    }

    /**
     * 将物体沿x、y、z 轴平移
     *
     * @param x 沿x轴平移的距离
     * @param y 沿y轴平移的距离
     * @param z 沿z轴平移的距离
     */
    public void translate(float x, float y, float z) {
        Matrix.translateM(MMatrix, 0, x, y, z);
    }

    /**
     * 将物体沿x、y、z 轴旋转angle 角度
     *
     * @param angle 旋转的角度
     * @param x x轴分量
     * @param y y轴分量
     * @param z z轴分量
     */
    public void rotate(float angle, float x, float y, float z) {
        Matrix.rotateM(MMatrix, 0, angle, x, y, z);
    }

    /**
     * 将物体沿x、y、z 轴进行缩放
     *
     * @param x x轴缩放因子
     * @param y y轴缩放因子
     * @param z z轴缩放因子
     */
    public void scale(float x, float y, float z) {
        Matrix.scaleM(MMatrix, 0, x, y, z);
    }

    /**
     * 入栈保存物体的位置状态，与popState() 方法结合，用于重新绘制物体的位置
     */
    public void pushState() {
        matrixStack.push(MMatrix);
    }

    /**
     * 将物体的位置状态出栈，与pushState() 方法结合， 用于重新绘制物体的位置
     */
    public void popState() {
        MMatrix = matrixStack.pop();
    }

    /**
     * 获取物体的模型矩阵
     *
     * @return 模型矩阵
     */
    public float[] getMMatrix() {
        return MMatrix;
    }
}
