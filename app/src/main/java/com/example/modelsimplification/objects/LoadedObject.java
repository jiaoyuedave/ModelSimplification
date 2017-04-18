package com.example.modelsimplification.objects;

import com.example.modelsimplification.data.VertexArray;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;

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
    }

    public void bindData(LoadedObjectShaderProgram program) {
        vertexArray.setVertexAttribPointer(0, program.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES, iCount, GL_UNSIGNED_INT, indexArray);
    }
}
