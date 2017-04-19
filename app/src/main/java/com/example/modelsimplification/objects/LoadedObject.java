package com.example.modelsimplification.objects;

import com.example.modelsimplification.data.VertexArray;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;
import com.example.modelsimplification.util.LoggerConfig;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Administrator on 2017/4/18.
 */

public class LoadedObject {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private final IntBuffer indexArray;

    private final int iCount;                              // 索引顶点的数目

    LoadedObject(float[] vertex, int[] index) {
        // Initialize vertex array
        vertexArray = new VertexArray(vertex);

        // Initialize index array
        iCount = index.length;
        indexArray = IntBuffer.allocate(iCount).put(index);
        indexArray.position(0);

        if (LoggerConfig.SYS_DEBUG) {
            System.out.println("Vertices:------------------");
            for (int i = 0; i < vertex.length; i = i + 3) {
                System.out.println(vertex[i] + " " + vertex[i + 1] + " " + vertex[i + 2]);
            }
            System.out.println();
            System.out.println("Indices:--------------------");
            for (int i = 0; i < index.length; i = i + 3) {
                System.out.println(index[i] + " " + index[i + 1] + " " + index[i + 2]);
            }
        }
    }

    public void bindData(LoadedObjectShaderProgram program) {
        vertexArray.setVertexAttribPointer(0, program.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, iCount, GL_UNSIGNED_INT, indexArray);
    }


    public static final void main(String[] args) {
        String filename = "C:\\Users\\Administrator\\Desktop\\dinosaur.2k.obj";
        ObjectModel om = new ObjectModel(filename);
        LoadedObject loadedObject = om.toLoadedObject();
    }
}
