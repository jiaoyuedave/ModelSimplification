package com.example.modelsimplification.objects;

import com.example.modelsimplification.BuildConfig;
import com.example.modelsimplification.util.LoggerConfig;

import java.nio.IntBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glDrawElements;

/**
 * 使用索引数组方法绘制的OpenGL ES 对象
 * Created by Jiao Yue on 2017/5/7.
 */

public class IndexedGLObject extends GLObject {

    protected final IntBuffer indexArray;                    // 索引数组

    private final int iCount;                              // 索引顶点的数目

    public IndexedGLObject(float[] vertices, float[] normals, int[] indices) {
        super(vertices, normals);

        // Initialize index array
        iCount = indices.length;
        indexArray = IntBuffer.allocate(iCount).put(indices);
        indexArray.position(0);

        if (BuildConfig.DEBUG && LoggerConfig.SYS_DEBUG) {
            System.out.println("Indices:--------------------");
            for (int i = 0; i < indices.length; i = i + 3) {
                System.out.println(indices[i] + " " + indices[i + 1] + " " + indices[i + 2]);
            }
            System.out.println();
        }
    }

    public void draw() {
        update();
        vertexArray.setVertexAttribPointer(0, mProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);
        normalArray.setVertexAttribPointer(0, mProgram.getNormalLocation(),
                NORMAL_COMPONENT_COUNT, 0);

        glDrawElements(GL_TRIANGLES, iCount, GL_UNSIGNED_INT, indexArray);
    }


    public static final void main(String[] args) {
//        String filename = "C:\\Users\\Administrator\\Desktop\\dinosaur.2k.obj";
        String filename = "C:\\Users\\dell\\Desktop\\dinosaur.2k.obj";
        ObjectModel om = new ObjectModel(filename);
        IndexedGLObject indexedGLObject = om.toIndexedGLObject();
    }
}
