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
    private final int uMatrixLocation;
    private final int uColorLocation;

    // Attribute locations
    private final int aPositionLocation;

    public LoadedObjectShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader);

        // Retrieve uniform locations for shader program
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);

        // Retrieve attribute loactions for shader program
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float[] matrix, float r, float g, float b) {
        // Pass the matrix to the vertex shader
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
        // Pass the color to the fragment shader
        glUniform4f(uColorLocation, r, g, b, 1f);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
