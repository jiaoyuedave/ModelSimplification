package com.example.modelsimplification.util;

import android.util.Log;

import com.example.modelsimplification.BuildConfig;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

/**
 * Created by Administrator on 2017/1/5.
 */

public class ShaderHelper {

    private static final String TAG = "ShaderHelper";

    /**
     * 编译顶点着色器
     *
     * @param shaderCode 着色器代码文本
     * @return 着色器对象ID
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片元着色器
     *
     * @param shaderCode 着色器代码文本
     * @return 着色器对象ID
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);
    }

    private static int compileShader(int type, String shaderCode) {
        // Create a new shader object
        final int shaderObjectId = glCreateShader(type);

        // Check if the shader object is not successfully created
        if (shaderObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Could not create new shader.");
            }
            return 0;
        }

        // Connect the shader object and the shaderCode
        glShaderSource(shaderObjectId, shaderCode);

        // Compile
        glCompileShader(shaderObjectId);

        // Get the compile status of the shader object
        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);

        // Get the shader log
        if (BuildConfig.DEBUG) {
            // Print the shader info log to the Android log output.
            Log.d(TAG, "Results of compiling source:\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
        }

        // Check if the compiling succeed
        if (compileStatus[0] == 0) {
            // If it failed, delete the shader object.
            glDeleteShader(shaderObjectId);

            if (BuildConfig.DEBUG) {
                Log.w(TAG, "compilation of shader failed");
            }
            return 0;
        }

        return shaderObjectId;
    }

    /**
     * 链接着色器，生成OpengGL 程序
     *
     * @param vertexShaderId 顶点着色器ID
     * @param fragmentShaderId 片元着色器ID
     * @return OpenGL程序ID
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // Create a new program object
        final int programObjectId = glCreateProgram();

        // Check if the program object is successfully created
        if (programObjectId == 0) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Could not create new program");
            }
            return 0;
        }

        // Attach shader to the program
        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        // Link the program
        glLinkProgram(programObjectId);

        // Check the linking status
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);

        // Get the program log
        if (BuildConfig.DEBUG) {
            // Print the program info log to the Android log output
            Log.d(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programObjectId));
        }

        // Check if the linking succeed
        if (linkStatus[0] == 0) {
            // If it failed, delete the program object
            glDeleteProgram(programObjectId);
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Linking of program failed.");
            }
            return 0;
        }

        return programObjectId;
    }

    /**
     * 验证程序，判断程序是否是低效的或者无法运行的，一般在调试模式下使用，打印出相关信息
     *
     * @param programObjectId OpenGL程序ID
     * @return 验证是否成功
     */
    public static boolean validateProgram(int programObjectId) {
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        Log.d(TAG, "Results of validating program: " + validateStatus[0] + "\nlog:" + glGetProgramInfoLog(programObjectId));

        return validateStatus[0] != 0;
    }

    /**
     * 生成OpenGL程序
     *
     * @param vertexShaderSource 顶点着色器代码文本
     * @param fragmentShaderSource 片元着色器代码文本
     * @return openGL程序对象ID
     */
    public static int buildProgram(String vertexShaderSource, String fragmentShaderSource) {
        int program;

        // Compile the shaders
        int vertexShader = compileVertexShader(vertexShaderSource);
        int fragmentShader = compileFragmentShader(fragmentShaderSource);

        // Link them into a shader program
        program = linkProgram(vertexShader, fragmentShader);

        if (BuildConfig.DEBUG) {
            validateProgram(program);
        }

        return program;
    }
}
