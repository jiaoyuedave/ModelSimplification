package com.example.modelsimplification.objects;

import com.example.modelsimplification.GlobalState;
import com.example.modelsimplification.data.VertexArray;
import com.example.modelsimplification.programs.LoadedObjectShaderProgram;
import com.example.modelsimplification.util.LoggerConfig;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import static android.opengl.GLES20.*;

/**
 * Created by Administrator on 2017/4/18.
 */

public class LoadedObject extends GLObject {

    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int NORMAL_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private final VertexArray normalArray;
    private final IntBuffer indexArray;

    private LoadedObjectShaderProgram mProgram;

    private final int iCount;                              // 索引顶点的数目

    LoadedObject(float[] vertices, float[] normals, int[] indices) {
        super();

        // Initialize vertex array
        vertexArray = new VertexArray(vertices);

        // Initialize normal array
        normalArray = new VertexArray(normals);

        // Initialize index array
        iCount = indices.length;
        indexArray = IntBuffer.allocate(iCount).put(indices);
        indexArray.position(0);

        if (LoggerConfig.SYS_DEBUG) {
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
            System.out.println("Indices:--------------------");
            for (int i = 0; i < indices.length; i = i + 3) {
                System.out.println(indices[i] + " " + indices[i + 1] + " " + indices[i + 2]);
            }
        }
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

        glDrawElements(GL_TRIANGLES, iCount, GL_UNSIGNED_INT, indexArray);
    }


    public static final void main(String[] args) {
        String filename = "C:\\Users\\Administrator\\Desktop\\dinosaur.2k.obj";
        ObjectModel om = new ObjectModel(filename);
        LoadedObject loadedObject = om.toLoadedObject();
    }
}
