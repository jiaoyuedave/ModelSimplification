package com.example.modelsimplification.programs;

import android.content.Context;

import com.example.modelsimplification.util.ShaderHelper;
import com.example.modelsimplification.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by lenovo on 2017/1/9.
 */

abstract public class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_COLOR = "u_Color";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        // Compile the shader and link the program
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        );
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program
        glUseProgram(program);
    }
}
