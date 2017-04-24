package com.example.modelsimplification.programs;

import android.content.Context;

import static android.opengl.GLES20.*;

import com.example.modelsimplification.R;

/**
 * Created by Administrator on 2017/4/18.
 */

/**
 * 加载物体的着色器类，封装了simple_vertex_shader 和simple_fragment_shader
 */
public class LoadedObjectShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMVPMatrixLocation;
    private final int uMMatrixLocation;
//    private final int uLightLocLocation;
    private final int uLightDirectionLocation;
    private final int uCameraLocation;
    private final int uColorLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aNormalLocation;

    public LoadedObjectShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        // Retrieve uniform locations for shader program
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVPMATRIX);
        uMMatrixLocation = glGetUniformLocation(program, U_MMATRIX);
//        uLightLocLocation = glGetUniformLocation(program, U_LIGHT_LOCATION);
        uLightDirectionLocation = glGetUniformLocation(program, U_LIGHT_DIRECTION);
        uCameraLocation = glGetUniformLocation(program, U_CAMERA);
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        // Retrieve attribute loactions for shader program
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }
/*
    public void setUniforms(float[] MVPMatrix, float[] MMatrix, float[] lightLocation, float[]
            camera, float r, float g, float b) {
        // Pass the uniforms to the vertex shader
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
        glUniformMatrix4fv(uMMatrixLocation, 1, false, MMatrix, 0);
        glUniform3fv(uLightLocLocation, 0, lightLocation, 0);
        glUniform3fv(uCameraLocation, 1, camera, 0);

        // Pass the uniforms to the fragment shader
        glUniform4f(uColorLocation, r, g, b, 1f);
    }
    */

    public void setUniforms(float[] MVPMatrix, float[] MMatrix, float[] lightDirection, float[]
            camera, float r, float g, float b) {
        // Pass the uniforms to the vertex shader
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);
        glUniformMatrix4fv(uMMatrixLocation, 1, false, MMatrix, 0);
        glUniform3fv(uLightDirectionLocation, 0, lightDirection, 0);
        glUniform3fv(uCameraLocation, 1, camera, 0);

        // Pass the uniforms to the fragment shader
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getNormalLocation() {
        return aNormalLocation;
    }
}
